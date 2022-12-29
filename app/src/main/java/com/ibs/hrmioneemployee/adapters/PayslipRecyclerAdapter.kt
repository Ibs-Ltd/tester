package com.ibs.hrmioneemployee.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.downloader.*
import com.ibs.hrmioneemployee.databinding.PayslipRecyclerSingleItemBinding
import com.ibs.hrmioneemployee.models.api_models.payslip.Result

class PayslipRecyclerAdapter(private val context: Context, private val payslipList: ArrayList<Result>,
                             private var onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<PayslipRecyclerAdapter.PayslipViewHolder>() {

    inner class PayslipViewHolder(binding: PayslipRecyclerSingleItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val monthYear = binding.monthYear
        val llDownload = binding.llDownload
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayslipViewHolder {

        val binding = PayslipRecyclerSingleItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return PayslipViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PayslipViewHolder, position: Int) {
        holder.monthYear.text = payslipList[position].month
        holder.llDownload.setOnClickListener {
            onItemClickListener.onItemClick(payslipList[position])
        }
    }

    override fun getItemCount(): Int {
        return payslipList.size
    }

    interface OnItemClickListener {
        fun onItemClick(result: Result)
    }
}