package com.ibs.hrmioneemployee.models.api_models.create_leave

data class CreateLeaveModel(
    val employeeId: Int,
    val startDate: Long,
    val endDate: Long,
    val leaveTypeId: Int,
    val reason: String,
)