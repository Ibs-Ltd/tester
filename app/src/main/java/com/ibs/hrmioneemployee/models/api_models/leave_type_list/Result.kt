package com.ibs.hrmioneemployee.models.api_models.leave_type_list

data class Result(
    val id: Int,
    val leaveTypeName: String,
    val remainingLeaves: Int
)