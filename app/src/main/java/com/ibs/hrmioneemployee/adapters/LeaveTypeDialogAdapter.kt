package com.ibs.hrmioneemployee.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.hrmioneemployee.databinding.LeaveTypeDialogRecyclerSingleItemBinding
import com.ibs.hrmioneemployee.models.api_models.leave_type_list.Result

class LeaveTypeDialogAdapter(private val context: Context,
                             private var leaveTypeList: ArrayList<Result>,
                             private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<LeaveTypeDialogAdapter.LeaveTypeViewHolder>() {

    inner class LeaveTypeViewHolder(binding: LeaveTypeDialogRecyclerSingleItemBinding): RecyclerView.ViewHolder(binding.root) {

        private val leaveType = binding.leaveType
        private val remainingLeaves = binding.remainingLeaves
        private val llLeaveType = binding.llLeaveType

        @SuppressLint("SetTextI18n")
        fun setValues(result: Result) {
            leaveType.text = result.leaveTypeName
            remainingLeaves.text = "${result.remainingLeaves} Left"

            llLeaveType.setOnClickListener {
                onItemClickListener.onItemClick(result)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveTypeViewHolder {
        val binding = LeaveTypeDialogRecyclerSingleItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return LeaveTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaveTypeViewHolder, position: Int) {
            holder.setValues(leaveTypeList[position])
    }

    override fun getItemCount(): Int {
        return leaveTypeList.size
    }

    interface OnItemClickListener {
        fun onItemClick(result: Result)
    }
}