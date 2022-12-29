package com.ibs.hrmioneemployee.models.api_models.notification

data class Result(
    val id: Int,
    val image: Any,
    val message: String,
    val time: Long,
    val title: String
)