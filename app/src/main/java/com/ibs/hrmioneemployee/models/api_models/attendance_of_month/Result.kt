package com.ibs.hrmioneemployee.models.api_models.attendance_of_month

data class Result(
    val absent: Int,
    val dateByDate: ArrayList<DateByDate>,
    val employeeName: String,
    val present: Int
)