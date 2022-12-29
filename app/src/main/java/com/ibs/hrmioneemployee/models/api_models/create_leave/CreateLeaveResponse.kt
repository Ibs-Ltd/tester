package com.ibs.hrmioneemployee.models.api_models.create_leave

data class CreateLeaveResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Any
)