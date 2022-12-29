package com.ibs.hrmioneemployee.models.api_models.signature

data class GetSignatureResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Result
)