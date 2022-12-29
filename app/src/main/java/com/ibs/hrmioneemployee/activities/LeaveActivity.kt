package com.ibs.hrmioneemployee.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.ibs.hrmioneemployee.databinding.ActivityLeaveBinding

class LeaveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.cvCreateLeaveRequest.setOnClickListener {
            startActivity(Intent(this, CreateLeaveRequestActivity::class.java))
        }

        binding.cvMyLeaveBalance.setOnClickListener {
            val intent = Intent(this, MyLeaveBalanceActivity::class.java)
            intent.putExtra("ClickStatus", "LeaveBalance")
            startActivity(intent)
        }

        binding.cvMyLeaveHistory.setOnClickListener {
            val intent = Intent(this, MyLeaveBalanceActivity::class.java)
            intent.putExtra("ClickStatus", "LeaveHistory")
            startActivity(intent)        }

        binding.llBack.setOnClickListener {
            onBackPressed()
        }
    }
}