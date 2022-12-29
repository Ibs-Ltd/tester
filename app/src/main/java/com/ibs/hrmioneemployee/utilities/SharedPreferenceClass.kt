package com.ibs.hrmioneemployee.utilities

import android.content.Context
import android.content.SharedPreferences
import com.ibs.hrmioneemployee.models.api_models.login.LoginResponse

class SharedPreferenceClass(var context: Context) {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    companion object {
        const val SHARED_PREF_NAME = "Login_Preference"
        const val LOGIN_TOKEN = "LoginToken"
        const val PROFILE_PICTURE_PATH = "profilePicturePath"
        const val USER_ID = "UserId"
        const val FIRST_NAME = "FirstName"
        const val LAST_NAME = "LastName"
        const val DOB = "DOB"
        const val GENDER = "Gender"
        const val MARITAL_STATUS = "MaritalStatus"
        const val NATIONALITY = "Nationality"
        const val USER_NAME = "Username"
        const val EMAIL_ID = "EmailId"
        const val COUNTRY_CODE = "CountryCode"
        const val MOBILE_NUMBER = "MobileNumber"
        const val CURRENT_ADDRESS = "CurrentAddress"
        const val DEGREE_EDUCATION = "DegreeEducation"
        const val INTERMEDIATE = "Intermediate"
        const val HIGH_SECONDARY = "HighSecondary"
        const val EMPLOYEE_ID = "EmployeeId"
        const val DATE_OF_JOINING = "DateOfJoining"
        const val DEPARTMENT = "Department"
        const val DESIGNATION = "Designation"
        const val SHIFT_DETAILS = "ShiftDetails"
        const val WORK_ADDRESS = "WorkAddress"
        const val BANK_NAME = "BankName"
        const val ACCOUNT_HOLDER_NAME = "AccountHolderName"
        const val ACCOUNT_NUMBER = "AccountNumber"
        const val BRANCH_ADDRESS = "BranchAddress"
        const val IFSC_CODE = "IfscCode"
        const val DEVICE_ADDRESS = "deviceAddress"
    }

    fun setString(key: String, value: String){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setInt(key: String, value: Int){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

//    fun saveLoginResponse(loginResponse: LoginResponse){
//        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
//        editor = sharedPreferences.edit()
//
//        //  personal
//        editor.putString("profilePicturePath", loginResponse.result.profilePicturePath)
//        editor.putInt("UserId", loginResponse.result.id)
//        editor.putString("FirstName", loginResponse.result.firstName)
//        editor.putString("LastName", loginResponse.result.lastName)
//        editor.putString("DOB", loginResponse.result.dob)
//        editor.putString("Gender", loginResponse.result.gender)
//        editor.putString("MaritalStatus", loginResponse.result.maritalStatus)
//        editor.putString("Nationality", loginResponse.result.nationality)
//        editor.putString("Username", loginResponse.result.userName)
//        editor.putString("EmailId", loginResponse.result.email)
//        editor.putInt("CountryCode", loginResponse.result.countryCode)
//        editor.putString("MobileNumber", loginResponse.result.mobileNumber)
//        editor.putString("CurrentAddress", loginResponse.result.currentAddress)
//        editor.putString("DegreeEducation", loginResponse.result.degreeEducation)
//        editor.putString("Intermediate", loginResponse.result.intermediate)
//        editor.putString("HighSecondary", loginResponse.result.highSchool)
//        //  work
//        editor.putInt("EmployeeId", loginResponse.result.employeeId)
//        editor.putString("DateOfJoining", loginResponse.result.dateOfJoining)
//        editor.putString("Department", loginResponse.result.department)
//        editor.putString("Designation", loginResponse.result.designation)
//        editor.putString("ShiftDetails", loginResponse.result.shiftDetails)
//        editor.putString("WorkAddress", loginResponse.result.workAddress)
//        //  bank
//        editor.putString("BankName", loginResponse.result.bankName)
//        editor.putString("AccountHolderName", loginResponse.result.accountHolderName)
//        editor.putString("AccountNumber", loginResponse.result.accountNumber)
//        editor.putString("BranchAddress", loginResponse.result.branchAddress)
//        editor.putString("IfscCode", loginResponse.result.ifscCode)
//        editor.putString("deviceAddress", loginResponse.result.deviceAddress)
//
//        editor.putBoolean("Logged", true)
//        editor.apply()
//
//    }

    fun getLoginToken() : String{
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return  sharedPreferences.getString(LOGIN_TOKEN, "").toString()
    }

    fun loggedIn(){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putBoolean("Logged", true)
        editor.apply()
    }

    fun isLoggedIn(): Boolean{
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("Logged", false)
    }

    fun logOut(){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}