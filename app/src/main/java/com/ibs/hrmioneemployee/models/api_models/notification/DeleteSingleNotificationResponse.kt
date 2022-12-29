package com.ibs.hrmioneemployee.models.api_models.notification

data class DeleteSingleNotificationResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Any
)