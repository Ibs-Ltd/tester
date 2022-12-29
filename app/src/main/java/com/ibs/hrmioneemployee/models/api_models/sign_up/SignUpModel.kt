package com.ibs.hrmioneemployee.models.api_models.sign_up

import com.ibs.hrmioneemployee.models.api_models.role_for_all_models.Role

data class SignUpModel(
    val firstName: String,
    val lastName: String,
    val email: String,
    val mobileNumber: String,
    val password: String,
    val role: Role
)