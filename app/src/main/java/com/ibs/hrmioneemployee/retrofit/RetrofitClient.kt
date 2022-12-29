package com.ibs.hrmioneemployee.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {

        //old
        //private const val BASE_URL = "http://18.222.217.198/"

        private const val BASE_URL = "http://18.218.231.50/"

        private var INSTANCE: Retrofit? = null

        fun getRetrofit(): Retrofit {
            if (INSTANCE == null) {
                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return INSTANCE!!
        }
    }
}