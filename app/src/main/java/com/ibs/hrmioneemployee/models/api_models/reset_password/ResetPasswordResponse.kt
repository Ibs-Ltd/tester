package com.ibs.hrmioneemployee.models.api_models.reset_password

data class ResetPasswordResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Any
)