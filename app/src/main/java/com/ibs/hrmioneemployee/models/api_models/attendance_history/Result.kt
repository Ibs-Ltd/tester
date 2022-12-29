package com.ibs.hrmioneemployee.models.api_models.attendance_history

import java.io.Serializable

data class Result(
    val checkIn: Long,
    val checkOut: Long,
    val date: String,
    val designation: String,
    val employeeId: Int,
    val employeeName: String,
    val id: Int,
    val isPresent: Int,
    val isCheckedIn: Int,
    val isCheckedOut: Int,
    val overTime: Int,
    val isHoliday: Int,
    val profilePicturePath: String,
    val workingHours: Long,
    val totalWorkingHours: Long,
    val workingLocation: String,
    val dateAndMonth: String,
) : Serializable