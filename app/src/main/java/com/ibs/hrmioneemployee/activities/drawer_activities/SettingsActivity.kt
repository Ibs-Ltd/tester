package com.ibs.hrmioneemployee.activities.drawer_activities

import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private var buttonOn: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        binding.onOffButton.setOnClickListener {
            if (!buttonOn) {
                buttonOn = true
                binding.onOffButton.setImageResource(R.drawable.off)
            } else {
                buttonOn = false
                binding.onOffButton.setImageResource(R.drawable.on)
            }
        }

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this, R.array.Font_Size, R.layout.setting_spinner_layout)

        adapter.setDropDownViewResource(R.layout.setting_spinner_layout)
        binding.fontSpinner.adapter = adapter

//        binding.fontSpinner.onItemSelectedListener = this
    }

//    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onNothingSelected(p0: AdapterView<*>?) {
//        TODO("Not yet implemented")
//    }
}