package com.ibs.hrmioneemployee.activities.drawer_activities

import android.Manifest
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.downloader.OnCancelListener
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.github.gcacace.signaturepad.views.SignaturePad
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.ActivityCompanyPolicy2Binding
import com.ibs.hrmioneemployee.models.api_models.company_policy.CompanyPolicyResponse
import com.ibs.hrmioneemployee.models.api_models.signature.GetSignatureResponse
import com.ibs.hrmioneemployee.models.api_models.signature.PostSignatureResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.RealPathUtil
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rajat.pdfviewer.PdfViewerActivity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class CompanyPolicyActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityCompanyPolicy2Binding
    private lateinit var dataLoading: DataLoading
    private lateinit var sp: SharedPreferences
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private lateinit var Authorization: String
    private var employeeId: Int = 0
    private var path: String? = null
    private var companyPolicyUrl: String? = null
    private lateinit var bitmap: Bitmap
    private lateinit var signaturePad: SignaturePad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompanyPolicy2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val alertDialog: AlertDialog = AlertDialog.Builder(this, R.style.SignatureDialog).create()
        val alertView: View = LayoutInflater.from(this).inflate(R.layout.signature_dialog_layout, null)

        val ok = alertView.findViewById<Button>(R.id.ok)
        val edit = alertView.findViewById<Button>(R.id.edit)
        signaturePad = alertView.findViewById<Button>(R.id.signaturePad) as SignaturePad

        binding.viewSign.setOnClickListener {
            alertDialog.setView(alertView)
            alertDialog.show()

            if (signaturePad.isEmpty) {
                callGetSignatureApi()
            }

            ok.setOnClickListener {
                alertDialog.dismiss()
            }
            edit.setOnClickListener {
                signaturePad.clearView()
            }
        }

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

//        binding.clearSign.setOnClickListener {
//            binding.signaturePad.clearView()
//        }

        dataLoading = DataLoading(this)
        sharedPreferenceClass = SharedPreferenceClass(this)
        Authorization = sharedPreferenceClass.getLoginToken()

        sp = this.getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, MODE_PRIVATE)
        employeeId = sp.getInt("UserId", -1)

        callCompanyPolicyApi()
        callGetSignatureApi()

        binding.companyPolicyImage.setOnClickListener {
            launchPdf()
        }


        binding.downloadCompanyPolicy.setOnClickListener {
            PRDownloader.initialize(this)
            checkUserPermission()
        }

        binding.submitText.setOnClickListener {
            callPostSignatureApi()
        }

