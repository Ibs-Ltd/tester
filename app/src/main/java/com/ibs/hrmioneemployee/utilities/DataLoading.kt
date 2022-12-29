package com.ibs.hrmioneemployee.utilities

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.ibs.hrmioneemployee.R

class DataLoading(private val context: Context?) {

    val isdialog = context?.let { AlertDialog.Builder(it, R.style.loadingDialog).create() }

    fun startLoading(){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.progress_bar_layout, null)
        isdialog!!.setView(dialogView)
//        isdialog.setCancelable(false)
        isdialog.show()
    }

    fun hideLoading(){
        isdialog!!.dismiss()
    }
}