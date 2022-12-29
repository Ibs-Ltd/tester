package com.ibs.hrmioneemployee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.AttendanceDateByDateRecyclerSingleItemBinding
import com.ibs.hrmioneemployee.models.api_models.attendance_of_month.DateByDate

class AttendanceDateByDateAdapter(private var context: Context, private var dateByDateList: ArrayList<DateByDate>) :
    RecyclerView.Adapter<AttendanceDateByDateAdapter.DateByDateViewHolder>() {

    inner class DateByDateViewHolder(binding: AttendanceDateByDateRecyclerSingleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var presentAbsentImage = binding.presentAbsentImage
        var date = binding.date

        fun setValues(dateByDate: DateByDate) {
            date.text = dateByDate.dateAndMonth
            if (dateByDate.isPresent == 1){
                presentAbsentImage.setBackgroundResource(R.drawable.attendance_green_circle_bg)
            }
            else if (dateByDate.isHoliday == 1){
                presentAbsentImage.setBackgroundResource(R.drawable.attendance_blue_circle_bg)
            }
            else{
                presentAbsentImage.setBackgroundResource(R.drawable.attendance_orange_circle_bg)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateByDateViewHolder {

        val binding = AttendanceDateByDateRecyclerSingleItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return DateByDateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateByDateViewHolder, position: Int) {
        holder.setValues(dateByDateList[position])
    }

    override fun getItemCount(): Int {
        return dateByDateList.size
    }
}