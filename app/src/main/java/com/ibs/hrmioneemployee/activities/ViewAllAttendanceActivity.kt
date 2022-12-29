package com.ibs.hrmioneemployee.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.adapters.AttendanceHistoryAdapter
import com.ibs.hrmioneemployee.databinding.ActivityViewAllAttendanceBinding
import com.ibs.hrmioneemployee.models.api_models.attendance_history.AttendanceHistoryResponse
import com.ibs.hrmioneemployee.models.api_models.attendance_history.Result
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ViewAllAttendanceActivity : AppCompatActivity(),
    AttendanceHistoryAdapter.OnItemClickListener {

    private lateinit var binding: ActivityViewAllAttendanceBinding
    private lateinit var attendanceAdapter: AttendanceHistoryAdapter
    private var attendanceList: ArrayList<Result> = ArrayList()
    private lateinit var Authorization: String
    private lateinit var sp: SharedPreferences
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private var userId: Int = 0
    private var startDateInLong: Long = 0
    private var endDateInLong: Long = 0
//    private lateinit var startDateInString: String
//    private lateinit var endDateInString: String
    private lateinit var dataLoading: DataLoading
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var endDay: String? = null
    private lateinit var call: Call<AttendanceHistoryResponse>

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewAllAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        dataLoading = DataLoading(this)

        sharedPreferenceClass = SharedPreferenceClass(this)
        Authorization = sharedPreferenceClass.getLoginToken()
        sp = getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, MODE_PRIVATE)
        userId = sp.getInt("UserId", -1)

//        startDateInString = "01/01/2018"
//        try {
//            val sdf = SimpleDateFormat("dd/MM/yyyy")
//            val date: Date = sdf.parse(startDateInString) as Date
//            startDateInLong = date.time
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }

//        val calender = Calendar.getInstance()
//        val year = calender.get(Calendar.YEAR)
//        val month = calender.get(Calendar.MONTH)
//        val day = calender.get(Calendar.DAY_OF_MONTH)
//        val newMonth = month + 1
//        if (day < 10) {
//            endDateInString = "0$day/$newMonth/$year"
//        } else {
//            endDateInString = "$day/$newMonth/$year"
//        }
//
//        endDay = endDateInString
//        try {
//            val sdf = SimpleDateFormat("dd/MM/yyyy")
//            val date: Date = sdf.parse(endDateInString) as Date
//            endDateInLong = date.time
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }

        callAttendanceHistoryApi()

        binding.filter.setOnClickListener {
            val intent = Intent(this, AttendanceFilterActivity::class.java)
            startActivityForResult(intent, 100)
        }

        binding.searchView.setOnClickListener {
            var calendar: Calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                R.style.DialogTheme,
                DatePickerDialog.OnDateSetListener { datePicker, mYear, mMonth, mDay ->
                    datePicker.setBackgroundColor(Color.BLUE)
                    val monthOfYear = mMonth + 1
                    if (mDay < 10) {
                        binding.searchView.text = "0$mDay/$monthOfYear/$mYear"
                    } else {
                        binding.searchView.text = "$mDay/$monthOfYear/$mYear"
                    }
                    filterList(binding.searchView.text.toString())
                }, year, month, day)
            datePickerDialog.show()
        }

//        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                filterList(newText)
//                return true
//            }
//        })

    }

    private fun filterList(text: String?) {

        val filteredList: ArrayList<Result> = ArrayList()
        val emptyList: ArrayList<Result> = ArrayList()
        for (result in attendanceList) {
            if (result.date.lowercase() == text!!.lowercase()) {
                filteredList.add(result)
            }
        }
        if (filteredList.isEmpty()) {
            attendanceAdapter.setFilteredList(emptyList)
        } else {
            attendanceAdapter.setFilteredList(filteredList)
        }
    }

    override fun onItemClick(result: Result) {
        val intent = Intent(this, SingleAttendanceDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("SingleAttendanceFullDetails", result)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                if (data != null) {
                    startDateInLong = data.getLongExtra("startDateInLong", 0)
                    endDateInLong = data.getLongExtra("endDateInLong", 0)
                    endDay = data.getStringExtra("endDateInString").toString()
                    callAttendanceHistoryApi()
                }
            }
        }
    }

    private fun callAttendanceHistoryApi() {

        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)

        call = if (endDay != null){
            apiServices.attendanceHistoryWithFilter(Authorization, userId, startDateInLong, endDateInLong, endDay!!)
        } else{
            apiServices.attendanceHistoryWithoutFilter(Authorization, userId)
        }

        call.enqueue(object : Callback<AttendanceHistoryResponse> { override fun onResponse(
                call: Call<AttendanceHistoryResponse>, response: Response<AttendanceHistoryResponse>) {

                if (response.isSuccessful && response.body() != null) {
                    if (response.code() == 200) {
                        attendanceList = response.body()!!.result
                        attendanceAdapter = AttendanceHistoryAdapter(
                            this@ViewAllAttendanceActivity,
                            attendanceList,
                            this@ViewAllAttendanceActivity
                        )
                        binding.recyclerView.apply {
                            linearLayoutManager =
                                LinearLayoutManager(this@ViewAllAttendanceActivity)
                            linearLayoutManager.reverseLayout = true
                            linearLayoutManager.stackFromEnd = true
                            layoutManager = linearLayoutManager
                            adapter = attendanceAdapter
                        }
                    }
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(
                        this@ViewAllAttendanceActivity,
                        jObjError.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<AttendanceHistoryResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@ViewAllAttendanceActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }
}