package com.ibs.hrmioneemployee.models.api_models.signature

data class PostSignatureResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Any
)