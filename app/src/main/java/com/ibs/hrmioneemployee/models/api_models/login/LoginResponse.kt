package com.ibs.hrmioneemployee.models.api_models.login

data class LoginResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Result,
    val token: String
)