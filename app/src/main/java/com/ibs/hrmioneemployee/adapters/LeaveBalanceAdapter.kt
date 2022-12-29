package com.ibs.hrmioneemployee.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.LeaveBalanceRecyclerSingleItemBinding
import com.ibs.hrmioneemployee.models.api_models.my_leave_balance.LeaveBalance

class LeaveBalanceAdapter(private val context: Context,
                          private var leaveBalanceList: ArrayList<LeaveBalance>) :
    RecyclerView.Adapter<LeaveBalanceAdapter.MyViewHolder>() {

    private var int: Int = 0

    inner class MyViewHolder(binding: LeaveBalanceRecyclerSingleItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private var leaveType = binding.leaveName
        private var remainingLeave = binding.remainingLeaves
        private var progressBar = binding.progressBar

        @SuppressLint("SetTextI18n", "NewApi", "UseCompatLoadingForDrawables")
        fun setValues(leaveBalance: LeaveBalance) {
            leaveType.text = leaveBalance.leaveName
            remainingLeave.text = leaveBalance.noOFLeaves.toString()

//            if (leaveBalance.totalNoOfLeave != 0){
//                progressBar.progress = (leaveBalance.noOFLeaves*100)/leaveBalance.totalNoOfLeave
//            }

            when (int) {
                0 -> {
                    progressBar.progressDrawable = context.getDrawable(R.drawable.casual_leave_progress_bar)
                    int += 1
                }
                1 -> {
                    progressBar.progressDrawable = context.getDrawable(R.drawable.medical_leave_progress_bar)
                    int += 1
                }
                2 -> {
                    progressBar.progressDrawable = context.getDrawable(R.drawable.annual_leave_progress_bar)
                    int += 1
                }
                3 -> {
                    progressBar.progressDrawable = context.getDrawable(R.drawable.maternity_leave_progress_bar)
                    int = 0
                }
            }

            val totalLeave = (leaveBalance.noOFLeaves * 100) / leaveBalance.totalNoOfLeave
            progressBar.progress = totalLeave
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LeaveBalanceRecyclerSingleItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setValues(leaveBalanceList[position])
    }

    override fun getItemCount(): Int {
        return leaveBalanceList.size
    }
}