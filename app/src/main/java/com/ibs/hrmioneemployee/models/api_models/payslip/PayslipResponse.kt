package com.ibs.hrmioneemployee.models.api_models.payslip

data class PayslipResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: ArrayList<Result>
)