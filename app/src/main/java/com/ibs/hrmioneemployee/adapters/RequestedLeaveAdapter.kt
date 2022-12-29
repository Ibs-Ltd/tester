package com.ibs.hrmioneemployee.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.hrmioneemployee.databinding.LeaveAppliedHistoryRecyclerSingleItemBinding
import com.ibs.hrmioneemployee.models.api_models.my_leave_balance.RequestedLeave
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RequestedLeaveAdapter(private val context: Context, private var requestedLeaveList: ArrayList<RequestedLeave>) :
    RecyclerView.Adapter<RequestedLeaveAdapter.MyViewHolder>() {

    inner class MyViewHolder(binding: LeaveAppliedHistoryRecyclerSingleItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private var leaveType = binding.leaveType
        private var appliedDate = binding.appliedDate
        private var leaveAppliedDateTime = binding.leaveAppliedDateTime

        @SuppressLint("SetTextI18n")
        fun setValues(requestedLeave: RequestedLeave) {
            leaveType.text = requestedLeave.leaveName
            appliedDate.text = "Applied for ${convertLongToDate(requestedLeave.startDate)} - ${convertLongToDate(requestedLeave.endDate)}"
            leaveAppliedDateTime.text = convertLongToCurrentDate(requestedLeave.creationTime)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LeaveAppliedHistoryRecyclerSingleItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setValues(requestedLeaveList[position])
    }

    override fun getItemCount(): Int {
        return requestedLeaveList.size
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(date: Long): String {
        val simpleDateFormat = SimpleDateFormat("MMM dd")
        val dateString: String = simpleDateFormat.format(date)
        return dateString
    }

//    @SuppressLint("SimpleDateFormat")
//    private fun convertLongToDateAndTime(date: Long): String {
//        val simpleDateFormat = SimpleDateFormat("dd MMM - HH:mm")
//        val dateString: String = simpleDateFormat.format(date)
//        return dateString
//    }

//    @SuppressLint("SimpleDateFormat")
//    private fun convertLongToDate(date: Long): String {
//        val simpleDateFormat = SimpleDateFormat("MMM dd")
//        val dateString: String = simpleDateFormat.format(date)
//        return dateString
//    }

    private val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy, hh:mm aa", Locale.ENGLISH)
    fun convertLongToCurrentDate(time: Long): String {
        return simpleDateFormat.format(time * 1000L)
    }
}