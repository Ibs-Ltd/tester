package com.ibs.hrmioneemployee.models.api_models.employees_list

data class EmployeesListResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Result
)