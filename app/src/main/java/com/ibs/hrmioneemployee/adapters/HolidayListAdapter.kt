package com.ibs.hrmioneemployee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.hrmioneemployee.databinding.HolidayRecyclerSingleItemBinding
import com.ibs.hrmioneemployee.models.other_models.HolidayListModel

class HolidayListAdapter(private val context: Context, private val holidayList: ArrayList<HolidayListModel>) :
    RecyclerView.Adapter<HolidayListAdapter.HolidayViewHOlder>() {

    inner class HolidayViewHOlder(binding: HolidayRecyclerSingleItemBinding): RecyclerView.ViewHolder(binding.root) {
        val holidayName = binding.holidayName
        val holidayDate = binding.holidayDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHOlder {
        val binding = HolidayRecyclerSingleItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolidayViewHOlder(binding)
    }

    override fun onBindViewHolder(holder: HolidayViewHOlder, position: Int) {
        holder.holidayName.text = holidayList[position].holidayName
        holder.holidayDate.text = holidayList[position].holidayDate
    }

    override fun getItemCount(): Int {
        return holidayList.size
    }
}