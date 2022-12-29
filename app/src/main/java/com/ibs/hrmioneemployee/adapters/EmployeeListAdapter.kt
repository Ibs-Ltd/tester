package com.ibs.hrmioneemployee.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.TeamRecyclerSingleItemBinding
import com.ibs.hrmioneemployee.models.api_models.employees_list.TotalEmployeeResponse

class EmployeeListAdapter(
    private val context: Context,
    private var employeeList: ArrayList<TotalEmployeeResponse>,
    private var onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<EmployeeListAdapter.TeamViewHolder>() {

    inner class TeamViewHolder(binding: TeamRecyclerSingleItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val employeePhoto = binding.employeePhoto
        private val employeeName = binding.employeeName
        private val employeeRole = binding.employeeRole
        private val cardView = binding.cardView

        @SuppressLint("SetTextI18n")
        fun setValues(totalEmployeeResponse: TotalEmployeeResponse){

            Glide.with(context).load(totalEmployeeResponse.profilePicturePath).apply(
                    RequestOptions.placeholderOf(R.drawable.dashboard_profile).error(R.drawable.dashboard_profile)).into(employeePhoto)
//            employeePhoto.setImageResource(result.profilePicturePath)
            employeeName.text = "${totalEmployeeResponse.firstName} ${totalEmployeeResponse.lastName}"
            employeeRole.text = totalEmployeeResponse.designation

            cardView.setOnClickListener {
                onItemClickListener.onItemClick(totalEmployeeResponse)
            }
        }
    }

    fun setFilteredList(filteredList: ArrayList<TotalEmployeeResponse>) {
        this.employeeList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {

        val binding = TeamRecyclerSingleItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.setValues(employeeList[position])
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    interface OnItemClickListener {
        fun onItemClick(totalEmployeeResponse: TotalEmployeeResponse)
    }
}
