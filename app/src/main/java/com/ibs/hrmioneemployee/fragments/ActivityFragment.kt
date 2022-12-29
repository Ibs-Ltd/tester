package com.ibs.hrmioneemployee.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ibs.hrmioneemployee.activities.*
import com.ibs.hrmioneemployee.databinding.FragmentActivityBinding

class ActivityFragment : Fragment() {

    private lateinit var binding: FragmentActivityBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentActivityBinding.inflate(inflater, container, false)

        binding.cvAttendance.setOnClickListener {
            startActivity(Intent(activity, AttendanceActivity::class.java))
        }

        binding.cvLeave.setOnClickListener {
            startActivity(Intent(activity, LeaveActivity::class.java))
        }

        binding.cvPayslip.setOnClickListener {
            startActivity(Intent(activity, PaySlipActivity::class.java))
        }

        binding.cvDirectory.setOnClickListener {
            startActivity(Intent(activity, DirectoryActivity::class.java))
        }

        binding.cvHolidays.setOnClickListener {
            startActivity(Intent(activity, HolidaysActivity::class.java))
        }

        return binding.root
    }
}