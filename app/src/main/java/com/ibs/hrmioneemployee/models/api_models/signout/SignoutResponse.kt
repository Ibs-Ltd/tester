package com.ibs.hrmioneemployee.models.api_models.signout

data class SignoutResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Any
)