package com.ibs.hrmioneemployee.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ibs.hrmioneemployee.databinding.ActivityResetPasswordBinding
import com.ibs.hrmioneemployee.models.api_models.reset_password.ResetPasswordModel
import com.ibs.hrmioneemployee.models.api_models.reset_password.ResetPasswordResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.InternetConnection
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var sp: SharedPreferences
    private lateinit var dataLoading: DataLoading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        binding.backToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        dataLoading = DataLoading(this)

        sp = getSharedPreferences("generateOTPForResetPassword", Context.MODE_PRIVATE)
        val emailId: String = sp.getString("emailId", "").toString()

        binding.submitText.setOnClickListener {
            val password: String = binding.etPassword.text.trim().toString()
            val confirmPassword: String = binding.etConfirmPassword.text.trim().toString()

            if (password.isEmpty()){
                binding.etPassword.error = "Can't be empty"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }
            else if (password.length < 6){
                binding.etPassword.error = "Password must be minimum 6 characters"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()){
                binding.etConfirmPassword.error = "Can't be empty"
                binding.etConfirmPassword.requestFocus()
                return@setOnClickListener
            }
            else if (confirmPassword != password){
                binding.etConfirmPassword.error = "Password and confirm password are not same"
                binding.etConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            val resetPasswordModel = ResetPasswordModel(emailId, password)

            if (InternetConnection.checkConnection(this)){
                callResetPasswordApi(resetPasswordModel)
            }
            else{
                Toast.makeText(this, "You're offline", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callResetPasswordApi(resetPasswordModel: ResetPasswordModel) {

        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<ResetPasswordResponse> = apiServices.resetPassword(resetPasswordModel)

        call.enqueue(object : Callback<ResetPasswordResponse>{
            override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {

                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.code == 200){
                        startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                    Toast.makeText(this@ResetPasswordActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }
                else{
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@ResetPasswordActivity, "Error Code: " + response.code()
                            + "\nMessage: " + jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                Toast.makeText(this@ResetPasswordActivity, "Failure $t", Toast.LENGTH_SHORT).show()
                dataLoading.hideLoading()
            }
        })
    }
}