package com.ibs.hrmioneemployee.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ibs.hrmioneemployee.databinding.ActivitySignUpBinding
import com.ibs.hrmioneemployee.models.api_models.role_for_all_models.Role
import com.ibs.hrmioneemployee.models.api_models.sign_up.SignUpModel
import com.ibs.hrmioneemployee.models.api_models.sign_up.SignUpResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        binding.etFirstName.setText("Aamir")
        binding.etLastName.setText("Khan")
        binding.etEmailId.setText("aamir@interestbudsolutions.com")
        binding.etMobileNumber.setText("9876567890")
        binding.etPassword.setText("123456")

        binding.signUpText.setOnClickListener {

            val firstName: String = binding.etFirstName.text.trim().toString()
            val lastName: String = binding.etLastName.text.trim().toString()
            val emailId: String = binding.etEmailId.text.trim().toString()
            val mobileNumber: String = binding.etMobileNumber.text.trim().toString()
            val password: String = binding.etPassword.text.trim().toString()
            binding.ccp.registerCarrierNumberEditText(binding.etMobileNumber).toString()
            val fullMobileNumber: String = binding.ccp.fullNumberWithPlus


            if (firstName.isEmpty()) {
                binding.etFirstName.error = "First name can't be empty"
                binding.etFirstName.requestFocus()
                return@setOnClickListener
            } else if (!firstName.matches("^[A-Za-z]+$".toRegex())) {
                binding.etFirstName.error = "Enter a valid first name"
                binding.etFirstName.requestFocus()
                return@setOnClickListener
            }


            if (lastName.isEmpty()) {
                binding.etLastName.error = "Last name can't be empty"
                binding.etLastName.requestFocus()
                return@setOnClickListener
            } else if (!lastName.matches("^[A-Za-z]+$".toRegex())) {
                binding.etLastName.error = "Enter a valid last name"
                binding.etLastName.requestFocus()
                return@setOnClickListener
            }


            if (emailId.isEmpty()) {
                binding.etEmailId.error = "Email can't be empty"
                binding.etEmailId.requestFocus()
                return@setOnClickListener
            }
            else if (!emailId.contains("@") && !emailId.contains(".com")) {
                binding.etEmailId.error = "Enter a valid email"
                binding.etEmailId.requestFocus()
                return@setOnClickListener
            }


            if (mobileNumber.isEmpty()) {
                binding.etMobileNumber.error = "Mobile number can't be empty"
                binding.etMobileNumber.requestFocus()
                return@setOnClickListener
            } else if (mobileNumber.length != 10) {
                binding.etMobileNumber.error = "Enter a valid mobile number"
                binding.etMobileNumber.requestFocus()
                return@setOnClickListener
            }


            if (password.isEmpty()) {
                binding.etPassword.error = "Password can't be empty"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            } else if (password.length < 6) {
                binding.etPassword.error = "Password must be minimum 6 characters"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            val sp: SharedPreferences = getSharedPreferences("SIGN_UP_DATA", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sp.edit()
            editor.putString("firstName", firstName)
            editor.putString("lastName", lastName)
            editor.putString("emailId", emailId)
            editor.putString("fullMobileNumber", fullMobileNumber)
            editor.putString("password", password)
            editor.apply()

            val role = Role("EMPLOYEE")
            val signUpModel = SignUpModel(firstName, lastName, emailId, fullMobileNumber, password, role)

            callSignUpApi(signUpModel)
        }

        binding.loginText.setOnClickListener {
            onBackPressed()
        }
    }

    private fun callSignUpApi(signUpModel: SignUpModel) {

        val dialog = ProgressDialog(this)
        dialog.setMessage("Loading... please wait")
        dialog.show()

//        val g = Gson()
//        val gs: String = g.toJson(signUpModel)
//        Log.d("check", gs)

        val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<SignUpResponse> = apiServices.createUser(signUpModel)

        call.clone().enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {

                if (response.body() != null && response.isSuccessful) {

                    if (response.body()!!.code == 200) {
                        val intent = Intent(this@SignUpActivity, OtpActivity::class.java)
                        intent.putExtra("PageStatus", "SignUp")
                        startActivity(intent)
                    }

                    Toast.makeText(this@SignUpActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }

                else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@SignUpActivity, "Error Code: " + response.code()
                            + "\nMessage: " + jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                dialog.dismiss()
                Toast.makeText(this@SignUpActivity, "Failure$t", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
