package com.ibs.hrmioneemployee.models.api_models.attendance_check_in_out

data class Result(
    val checkIn: Long,
    val checkOut: Long,
    val date: String,
    val designation: String,
    val employeeId: Int,
    val employeeName: String,
    val id: Int,
    val isPresent: Int,
    val overTime: Int,
    val profilePicturePath: String,
    val workingHours: Long,
    val workingLocation: Any
)