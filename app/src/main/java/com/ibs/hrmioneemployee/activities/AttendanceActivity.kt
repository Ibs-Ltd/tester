package com.ibs.hrmioneemployee.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.ibs.hrmioneemployee.adapters.AttendanceDateByDateAdapter
import com.ibs.hrmioneemployee.databinding.ActivityAttendanceBinding
import com.ibs.hrmioneemployee.models.api_models.attendance_of_month.AttendanceOfMonthResponse
import com.ibs.hrmioneemployee.models.api_models.attendance_of_month.DateByDate
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import com.whiteelephant.monthpicker.MonthPickerDialog
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var sp: SharedPreferences
//    private lateinit var sharedPreferenceSettings: SharedPreferenceSettings
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private var userId: Int = 0
    private lateinit var Authorization: String
//    private var selectedMonth: Int = -1
//    private var selectedYear: Int = -1
    val monthFormatDate = SimpleDateFormat("MM", Locale.US)
    val yearFormatDate = SimpleDateFormat("yyyy", Locale.US)
//    private var totalDaysInMonth : Int = 0
    private lateinit var attendanceDateByDateAdapter: AttendanceDateByDateAdapter
    private lateinit var dateByDateList: ArrayList<DateByDate>
    private lateinit var dataLoading: DataLoading

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        dataLoading = DataLoading(this)

        sharedPreferenceClass = SharedPreferenceClass(this)
        Authorization = sharedPreferenceClass.getLoginToken()
        sp = this.getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, MODE_PRIVATE)
        userId = sp.getInt("UserId", 1)
        val firstName = sp.getString("FirstName", "")
        val lastName = sp.getString("LastName", "")
        binding.fullName.text = "Hi $firstName $lastName"

        val today = Calendar.getInstance()
        val month: String = getMonth((today.get(Calendar.MONTH))+1)
        val year = today.get(Calendar.YEAR)

        binding.monthYear.text = "$month, $year"

//        totalDaysInMonth = today.getActualMaximum(Calendar.DAY_OF_MONTH)
        callAttendanceOfMonthApi(month, year)

        binding.filter.setOnClickListener {

            val builder = MonthPickerDialog.Builder(this, { selectedMonth, selectedYear ->
                val selectdate: Calendar = Calendar.getInstance()
                selectdate.set(Calendar.YEAR, selectedYear)
                selectdate.set(Calendar.MONTH, selectedMonth)

                val month: Int = monthFormatDate.format(selectdate.time).toInt()
                val year: Int = yearFormatDate.format(selectdate.time).toInt()
                val monthInString: String = getMonth(month)
                binding.monthYear.text = "$monthInString, $year"
                callAttendanceOfMonthApi(monthInString, year)

            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH))

            builder.setActivatedYear(today.get(Calendar.YEAR)).setMinYear(2010).setMaxYear(today.get(Calendar.YEAR))
                .setActivatedMonth(today.get(Calendar.MONTH)).setMinMonth(0).setMaxMonth(11).build().show()
        }

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        binding.viewAll.setOnClickListener {
            startActivity(Intent(this, ViewAllAttendanceActivity::class.java))
        }
    }

    private fun callAttendanceOfMonthApi(month: String, year: Int) {

        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<AttendanceOfMonthResponse> = apiServices.attendanceOfMonth(Authorization, userId, month, year)

        call.enqueue(object : Callback<AttendanceOfMonthResponse>{
            override fun onResponse(call: Call<AttendanceOfMonthResponse>, response: Response<AttendanceOfMonthResponse>) {
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.code == 200){
                        dateByDateList = response.body()!!.result.dateByDate
                        binding.recyclerView.apply {
                            layoutManager = GridLayoutManager(this@AttendanceActivity, 6)
                            attendanceDateByDateAdapter = AttendanceDateByDateAdapter(this@AttendanceActivity, dateByDateList)
                            adapter = attendanceDateByDateAdapter
                        }
//                        Toast.makeText(this@AttendanceActivity, ""+response.body()!!.message, Toast.LENGTH_SHORT).show()
                        setAttendanceData(response.body()!!)
                    }
                }
                else{
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@AttendanceActivity, "Error Code: " + response.code()
                                + "\nMessage: " + jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
               dataLoading.hideLoading()
            }
            override fun onFailure(call: Call<AttendanceOfMonthResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@AttendanceActivity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAttendanceData(body: AttendanceOfMonthResponse) {

        val presentDays = body.result.present
        val absentDays = body.result.absent

        val presentPercentage = (presentDays * 100) / 31
        val absentPercentage = (absentDays * 100) / 31

        binding.presentDays.text = presentDays.toString()
        binding.absentDays.text = absentDays.toString()

        binding.presentProgressBar.progress = presentPercentage
        binding.absentProgressBar.progress = absentPercentage
    }

    private fun getMonth(month: Int): String {

        var month2 = ""
        when (month) {
            1 -> month2 = "January"
            2 -> month2 = "February"
            3 -> month2 = "March"
            4 -> month2 = "April"
            5 -> month2 = "May"
            6 -> month2 = "June"
            7 -> month2 = "July"
            8 -> month2 = "August"
            9 -> month2 = "September"
            10 -> month2 = "October"
            11 -> month2 = "November"
            12 -> month2 = "December"
        }
        return month2
    }
}