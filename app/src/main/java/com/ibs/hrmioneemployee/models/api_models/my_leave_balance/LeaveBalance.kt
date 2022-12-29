package com.ibs.hrmioneemployee.models.api_models.my_leave_balance

data class LeaveBalance(
    val leaveName: String,
    val noOFLeaves: Int,
    val totalNoOfLeave: Int
)