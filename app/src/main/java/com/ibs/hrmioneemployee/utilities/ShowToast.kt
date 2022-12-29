package com.ibs.hrmioneemployee.utilities

import android.content.Context
import android.widget.Toast

object ShowToast {
    fun showToast(context: Context){
        Toast.makeText(context, "You're offline", Toast.LENGTH_SHORT).show()
    }
}