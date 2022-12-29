package com.ibs.hrmioneemployee.models.api_models.leave_type_list

data class LeaveTypeListResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: ArrayList<Result>
)