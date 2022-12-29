package com.ibs.hrmioneemployee.models.api_models.my_leave_balance

data class Result(
    val leaveBalances: ArrayList<LeaveBalance>,
    val leaveHistory: ArrayList<LeaveHistory>,
    val requestedLeaves: ArrayList<RequestedLeave>,
    val remainingLeaves: Int,
    val totalLeaves: Int,
    val usedLeaves: Int
)