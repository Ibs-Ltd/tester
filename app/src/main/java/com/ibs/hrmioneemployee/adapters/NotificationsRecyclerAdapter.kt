package com.ibs.hrmioneemployee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.NotificationRecyclerSingleRowBinding
import com.ibs.hrmioneemployee.models.api_models.notification.Result
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationsRecyclerAdapter(private val context: Context, private val notificationList: ArrayList<Result>)
    : RecyclerView.Adapter<NotificationsRecyclerAdapter.NotificationsViewHolder>() {

    inner class NotificationsViewHolder(binding: NotificationRecyclerSingleRowBinding) : RecyclerView.ViewHolder(binding.root) {

        private val notificationImage = binding.notificationImage
        private val notificationTitle = binding.notificationTitle
        private val notificationDate = binding.notificationDate
        private val notificationMessage = binding.notificationMessage

        fun setValues(result: Result) {
            notificationTitle.text = result.title
            notificationMessage.text = result.message
            notificationDate.text = convertLongToDate(result.time)
            Glide.with(context).load(result.image).apply(RequestOptions.placeholderOf(R.drawable.email).error(R.drawable.email))
                .into(notificationImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {

        val binding = NotificationRecyclerSingleRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return NotificationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        holder.setValues(notificationList[position])
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

//    @SuppressLint("SimpleDateFormat")
//    private fun convertLongToDateAndTime(date: Long): String {
//        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy | HH:mm")
//        val dateString: String = simpleDateFormat.format(date)
//        return dateString
//    }

    private val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy | hh:mm aa", Locale.ENGLISH)
    fun convertLongToDate(time: Long): String {
        return simpleDateFormat.format(time * 1000L)
    }
}