//        binding.clickHereToEdit.setOnClickListener {
//            binding.signaturePad.clearView()
//            binding.signaturePad.setBackgroundResource(R.drawable.signature_pad_background)
//            binding.clearSign.visibility = View.VISIBLE
//        }
    }

    private fun launchPdf() {
        startActivity(
            PdfViewerActivity.launchPdfFromUrl(
                context = this, companyPolicyUrl,
                pdfTitle = "Company Policy", directoryName = "dir", enableDownload = true
            )
        )
    }

    private fun callPostSignatureApi() {

        if (signaturePad.isEmpty) {
            Toast.makeText(this, "Please sign your name", Toast.LENGTH_SHORT).show()
            return
        }

        var isAgree = 0
        if (binding.checkPrivacyPolicy.isChecked && binding.checkTermsConditions.isChecked) {
            isAgree = 1
        }

        val bitmap: Bitmap = signaturePad.signatureBitmap
        val uri: Uri = getImageUri(bitmap)
        path = RealPathUtil.getPath(this, uri)

        //  for image only
        val file = File(path!!)
//        val hello = file.canRead()
//        println("this is --------------------- $hello")
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val sign: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestFile)

        dataLoading.startLoading()
        val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<PostSignatureResponse> = apiServices.postSignature(Authorization, sign, employeeId, isAgree)

        call.enqueue(object : Callback<PostSignatureResponse> {
            override fun onResponse(
                call: Call<PostSignatureResponse>,
                response: Response<PostSignatureResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        Toast.makeText(
                            this@CompanyPolicyActivity2,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT).show()
                        signaturePad.signatureBitmap = signaturePad.signatureBitmap
//                        clearSign.visibility = View.INVISIBLE
                    }
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(
                        this@CompanyPolicyActivity2,
                        jObjError.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<PostSignatureResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@CompanyPolicyActivity2, "Error $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getImageUri(inImage: Bitmap): Uri {

        val tempFile = File.createTempFile("EmployeeSignature", ".png")
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(tempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()
        return Uri.fromFile(tempFile)
    }

    private fun checkUserPermission() {

        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object :
            MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    if (companyPolicyUrl != null) {
                        downloadPdf()
                    }
                } else {
                    Toast.makeText(
                        this@CompanyPolicyActivity2,
                        "Please allow permissions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest?>?,
                token: PermissionToken?
            ) {
                token?.continuePermissionRequest()
            }
        }).check()
    }

    private fun downloadPdf() {

        dataLoading.startLoading()

        val file: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        PRDownloader.download(
            companyPolicyUrl, file.path, URLUtil.guessFileName(companyPolicyUrl, null, null)
        )
            .build()
            .setOnStartOrResumeListener { }
            .setOnPauseListener {
                dataLoading.hideLoading()
                Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show()
            }
            .setOnCancelListener(object : OnCancelListener {
                override fun onCancel() {
                    dataLoading.hideLoading()
                    Toast.makeText(this@CompanyPolicyActivity2, "Cancelled", Toast.LENGTH_SHORT)
                        .show()
                }
            })
//            .setOnProgressListener(object : OnProgressListener {
//                @SuppressLint("SetTextI18n")
//                override fun onProgress(progress: Progress?) {
//                    val percentage: Long = (progress!!.currentBytes*100/progress.totalBytes)
//                }
//            })
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    dataLoading.hideLoading()
                    Toast.makeText(
                        this@CompanyPolicyActivity2,
                        "Download completed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: com.downloader.Error?) {
                    dataLoading.hideLoading()
                    Toast.makeText(this@CompanyPolicyActivity2, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun callCompanyPolicyApi() {

        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<CompanyPolicyResponse> = apiServices.companyPolicy(Authorization, employeeId)

        call.enqueue(object : Callback<CompanyPolicyResponse> {
            override fun onResponse(
                call: Call<CompanyPolicyResponse>,
                response: Response<CompanyPolicyResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        companyPolicyUrl = response.body()!!.result
                    }
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(
                        this@CompanyPolicyActivity2,
                        jObjError.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<CompanyPolicyResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@CompanyPolicyActivity2, "Error $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callGetSignatureApi() {

        dataLoading.startLoading()
        val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<GetSignatureResponse> = apiServices.getSignature(Authorization, employeeId)

        call.enqueue(object : Callback<GetSignatureResponse> {
            override fun onResponse(
                call: Call<GetSignatureResponse>,
                response: Response<GetSignatureResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        convertUrlToBitmap(response.body()!!.result.signature)
                        if (response.body()!!.result.isTermsAccepted == 1) {
                            binding.checkPrivacyPolicy.isChecked = true
                            binding.checkTermsConditions.isChecked = true
                        }
                    }
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(
                        this@CompanyPolicyActivity2,
                        jObjError.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                    Toast.makeText(
                        this@CompanyPolicyActivity2,
                        jObjError.getString("error"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<GetSignatureResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@CompanyPolicyActivity2, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun convertUrlToBitmap(src: String?) {

        Thread {
            //Do some Network Request
            try {
                val url = URL(src)
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: IOException) {
                null
            }
            runOnUiThread {
                //Update UI
                signaturePad.signatureBitmap = bitmap
            }
        }.start()
    }
}