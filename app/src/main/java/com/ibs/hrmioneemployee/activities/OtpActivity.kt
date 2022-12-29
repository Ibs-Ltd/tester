package com.ibs.hrmioneemployee.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.ActivityOtpBinding
import com.ibs.hrmioneemployee.models.api_models.generateOTPForResetPassword.otpForResetPasswordResponse
import com.ibs.hrmioneemployee.models.api_models.verify_otp.VerifyOtpModel
import com.ibs.hrmioneemployee.models.api_models.verify_otp.VerifyOtpResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.InternetConnection
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private lateinit var spForgot: SharedPreferences
//    private lateinit var spSignUp: SharedPreferences
//    private lateinit var s: String
    private lateinit var dataLoading: DataLoading

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        dataLoading = DataLoading(this)

//        s = intent.getStringExtra("PageStatus").toString()
//        spSignUp = getSharedPreferences("SIGN_UP_DATA", Context.MODE_PRIVATE)
        spForgot = getSharedPreferences("generateOTPForResetPassword", Context.MODE_PRIVATE)
        val emailId: String = spForgot.getString("emailId", "").toString()
        binding.otpDescription.text = "${getString(R.string.otp_description)} $emailId"

//        if (s == "SignUp") {
//            binding.otpDescription.text = "${getString(R.string.otp_description)} ${spSignUp.getString("emailId", "").toString()}"
//
//        } else if (s == "Forgot") {
//            binding.otpDescription.text = "${getString(R.string.otp_description)} ${spForgot.getString("emailId", "").toString()}"
//        }

        binding.submitText.setOnClickListener {

            val otp: String = binding.pinView.text.toString()

            if (binding.pinView.length() != 4) {
                binding.pinView.error = "Enter otp"
                binding.pinView.requestFocus()
                return@setOnClickListener
            }
            val verifyOtpModel = VerifyOtpModel(emailId, otp)
            if (InternetConnection.checkConnection(this)){
                callVerificationOtpApi(verifyOtpModel)
            }
            else{
                Toast.makeText(this, "You're offline", Toast.LENGTH_SHORT).show()
            }

//            if (s == "SignUp") {
//                val emailId: String = spSignUp.getString("emailId", "").toString()
//                val verifyOtpModel = VerifyOtpModel(emailId, role, otp)
//                callOtpVerificationApi(verifyOtpModel)
//            }
//
//            else if (s == "Forgot") {
//                val emailId: String = spForgot.getString("emailId", "").toString()
//                val verifyOtpModel = VerifyOtpModel(emailId, role, otp)
//                callOtpVerificationApi(verifyOtpModel)
//            }
        }

        binding.resendOtpText.setOnClickListener {

            if (InternetConnection.checkConnection(this)){
                resendOtpByCallingGenerateOtpApi()
            }
            else{
                Toast.makeText(this, "You're offline", Toast.LENGTH_SHORT).show()
            }
//            if (s.equals("SignUp")) {
//                resendOtpByCallingSignUpApi()
//            } else if (s.equals("Forgot")) {
//                resendOtpByCallingGenerateOtpApi()
//            }
//            try {
//                Thread.sleep(5000)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
        }
    }

    private fun callVerificationOtpApi(verifyOtpModel: VerifyOtpModel) {

        dataLoading.startLoading()

        val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<VerifyOtpResponse> = apiServices.verifyOTP(verifyOtpModel)

        call.enqueue(object : Callback<VerifyOtpResponse>{
            override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>
            ) {

                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        startActivity(Intent(this@OtpActivity, ResetPasswordActivity::class.java))
                    }
                    Toast.makeText(this@OtpActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }
                else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@OtpActivity, "Error Code: " + response.code()
                            + "\nMessage: " + jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@OtpActivity, "Failure$t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun resendOtpByCallingGenerateOtpApi() {

        dataLoading.startLoading()

        val emailId: String = spForgot.getString("emailId", "").toString()
        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<otpForResetPasswordResponse> = apiServices.generateOTPForResetPassword(emailId)

        call.enqueue(object : Callback<otpForResetPasswordResponse> {
            override fun onResponse(call: Call<otpForResetPasswordResponse>, response: Response<otpForResetPasswordResponse>) {

                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.code == 200) {
                        Toast.makeText(this@OtpActivity, "OTP has been sent", Toast.LENGTH_SHORT).show()
                    }
//                    Toast.makeText(this@OtpActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }

                else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@OtpActivity, "Error Code: " + response.code()
                                + "\nMessage: " + jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<otpForResetPasswordResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@OtpActivity, "Failure $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    private fun resendOtpByCallingSignUpApi() {
//
//        val dialog = ProgressDialog(this)
//        dialog.setMessage("Loading... please wait")
//        dialog.show()
//
//        val firstName: String = spSignUp.getString("firstName", "").toString()
//        val lastName: String = spSignUp.getString("lastName", "").toString()
//        val emailId: String = spSignUp.getString("emailId", "").toString()
//        val fullMobileNumber: String = spSignUp.getString("fullMobileNumber", "").toString()
//        val password: String = spSignUp.getString("password", "").toString()
//
//        val signUpModel = SignUpModel(firstName, lastName, emailId, fullMobileNumber, password, role)
//
//        val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
//        val call: Call<SignUpResponse> = apiServices.createUser(signUpModel)
//
//        call.clone().enqueue(object : Callback<SignUpResponse> {
//            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
//
//                if (response.body() != null && response.isSuccessful) {
//                    if (response.body()!!.code == 200) {
//                        Toast.makeText(this@OtpActivity, "OTP has been sent", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                else {
//                    val jObjError = JSONObject(response.errorBody()!!.string())
//                    Toast.makeText(this@OtpActivity, "Error Code: " + response.code()
//                            + "\nMessage: " + jObjError.getString("message"), Toast.LENGTH_SHORT).show()
//                }
//                dialog.dismiss()
//            }
//
//            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
//                dialog.dismiss()
//                Toast.makeText(this@OtpActivity, "Failure$t", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

//    private fun callOtpVerificationApi(verifyOtpModel: VerifyOtpModel) {
//
//        val dialog = ProgressDialog(this)
//        dialog.setMessage("Loading... please wait")
//        dialog.show()
//
//        val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
//        val call: Call<VerifyOtpResponse> = apiServices.verifyOTP(verifyOtpModel)
//
//        call.enqueue(object : Callback<VerifyOtpResponse> {
//            override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>) {
//
//                if (response.isSuccessful && response.body() != null) {
//                    if (response.body()!!.code == 200) {
//                        if (s == "SignUp"){
//                            startActivity(Intent(this@OtpActivity, LoginActivity::class.java))
//                            finish()
//                        }
//                        else if (s == "Forgot"){
//                            startActivity(Intent(this@OtpActivity, ResetPasswordActivity::class.java))
//                        }
//                    }
//                    Toast.makeText(this@OtpActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
//                }
//                else {
//                    val jObjError = JSONObject(response.errorBody()!!.string())
//                    Toast.makeText(this@OtpActivity, "Error Code: " + response.code()
//                            + "\nMessage: " + jObjError.getString("message"), Toast.LENGTH_SHORT).show()
//                }
//                dialog.dismiss()
//            }
//
//            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
//                dialog.dismiss()
//                Toast.makeText(this@OtpActivity, "Failure$t", Toast.LENGTH_SHORT).show()
//            }
//        })
//
//    }
}