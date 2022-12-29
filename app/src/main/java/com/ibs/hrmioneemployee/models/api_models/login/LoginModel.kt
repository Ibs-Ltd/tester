package com.ibs.hrmioneemployee.models.api_models.login

data class LoginModel(
    val email: String,
    val password: String,
    val deviceAddress: String,
    val lastLoginLocation: String,
    val deviceType: String,
    val deviceToken: String
)