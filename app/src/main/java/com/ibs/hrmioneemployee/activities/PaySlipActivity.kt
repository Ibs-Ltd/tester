package com.ibs.hrmioneemployee.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.*
import com.ibs.hrmioneemployee.adapters.PayslipRecyclerAdapter
import com.ibs.hrmioneemployee.databinding.ActivityPaySlipBinding
import com.ibs.hrmioneemployee.models.api_models.payslip.PayslipResponse
import com.ibs.hrmioneemployee.models.api_models.payslip.Result
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.InternetConnection
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import com.ibs.hrmioneemployee.utilities.ShowToast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.whiteelephant.monthpicker.MonthPickerDialog
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PaySlipActivity : AppCompatActivity(), PayslipRecyclerAdapter.OnItemClickListener {

    private lateinit var binding: ActivityPaySlipBinding
    private lateinit var payslipAdapter: PayslipRecyclerAdapter
    val formatDate = SimpleDateFormat("yyyy", Locale.US)
    private var year = 0
    private lateinit var Authorization:String
    private var employeeId = 0
    private lateinit var sp: SharedPreferences
    private lateinit var sharedPreferencesClass: SharedPreferenceClass
    private lateinit var payslipList: ArrayList<Result>
    private lateinit var dataLoading: DataLoading

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaySlipBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        dataLoading = DataLoading(this)

        val calendar: Calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)

        sharedPreferencesClass = SharedPreferenceClass(this)
        Authorization = sharedPreferencesClass.getLoginToken()

        sp = getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, MODE_PRIVATE)
        employeeId = sp.getInt("UserId", 1)

        if (InternetConnection.checkConnection(this)){
            callEmployeePayslipApi()
        }
        else{
            ShowToast.showToast(this)
        }

        binding.filter.setOnClickListener {
            openYearPicker()
        }

        binding.payslipCircle.setOnClickListener {
            openYearPicker()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun openYearPicker() {

        val today = Calendar.getInstance()
        val builder = MonthPickerDialog.Builder(this, { selectedMonth, selectedYear ->
            val selectdate: Calendar = Calendar.getInstance()
            selectdate.set(Calendar.YEAR, selectedYear)
            selectdate.set(Calendar.MONTH, selectedMonth)
            val date: Int = formatDate.format(selectdate.time).toInt()
            binding.payslipYear.text = date.toString()
            binding.monthYearText.text = "Jan $date - Dec $date"
            year = date
            callEmployeePayslipApi()

        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH))

        builder.setActivatedYear(today.get(Calendar.YEAR)).setMinYear(2010).setMaxYear(today.get(Calendar.YEAR)).showYearOnly().build().show()
    }


    private fun callEmployeePayslipApi() {
        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<PayslipResponse> = apiServices.payslip(Authorization, employeeId, year)

        call.enqueue(object : Callback<PayslipResponse>{
            override fun onResponse(call: Call<PayslipResponse>, response: Response<PayslipResponse>) {

                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.code == 200){

                        payslipList = response.body()!!.result
                        binding.recyclerView.apply {
                            layoutManager = LinearLayoutManager(this@PaySlipActivity)
                            payslipAdapter = PayslipRecyclerAdapter(this@PaySlipActivity, payslipList, this@PaySlipActivity)
                            adapter = payslipAdapter
                        }
                        Toast.makeText(this@PaySlipActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@PaySlipActivity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<PayslipResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@PaySlipActivity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(result: Result) {
        PRDownloader.initialize(this)
        checkUserPermission(result.payslip)
    }

    private fun checkUserPermission(url: String) {

        Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE).withListener(object :
                MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()){
                        downloadPdf(url)
                    }
                    else{
                        Toast.makeText(this@PaySlipActivity, "Please allow all permissions", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) { token?.continuePermissionRequest()
                }
            }).check()
    }

//    private fun downloadPdf(url: String) {
//        var request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
//            request.allowScanningByMediaScanner()
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        }
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, null, null))
//        var downloadManager: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        request.setMimeType("application/pdf")
//        request.allowScanningByMediaScanner()
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
//        downloadManager.enqueue(request)
//    }

    private fun downloadPdf(url: String) {

        dataLoading.startLoading()

        val file: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        PRDownloader.download(url, file.path, URLUtil.guessFileName(url, null, null))
            .build()
            .setOnStartOrResumeListener { }
            .setOnPauseListener {
                dataLoading.hideLoading()
                Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show()
            }
            .setOnCancelListener(object : OnCancelListener {
                override fun onCancel() {
                    dataLoading.hideLoading()
                    Toast.makeText(this@PaySlipActivity, "Cancelled", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@PaySlipActivity, "Download completed", Toast.LENGTH_SHORT).show()
                }
                override fun onError(error: com.downloader.Error?) {
                    dataLoading.hideLoading()
                    Toast.makeText(this@PaySlipActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

}