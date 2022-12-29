package com.ibs.hrmioneemployee.models.api_models.verify_otp

data class VerifyOtpModel(
    val email: String,
    val otp: String
)