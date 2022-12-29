package com.ibs.hrmioneemployee.activities.drawer_activities

import android.Manifest
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.downloader.OnCancelListener
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.ibs.hrmioneemployee.models.api_models.company_policy.CompanyPolicyResponse
import com.ibs.hrmioneemployee.models.api_models.signature.GetSignatureResponse
import com.ibs.hrmioneemployee.models.api_models.signature.PostSignatureResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.RealPathUtil
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.ActivityCompanyPolicyBinding
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
import java.io.*
import java.net.URL

class CompanyPolicyActivity : AppCompatActivity(){

    private lateinit var binding: ActivityCompanyPolicyBinding
    private lateinit var dataLoading: DataLoading
    private lateinit var sp: SharedPreferences
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private lateinit var Authorization: String
    private var employeeId: Int = 0
    private var path: String? = null
    private var companyPolicyUrl: String? = null
    private lateinit var bitmap: Bitmap
//    var pageNumber = 0
//    var pdfFileName: String? = null

    // for PDF view.
//    var pdfView: PDFView? = null

    // url of our PDF file.
//    var pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
//    var pdfUrl = "http://18.218.231.50/hr/privacyPolicy/Company Policy-5.pdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompanyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // initializing our pdf view.
//        RetrivePDFfromUrl().execute(pdfurl)
//        RetrievePDFFromURL(binding.pdfView).execute(pdfUrl)

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        binding.clearSign.setOnClickListener {
            binding.signaturePad.clearView()
        }

        dataLoading = DataLoading(this)
        sharedPreferenceClass = SharedPreferenceClass(this)
        Authorization = sharedPreferenceClass.getLoginToken()

