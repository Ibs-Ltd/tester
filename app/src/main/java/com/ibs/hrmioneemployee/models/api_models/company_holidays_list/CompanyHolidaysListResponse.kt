package com.ibs.hrmioneemployee.models.api_models.company_holidays_list

data class CompanyHolidaysListResponse(
    val code: Int,
    val error: Any,
    val message: String,
    val result: ArrayList<Result>
)