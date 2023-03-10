package com.ibs.hrmioneemployee.models.api_models.login

data class Result(
    val accountHolderName: String,
    val accountNumber: String,
    val age: Any,
    val bankName: String,
    val branchAddress: String,
    val countryCode: Int,
    val currentAddress: String,
    val dateOfJoining: String,
    val degreeEducation: String,
    val department: String,
    val designation: String,
    val dob: String,
    val email: String,
    val employeeId: Int,
    val firstName: String,
    val gender: String,
    val highSchool: String,
    val id: Int,
    val ifscCode: String,
    val intermediate: String,
    val lastName: String,
    val deviceAddress: String,
    val maritalStatus: String,
    val mobileNumber: String,
    val nationality: String,
    val profilePicturePath: String,
    val shiftDetails: String,
    val userName: String,
    val workAddress: String
)