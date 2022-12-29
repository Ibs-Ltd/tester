package com.ibs.hrmioneemployee.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.ActivityEmployeeFullDetailsBinding
import com.ibs.hrmioneemployee.models.api_models.employees_list.TotalEmployeeResponse

class EmployeeFullDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeFullDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmployeeFullDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        val totalEmployeeResponse: TotalEmployeeResponse = intent.extras!!.getSerializable("EmployeeFullDetails") as TotalEmployeeResponse

        binding.employeeName.text = "${totalEmployeeResponse.firstName} ${totalEmployeeResponse.lastName}"
        binding.jobRole.text = totalEmployeeResponse.designation
        binding.birthday.text = totalEmployeeResponse.dob
        if (totalEmployeeResponse.countryCode == null || totalEmployeeResponse.countryCode == ""){
            binding.mobileNumber.text = totalEmployeeResponse.mobileNumber
        }
        else{
            binding.mobileNumber.text = "${totalEmployeeResponse.countryCode} ${totalEmployeeResponse.mobileNumber}"
        }
        binding.email.text = totalEmployeeResponse.email
        Glide.with(this).load(totalEmployeeResponse.profilePicturePath).apply(
            RequestOptions.placeholderOf(R.drawable.dashboard_profile).error(R.drawable.dashboard_profile)).into(binding.employeeFullPhoto)
    }
}