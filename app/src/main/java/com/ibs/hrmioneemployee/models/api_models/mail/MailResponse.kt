package com.ibs.hrmioneemployee.models.api_models.mail

data class MailResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Any
)
