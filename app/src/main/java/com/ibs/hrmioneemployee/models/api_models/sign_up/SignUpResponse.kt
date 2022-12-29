package com.ibs.hrmioneemployee.models.api_models.sign_up

data class SignUpResponse(
    val code: Int,
    val data: Any,
    val error: Any,
    val message: String,
    val token: Any,
    val user: Any
)