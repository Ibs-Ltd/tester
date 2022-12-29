package com.ibs.hrmioneemployee.models.api_models.employees_list

data class Result(
    val numberOfEmployees: Int,
    val totalEmployeeResponse: ArrayList<TotalEmployeeResponse>
)