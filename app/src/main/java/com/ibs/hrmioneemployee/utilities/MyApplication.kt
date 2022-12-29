package com.ibs.hrmioneemployee.utilities

import android.app.Application
import com.ibs.hrmioneemployee.repository.Repository
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient

class MyApplication : Application() {

    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        repository = Repository(apiServices)
    }
}