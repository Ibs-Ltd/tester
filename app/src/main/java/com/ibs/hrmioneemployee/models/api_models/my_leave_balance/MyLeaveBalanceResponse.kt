package com.ibs.hrmioneemployee.models.api_models.my_leave_balance

data class MyLeaveBalanceResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Result
)