        sp = this.getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, MODE_PRIVATE)
        employeeId = sp.getInt("UserId", -1)

        callCompanyPolicyApi()
        callGetSignatureApi()

        binding.companyPolicyImage.setOnClickListener {

//            startActivity(PdfViewerActivity.
//            launchPdfFromUrl(this, companyPolicyUrl,
//                "Company Policy",
//                "dir", enableDownload = false))
            launchPdf()
        }

        binding.downloadCompanyPolicy.setOnClickListener {
            PRDownloader.initialize(this)
            checkUserPermission()
        }

        binding.submitText.setOnClickListener {
            callPostSignatureApi()
        }

        binding.clickHereToEdit.setOnClickListener {
            binding.signaturePad.clearView()
            binding.signaturePad.setBackgroundResource(R.drawable.signature_pad_background)
            binding.clearSign.visibility = View.VISIBLE
        }
    }

    private fun launchPdf() {
        startActivity(PdfViewerActivity.launchPdfFromUrl(
                context = this, companyPolicyUrl,
                pdfTitle = "Company Policy", directoryName = "dir", enableDownload = true
            )
        )
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
                    Toast.makeText(this@CompanyPolicyActivity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                    Toast.makeText(
                        this@CompanyPolicyActivity,
                        jObjError.getString("error"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<GetSignatureResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@CompanyPolicyActivity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callPostSignatureApi() {

        if (binding.signaturePad.isEmpty) {
            Toast.makeText(this, "Please sign your name", Toast.LENGTH_SHORT).show()
            return
        }

        var isAgree = 0
        if (binding.checkPrivacyPolicy.isChecked && binding.checkTermsConditions.isChecked) {
            isAgree = 1
        }

        val bitmap: Bitmap = binding.signaturePad.signatureBitmap
        val uri: Uri = getImageUri(bitmap)
        path = RealPathUtil.getPath(this, uri)

        //  for image only
        val file = File(path!!)
//        val hello = file.canRead()
//        println("this is --------------------- $hello")
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val sign: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        dataLoading.startLoading()
        val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<PostSignatureResponse> =
            apiServices.postSignature(Authorization, sign, employeeId, isAgree)

        call.enqueue(object : Callback<PostSignatureResponse> {
            override fun onResponse(
                call: Call<PostSignatureResponse>,
                response: Response<PostSignatureResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        Toast.makeText(
                            this@CompanyPolicyActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.signaturePad.signatureBitmap = binding.signaturePad.signatureBitmap
                        binding.clearSign.visibility = View.INVISIBLE
                    }
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(
                        this@CompanyPolicyActivity,
                        jObjError.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<PostSignatureResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@CompanyPolicyActivity, "Error $t", Toast.LENGTH_SHORT).show()
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

//    private fun displayFromAsset() {
//
//        val fileUri = URI(companyPolicyUrl)
//        val file = File(fileUri)

//        val uri:Uri =  File(companyPolicyUrl).toUri()

//        val uri: Uri = Uri.fromFile(File(companyPolicyUrl))

//        val fileUri = Uri.parse(companyPolicyUrl)
//        val startIndex = fileUri.toString().lastIndexOf('/')
//        val fileName = fileUri.toString().substring(startIndex + 1)

//        binding.pdfView.fromUri(uri)
//
//        binding.pdfView.fromAsset(fileName)
//            .defaultPage(pageNumber)
//            .enableSwipe(true)
//            .swipeHorizontal(false)
//            .onPageChange(this)
//            .enableAnnotationRendering(true)
//            .onLoad(this)
//            .scrollHandle(DefaultScrollHandle(this))
//            .load()
//    }

    private fun checkUserPermission() {

        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object :
            MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    if (companyPolicyUrl != null) {
                        downloadPdf()
                    }
                } else {
                    Toast.makeText(this@CompanyPolicyActivity, "Please allow permissions", Toast.LENGTH_SHORT).show()
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

        val file: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

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
                    Toast.makeText(this@CompanyPolicyActivity, "Cancelled", Toast.LENGTH_SHORT)
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
                        this@CompanyPolicyActivity,
                        "Download completed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: com.downloader.Error?) {
                    dataLoading.hideLoading()
                    Toast.makeText(this@CompanyPolicyActivity, "Error", Toast.LENGTH_SHORT).show()
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
//                        displayFromAsset()
//                        RetrievePDFFromURL(binding.pdfView).execute(pdfUrl)
                    }
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(
                        this@CompanyPolicyActivity,
                        jObjError.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<CompanyPolicyResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@CompanyPolicyActivity, "Error $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    override fun loadComplete(nbPages: Int) {
//        val meta = pdfView!!.documentMeta
//        printBookmarksTree(pdfView!!.tableOfContents, "-")
//    }
//
//    private fun printBookmarksTree(
//        tableOfContents: List<com.shockwave.pdfium.PdfDocument.Bookmark>,
//        s: String
//    ) {
//        for (b in tableOfContents) {
//            if (b.hasChildren()) {
//                printBookmarksTree(b.children, "$s-")
//            }
//        }
//    }
//
//    override fun onPageChanged(page: Int, pageCount: Int) {
//        pageNumber = page
//        title = String.format("%s %s / %s", pdfFileName, page + 1, pageCount)
//    }

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
                binding.signaturePad.signatureBitmap = bitmap
            }
        }.start()
    }

//    // create an async task class for loading pdf file from URL.
//    inner class RetrivePDFfromUrl : AsyncTask<String?, Void?, InputStream?>() {
//        override fun doInBackground(vararg p0: String?): InputStream? {
//            // we are using inputstream
//            // for getting out PDF.
//            var inputStream: InputStream? = null
//            try {
//                val url = URL(strings[0])
//                // below is the step where we are
//                // creating our connection.
//                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
//                if (urlConnection.responseCode === 200) {
//                    // response is success.
//                    // we are getting input stream from url
//                    // and storing it in our variable.
//                    inputStream = BufferedInputStream(urlConnection.inputStream)
//                }
//            } catch (e: IOException) {
//                // this is the method
//                // to handle errors.
//                e.printStackTrace()
//                return null
//            }
//            return inputStream
//        }
//
//        override fun onPostExecute(inputStream: InputStream?) {
//            // after the execution of our async
//            // task we are loading our pdf in our pdf view.
//            binding.pdfView.fromStream(inputStream).load()
//        }
//    }

    // on below line we are creating a class for
    // our pdf view and passing our pdf view
    // to it as a parameter.
//    class RetrievePDFFromURL(pdfView: PDFView) :
//        AsyncTask<String, Void, InputStream>() {
//
//        private val mypdfView: PDFView = pdfView
//
//        // on below line we are calling our do in background method.
//        override fun doInBackground(vararg params: String?): InputStream? {
//            // on below line we are creating a variable for our input stream.
//            var inputStream: InputStream? = null
//            try {
//                // on below line we are creating an url
//                // for our url which we are passing as a string.
//                val url = URL(params.get(0))
//
//                // on below line we are creating our http url connection.
//                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
//
//                // on below line we are checking if the response
//                // is successful with the help of response code
//                // 200 response code means response is successful
//                if (urlConnection.responseCode == 200) {
//                    // on below line we are initializing our input stream
//                    // if the response is successful.
//                    inputStream = BufferedInputStream(urlConnection.inputStream)
//                }
//            }
//            // on below line we are adding catch block to handle exception
//            catch (e: Exception) {
//                // on below line we are simply printing
//                // our exception and returning null
//                e.printStackTrace()
//                return null;
//            }
//            // on below line we are returning input stream.
//            return inputStream;
//        }
//
//        // on below line we are calling on post execute
//        // method to load the url in our pdf view.
//        override fun onPostExecute(result: InputStream?) {
//            // on below line we are loading url within our
//            // pdf view on below line using input stream.
//            mypdfView.fromStream(result).load()
//        }
//    }
}