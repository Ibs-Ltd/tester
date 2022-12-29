package com.ibs.hrmioneemployee.models.api_models.my_leave_balance

data class LeaveHistory(
    val id: Int,
    val creationTime: Long,
    val startDate: Long,
    val endDate: Long,
    val leaveName: String,
    val status: String
)