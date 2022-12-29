package com.ibs.hrmioneemployee.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.LeaveAppliedHistoryRecyclerSingleItemBinding
import com.ibs.hrmioneemployee.models.api_models.my_leave_balance.LeaveHistory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LeaveHistoryAdapter(private val context: Context,
                          private var leaveHistoryList: ArrayList<LeaveHistory>
                                    ) :
    RecyclerView.Adapter<LeaveHistoryAdapter.MyViewHolder>() {

    inner class MyViewHolder(binding: LeaveAppliedHistoryRecyclerSingleItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private var leaveType = binding.leaveType
        private var appliedDate = binding.appliedDate
        private var leaveAppliedDateTime = binding.leaveAppliedDateTime
        private var leaveStatus = binding.leaveStatus

        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun setValues(leaveHistory: LeaveHistory) {
            leaveType.text = leaveHistory.leaveName
            appliedDate.text = "Applied for ${convertLongToDate(leaveHistory.startDate)} - ${convertLongToDate(leaveHistory.endDate)}"
            leaveAppliedDateTime.text = convertLongToCurrentDate(leaveHistory.creationTime)

            if (leaveHistory.status == "APPROVED"){
                leaveStatus.text = leaveHistory.status
                leaveStatus.setTextColor(Color.WHITE)
                leaveStatus.setBackgroundResource(R.drawable.approved_background)
            }
            else if (leaveHistory.status == "REJECTED"){
                leaveStatus.text = leaveHistory.status
                leaveStatus.setTextColor(Color.WHITE)
                leaveStatus.setBackgroundResource(R.drawable.rejected_background)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LeaveAppliedHistoryRecyclerSingleItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setValues(leaveHistoryList[position])
    }

    override fun getItemCount(): Int {
        return leaveHistoryList.size
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(date: Long): String {
        val simpleDateFormat = SimpleDateFormat("MMM dd")
        val dateString: String = simpleDateFormat.format(date)
        return dateString
    }
//
//    @SuppressLint("SimpleDateFormat")
//    private fun convertLongToDateAndTime(date: Long): String {
//        val simpleDateFormat = SimpleDateFormat("dd MMM - HH:mm")
//        val dateString: String = simpleDateFormat.format(date)
//        return dateString
//    }

    private val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy, hh:mm aa", Locale.ENGLISH)
    fun convertLongToCurrentDate(time: Long): String {
        return simpleDateFormat.format(time * 1000L)
    }
}