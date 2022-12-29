package com.ibs.hrmioneemployee.models.api_models.attendance_history

data class AttendanceHistoryResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: ArrayList<Result>
)