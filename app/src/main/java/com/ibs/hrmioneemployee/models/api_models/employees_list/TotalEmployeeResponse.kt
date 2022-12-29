package com.ibs.hrmioneemployee.models.api_models.employees_list

import java.io.Serializable

data class TotalEmployeeResponse(
    val accountHolderName: Any,
    val accountNumber: Any,
    val age: Any,
    val bankName: Any,
    val branchAddress: Any,
    val countryCode: Any,
    val currentAddress: Any,
    val dateOfJoining: Any,
    val degreeEducation: Any,
    val department: Any,
    val designation: String,
    val deviceAddress: Any,
    val deviceName: Any,
    val dob: String,
    val email: String,
    val employeeId: String,
    val firstName: String,
    val gender: Any,
    val highSchool: Any,
    val id: Int,
    val ifscCode: Any,
    val intermediate: Any,
    val lastName: String,
    val maritalStatus: Any,
    val mobileNumber: String,
    val nationality: Any,
    val profilePicturePath: Any,
    val shiftDetails: Any,
    val skypeId: Any,
    val userName: String,
    val workAddress: Any
) : Serializable