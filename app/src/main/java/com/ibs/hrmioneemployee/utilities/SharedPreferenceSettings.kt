package com.ibs.hrmioneemployee.utilities

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceSettings(var context: Context) {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    companion object {
        const val SHARED_PREF_NAME = "Settings_Preference"
        const val CHECK_IN_OUT_PREF = "Check_In_Out_Preference"
        const val TODAY_DATE_PREF = "Today_Date_Preference"
//        const val PROFILE_CLICK_PREF = "Profile_Click_Preference"
    }

    fun saveToggleButtonSettings(string: String){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString("Toggle", string)
        editor.apply()
    }

    fun getToggleButtonSettings(): String{
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("Toggle", "Office").toString()
    }

    fun saveCheckInCheckOutKeyForWFH(key:Int){
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putInt("keyWFH", key)
        editor.apply()
    }

    fun getCheckInCheckOutKeyForWFH(): Int{
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        return sharedPreferences.getInt("keyWFH", 0)
    }

    fun saveCheckInCheckOutKeyForWFO(key:Int){
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putInt("keyWFO", key)
        editor.apply()
    }

    fun getCheckInCheckOutKeyForWFO(): Int{
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        return sharedPreferences.getInt("keyWFO", 0)
    }

    fun saveCheckInDataForWFH(inTime:String){
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString("checkInKeyWFH", inTime)
        editor.apply()
    }

    fun getCheckInDataForWFH(): String{
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        return sharedPreferences.getString("checkInKeyWFH", "--:--").toString()
    }

    fun saveCheckInDataForWFO(inTime:String){
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString("checkInKeyWFO", inTime)
        editor.apply()
    }

    fun getCheckInDataForWFO(): String{
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        return sharedPreferences.getString("checkInKeyWFO", "--:--").toString()
    }

    fun saveCheckOutDataForWFH(outTime:String, workingHr:String){
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString("checkOutTimeWFH", outTime)
        editor.putString("workingHrTimeWFH", workingHr)
        editor.apply()
    }

    fun getCheckOutTimeForWFH(): String{
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        return sharedPreferences.getString("checkOutTimeWFH", "--:--").toString()
    }

    fun getWorkingHrTimeForWFH(): String{
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        return sharedPreferences.getString("workingHrTimeWFH", "--:--").toString()
    }

    fun saveCheckOutDataForWFO(outTime:String, workingHr:String){
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString("checkOutTimeWFO", outTime)
        editor.putString("workingHrTimeWFO", workingHr)
        editor.apply()
    }

    fun getCheckOutTimeForWFO(): String{
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        return sharedPreferences.getString("checkOutTimeWFO", "--:--").toString()
    }

    fun getWorkingHrTimeForWFO(): String{
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        return sharedPreferences.getString("workingHrTimeWFO", "--:--").toString()
    }

    fun clearCheckInCheckOut(){
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun saveTodayDate(date: String){
        sharedPreferences = context.getSharedPreferences(TODAY_DATE_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString("todayDate", date)
        editor.apply()
    }

    fun getTodayDate(): String{
        sharedPreferences = context.getSharedPreferences(TODAY_DATE_PREF, Context.MODE_PRIVATE)
        return sharedPreferences.getString("todayDate", "").toString()
    }

//    fun saveProfileClickData(int: Int){
//        sharedPreferences = context.getSharedPreferences(PROFILE_CLICK_PREF, Context.MODE_PRIVATE)
//        editor = sharedPreferences.edit()
//        editor.putInt("profileClick", int)
//        editor.apply()
//    }
//
//    fun getProfileClickData(): Int{
//        sharedPreferences = context.getSharedPreferences(PROFILE_CLICK_PREF, Context.MODE_PRIVATE)
//        return sharedPreferences.getInt("profileClick", 0)
//    }

    fun setHomepage(){
        sharedPreferences = context.getSharedPreferences("HomePage", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putBoolean("homepage", true)
        editor.apply()
    }

    fun getHomepage() : Boolean{
        sharedPreferences = context.getSharedPreferences("HomePage", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("homepage", false)
    }

    fun setIntroScreen(){
        sharedPreferences = context.getSharedPreferences("IntroScreen", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putBoolean("intro_screen", true)
        editor.apply()
    }

    fun getIntroScreen(): Boolean{
        sharedPreferences = context.getSharedPreferences("IntroScreen", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("intro_screen", false)
    }

    fun setRole(){
        sharedPreferences = context.getSharedPreferences("Role", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putBoolean("role", true)
        editor.apply()
    }

    fun getRole(): Boolean{
        sharedPreferences = context.getSharedPreferences("Role", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("role", false)
    }
}