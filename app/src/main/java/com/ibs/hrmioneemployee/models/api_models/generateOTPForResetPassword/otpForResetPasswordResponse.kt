package com.ibs.hrmioneemployee.models.api_models.generateOTPForResetPassword

data class otpForResetPasswordResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Any
)