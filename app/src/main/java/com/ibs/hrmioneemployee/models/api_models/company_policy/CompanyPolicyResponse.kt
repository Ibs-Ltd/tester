package com.ibs.hrmioneemployee.models.api_models.company_policy

data class CompanyPolicyResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: String
)