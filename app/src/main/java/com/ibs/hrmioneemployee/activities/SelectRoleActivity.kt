package com.ibs.hrmioneemployee.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.ibs.hrmioneemployee.databinding.ActivitySelectRoleBinding
import com.ibs.hrmioneemployee.utilities.SharedPreferenceSettings

class SelectRoleActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectRoleBinding
    private lateinit var sharedPreferenceSettings: SharedPreferenceSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        sharedPreferenceSettings = SharedPreferenceSettings(this)
        sharedPreferenceSettings.setIntroScreen()

        binding.llEmployee.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    override fun onStart() {
        super.onStart()
        if (sharedPreferenceSettings.getRole()){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}