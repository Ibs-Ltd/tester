package com.ibs.hrmioneemployee.models.api_models.homepage

data class HomepageResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Result
)