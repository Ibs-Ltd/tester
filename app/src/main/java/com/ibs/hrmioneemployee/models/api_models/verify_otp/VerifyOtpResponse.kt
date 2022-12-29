package com.ibs.hrmioneemployee.models.api_models.verify_otp

data class VerifyOtpResponse(
    val code: Int,
    val error: String,
    val message: String,
    val result: Any
)