package com.ibs.hrmioneemployee.view_models

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibs.hrmioneemployee.models.api_models.employees_list.EmployeesListResponse
import com.ibs.hrmioneemployee.repository.Repository

class MainViewModel(private val repository: Repository) : ViewModel() {

    private var employeesList = MutableLiveData<EmployeesListResponse>()

    fun callEmployeesListApi(authorization: String, employeeId : Int, context: Context){
        repository.callEmployeesListApi(authorization, employeeId, context)
    }

    fun getEmployeesList() : MutableLiveData<EmployeesListResponse>{
        employeesList = repository.getEmployeesList()
        return employeesList
    }

}