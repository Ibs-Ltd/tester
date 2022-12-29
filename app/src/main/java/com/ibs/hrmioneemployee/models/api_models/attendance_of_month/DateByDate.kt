package com.ibs.hrmioneemployee.models.api_models.attendance_of_month

data class DateByDate(
    val checkIn: Long,
    val checkOut: Long,
    val date: String,
    val dateAndMonth: String,
    val designation: String,
    val employeeId: Int,
    val employeeName: String,
    val id: Int,
    val isPresent: Int,
    val overTime: Int,
    val profilePicturePath: String,
    val workingHours: Int,
    val isHoliday: Int,
    val workingLocation: Any
)