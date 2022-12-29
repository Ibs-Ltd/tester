package com.ibs.hrmioneemployee.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ibs.hrmioneemployee.databinding.ActivityForgotBinding
import com.ibs.hrmioneemployee.models.api_models.generateOTPForResetPassword.otpForResetPasswordResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.InternetConnection
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerateOtpForResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotBinding
    private lateinit var dataLoading: DataLoading

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataLoading = DataLoading(this)

        binding.submitText.setOnClickListener {

            val emailId: String = binding.etEmailId.text.toString()

            if (emailId.isEmpty()) {
                binding.etEmailId.error = "Enter your email id"
                binding.etEmailId.requestFocus()
                return@setOnClickListener
            }
            else if (!emailId.contains("@") && !emailId.contains(".com")) {
                binding.etEmailId.error = "Enter a valid email"
                binding.etEmailId.requestFocus()
                return@setOnClickListener
            }

            val sp: SharedPreferences = getSharedPreferences("generateOTPForResetPassword", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sp.edit()
            editor.putString("emailId", emailId)
            editor.apply()

            if (InternetConnection.checkConnection(this)){
                callForgotPasswordApi(emailId)
            }
            else{
                Toast.makeText(this, "You're offline", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backToLogin.setOnClickListener {
            onBackPressed()
        }

        binding.llBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun callForgotPasswordApi(emailId: String) {

        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<otpForResetPasswordResponse> = apiServices.generateOTPForResetPassword(emailId)

        call.enqueue(object : Callback<otpForResetPasswordResponse> {
            override fun onResponse(call: Call<otpForResetPasswordResponse>, response: Response<otpForResetPasswordResponse>) {

                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.code == 200) {
                        val intent = Intent(this@GenerateOtpForResetPasswordActivity, OtpActivity::class.java)
                        intent.putExtra("PageStatus", "Forgot")
                        startActivity(intent)
                    }
//                    Toast.makeText(this@ForgotActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }

                else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@GenerateOtpForResetPasswordActivity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<otpForResetPasswordResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@GenerateOtpForResetPasswordActivity, "Failure $t", Toast.LENGTH_SHORT).show()
            }
        })
    }
}