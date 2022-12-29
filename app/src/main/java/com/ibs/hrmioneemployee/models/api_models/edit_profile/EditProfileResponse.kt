package com.ibs.hrmioneemployee.models.api_models.edit_profile

data class EditProfileResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: Result
)