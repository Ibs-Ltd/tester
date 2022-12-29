package com.ibs.hrmioneemployee.utilities

import android.content.Context
import android.net.ConnectivityManager

object InternetConnection {
    /** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT  */
    fun checkConnection(context: Context): Boolean {

        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connMgr.activeNetworkInfo

        if (activeNetworkInfo != null) { // connected to the internet

            if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true
            }
            else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true
            }

        }
        return false
    }
}