package com.ibs.hrmioneemployee.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.AttendanceHistorySingleRowBinding
import com.ibs.hrmioneemployee.models.api_models.attendance_history.Result
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class AttendanceHistoryAdapter(
    private val context: Context,
    private var attendanceList: ArrayList<Result>,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AttendanceHistoryAdapter.AttendanceViewHolder>() {

    inner class AttendanceViewHolder(binding: AttendanceHistorySingleRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val officeHomeImage = binding.officeHomeImage
        private val attendanceDate = binding.attendanceDate
        private val checkInTime = binding.checkInTime
        private val checkOutTime = binding.checkOutTime
        private val workingHrs = binding.workingHrs
        private val llSingleItem = binding.llSingleItem
        private val llAbsentHoliday = binding.llAbsentHoliday
        private val absentHoliday = binding.absentHoliday

        @SuppressLint("SetTextI18n")
        fun setValues(result: Result) {

            if (result.isPresent == 1) {
                if (result.workingLocation == "WFO") {
                    officeHomeImage.setImageResource(R.drawable.office_history)
                } else if (result.workingLocation == "WFH") {
                    officeHomeImage.setImageResource(R.drawable.home_history)
                }
            } else {
                if (result.isHoliday == 1) {
                    officeHomeImage.visibility = View.GONE
                    llAbsentHoliday.visibility = View.VISIBLE
                    absentHoliday.text = "H"
                } else {
                    officeHomeImage.visibility = View.GONE
                    llAbsentHoliday.visibility = View.VISIBLE
                    absentHoliday.text = "A"
                }
            }

            attendanceDate.text = result.date
            if (result.checkIn.compareTo(0) == 0) {
                checkInTime.text = "--:--"
            } else {
                checkInTime.text = convertLongToTime(result.checkIn)
            }
            if (result.checkOut.compareTo(0) == 0) {
                checkOutTime.text = "--:--"
            } else {
                checkOutTime.text = convertLongToTime(result.checkOut)
            }
            if (result.checkOut.compareTo(0) != 0 && result.workingHours.compareTo(0) == 0) {
                workingHrs.text = "00:00"
            }
            else if (result.workingHours.compareTo(0) == 0) {
                workingHrs.text = "--:--"
            } else {
                workingHrs.text = convertMillisToDuration(result.workingHours)
            }

            llSingleItem.setOnClickListener {
                onItemClickListener.onItemClick(result)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(filteredList: ArrayList<Result>) {
        this.attendanceList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {

        val binding =
            AttendanceHistorySingleRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.setValues(attendanceList[position])
    }

    override fun getItemCount(): Int {
        return attendanceList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface OnItemClickListener {
        fun onItemClick(result: Result)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        //val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        val format = SimpleDateFormat("hh:mm aa")
        return format.format(date)
    }

//    @SuppressLint("SimpleDateFormat")
//    fun convertLongToTimeWithoutAMPM(time: Long): String {
//        val date = Date(time)
//        //val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
//        val format = SimpleDateFormat("HH:mm")
//        return format.format(date)
//    }

    @OptIn(ExperimentalTime::class)
    @SuppressLint("SimpleDateFormat")
    fun convertMillisToDuration(time: Long): String {
//        val date = Date(time)
//        //val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
//        val format = SimpleDateFormat("HH:mm")
//        return format.format(date)

        var hours1: String
        var minutes1: String
        val duration = Duration.milliseconds(time)
        val hours = duration.inWholeHours
        hours1 = hours.toString()
        if (hours < 10) {
            hours1 = "0$hours1"
        }
        var minutes = duration.inWholeMinutes
        minutes %= 60
        minutes1 = minutes.toString()
        if (minutes < 10) {
            minutes1 = "0$minutes1"
        }
        return "${hours1}h:${minutes1}m"
    }
}
