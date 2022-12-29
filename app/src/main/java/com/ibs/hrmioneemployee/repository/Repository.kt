package com.ibs.hrmioneemployee.repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.ibs.hrmioneemployee.models.api_models.employees_list.EmployeesListResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.utilities.DataLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class Repository(private val apiServices: ApiServices) {

    private val employeesList = MutableLiveData<EmployeesListResponse>()
    private lateinit var dataLoading: DataLoading

    fun getEmployeesList(): MutableLiveData<EmployeesListResponse> {
        return employeesList
    }

    fun callEmployeesListApi(authorization: String, employeeId: Int, context: Context) {

        dataLoading = DataLoading(context)
        dataLoading.startLoading()

        MainScope().launch {
            withContext(Dispatchers.IO) {
                val result = apiServices.getEmployeesList(authorization, employeeId)
                if (result.isSuccessful && result.body() != null) {
                    employeesList.postValue(result.body())
                    dataLoading.hideLoading()
                } else {
                    val jObjError = JSONObject(result.errorBody()!!.string())
                    Toast.makeText(context, jObjError.getString("message"), Toast.LENGTH_SHORT)
                        .show()
                    dataLoading.hideLoading()
                }
            }
        }
    }


}