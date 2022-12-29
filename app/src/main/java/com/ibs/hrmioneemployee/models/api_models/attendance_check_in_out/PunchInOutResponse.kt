package com.ibs.hrmioneemployee.models.api_models.attendance_check_in_out

data class PunchInOutResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Result
)