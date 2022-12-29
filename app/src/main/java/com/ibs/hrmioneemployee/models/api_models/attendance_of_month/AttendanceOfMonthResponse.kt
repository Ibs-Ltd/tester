package com.ibs.hrmioneemployee.models.api_models.attendance_of_month

data class AttendanceOfMonthResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Result
)