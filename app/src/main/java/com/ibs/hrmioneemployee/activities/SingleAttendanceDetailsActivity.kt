package com.ibs.hrmioneemployee.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.ibs.hrmioneemployee.databinding.ActivitySingleAttendanceDetailsBinding
import com.ibs.hrmioneemployee.models.api_models.attendance_history.Result
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class SingleAttendanceDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingleAttendanceDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySingleAttendanceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        val result: Result = intent.extras!!.getSerializable("SingleAttendanceFullDetails") as Result

        binding.date.text = result.date

        if ((result.totalWorkingHours/60).compareTo(0) != 0){
            binding.leaveBalanceMainProgressBar.progress = (convertMillisToMinutes(result.workingHours)*100)/(result.totalWorkingHours/60).toInt()
        }

        if (result.checkIn.compareTo(0) == 0) {
            binding.checkInTime.text = "--:--"
        } else {
            binding.checkInTime.text = convertLongToTime(result.checkIn)
        }
        if (result.checkOut.compareTo(0) == 0) {
            binding.checkOutTime.text = "--:--"
        } else {
            binding.checkOutTime.text = convertLongToTime(result.checkOut)
        }
        if (result.workingHours.compareTo(0) == 0){
            binding.shiftTime.text = "00:00"
        }
        else {
            binding.shiftTime.text = convertMillisToDuration(result.workingHours)
        }
    }

    @OptIn(ExperimentalTime::class)
    @SuppressLint("SimpleDateFormat")
    fun convertMillisToDuration(time: Long): String {

        var hours1: String
        var minutes1: String
        val duration = Duration.milliseconds(time)
        val hours = duration.inWholeHours
        hours1 = hours.toString()
        if (hours<10){
            hours1 = "0$hours1"
        }
        var minutes = duration.inWholeMinutes
        minutes %= 60
        minutes1 = minutes.toString()
        if (minutes<10){
            minutes1 = "0$minutes1"
        }
        return "${hours1}h:${minutes1}m"
    }

    @OptIn(ExperimentalTime::class)
    @SuppressLint("SimpleDateFormat")
    fun convertMillisToMinutes(time: Long): Int {
        val duration = Duration.milliseconds(time)
        val minutes = duration.inWholeMinutes
        return minutes.toInt()
    }

    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("hh:mm aa")
        return format.format(date)
    }
}