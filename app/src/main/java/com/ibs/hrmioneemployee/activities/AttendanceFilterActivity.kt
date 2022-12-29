package com.ibs.hrmioneemployee.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.ActivityAttendanceFilterBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AttendanceFilterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceFilterBinding
    private lateinit var startDateInString: String
    private lateinit var endDateInString: String
    private var startDateInLong: Long = 0
    private var endDateInLong: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAttendanceFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        val years = arrayOf(
            "2018",
            "2019",
            "2020",
            "2021",
            "2022",
            "2023",
            "2024",
            "2025",
            "2026",
            "2027",
            "2028",
            "2029",
            "2030",
            "2031",
            "2032",
            "2033",
            "2034",
            "2035",
            "2036",
            "2037",
            "2038",
            "2039",
            "2040"
        )
        val months = arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )
        val dates = arrayOf(
            "01",
            "02",
            "03",
            "04",
            "05",
            "06",
            "07",
            "08",
            "09",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28",
            "29",
            "30",
            "31"
        )

        //  year
        val yearFirstAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter<CharSequence>(this, R.layout.attendance_filter_spinner_layout, years)
        yearFirstAdapter.setDropDownViewResource(R.layout.attendance_filter_spinner_layout)
        binding.yearFirstSpinner.adapter = yearFirstAdapter

        val yearLastAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter<CharSequence>(this, R.layout.attendance_filter_spinner_layout, years)
        yearLastAdapter.setDropDownViewResource(R.layout.attendance_filter_spinner_layout)
        binding.yearLastSpinner.adapter = yearLastAdapter

        //  month
        val monthFirstAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter<CharSequence>(this, R.layout.attendance_filter_spinner_layout, months)
        monthFirstAdapter.setDropDownViewResource(R.layout.attendance_filter_spinner_layout)
        binding.monthFirstSpinner.adapter = monthFirstAdapter

        val monthLastAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter<CharSequence>(this, R.layout.attendance_filter_spinner_layout, months)
        monthLastAdapter.setDropDownViewResource(R.layout.attendance_filter_spinner_layout)
        binding.monthLastSpinner.adapter = monthLastAdapter

        //  date
        val dateFirstAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter<CharSequence>(this, R.layout.attendance_filter_spinner_layout, dates)
        dateFirstAdapter.setDropDownViewResource(R.layout.attendance_filter_spinner_layout)
        binding.dateFirstSpinner.adapter = dateFirstAdapter

        val dateLastAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter<CharSequence>(this, R.layout.attendance_filter_spinner_layout, dates)
        dateLastAdapter.setDropDownViewResource(R.layout.attendance_filter_spinner_layout)
        binding.dateLastSpinner.adapter = dateLastAdapter

        binding.tvApplyForFilter.setOnClickListener {
            applyForFilterClick()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun applyForFilterClick() {

        val yearFirst: String = binding.yearFirstSpinner.selectedItem.toString()
        val monthFirst: Int = binding.monthFirstSpinner.selectedItemPosition
        val dateFirst: String = binding.dateFirstSpinner.selectedItem.toString()

        startDateInString = "$dateFirst/${monthFirst + 1}/$yearFirst"
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val date: Date = sdf.parse(startDateInString) as Date
            startDateInLong = date.time

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val yearLast: String = binding.yearLastSpinner.selectedItem.toString()
        val monthLast: Int = binding.monthLastSpinner.selectedItemPosition
        val dateLast: String = binding.dateLastSpinner.selectedItem.toString()

        endDateInString = "$dateLast/${monthLast + 1}/$yearLast"

        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val date: Date = sdf.parse(endDateInString) as Date
            endDateInLong = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val intent = Intent()
        intent.putExtra("startDateInLong", startDateInLong)
        intent.putExtra("endDateInLong", endDateInLong)
        intent.putExtra("endDateInString", endDateInString)
        setResult(RESULT_OK, intent)
//        Log.d("StartDate", startDateInLong.toString())
//        Log.d("endDate", endDateInLong.toString())
        finish()
    }
}