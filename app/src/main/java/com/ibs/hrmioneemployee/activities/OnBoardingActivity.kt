package com.ibs.hrmioneemployee.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.adapters.OnboardingItemsAdapter
import com.ibs.hrmioneemployee.databinding.ActivityOnBoardingBinding
import com.ibs.hrmioneemployee.models.other_models.OnboardingItemModel
import com.ibs.hrmioneemployee.utilities.SharedPreferenceSettings

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout
    private lateinit var previousArrow: ImageView
    private lateinit var sharedPreferenceSettings: SharedPreferenceSettings
    private lateinit var onboardingViewPager: ViewPager2
    private var flag: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        sharedPreferenceSettings = SharedPreferenceSettings(this)

        previousArrow = findViewById(R.id.previousArrow)

        setOnboardingItems()
        setUpIndicators()
        setCurrentIndicator(0)
    }

    private fun setOnboardingItems() {
        onboardingItemsAdapter = OnboardingItemsAdapter(this,
            listOf(
                OnboardingItemModel(
                    "Track","Attendance", R.drawable.tutorial_1_icon, getString(R.string.onboardingDescription1)
                ),
                OnboardingItemModel(
                    "Apply","Leave", R.drawable.tutorial_2_icon, getString(R.string.onboardingDescription2)
                ),
                OnboardingItemModel(
                    "Mail","Communication", R.drawable.tutorial_3_icon, getString(R.string.onboardingDescription3)
                ),
                OnboardingItemModel(
                    "Upload","Document", R.drawable.tutorial_4_icon, getString(R.string.onboardingDescription4)
                ),
                OnboardingItemModel(
                    "Track","Payslip", R.drawable.tutorial_5_icon, getString(R.string.onboardingDescription5)
                )
            )
        )

        onboardingViewPager = findViewById(R.id.onboardingViewPager)
        onboardingViewPager.adapter = onboardingItemsAdapter
        onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        binding.nextArrow.setOnClickListener {
            if (onboardingViewPager.currentItem + 1 < onboardingItemsAdapter.itemCount) {
                onboardingViewPager.currentItem += 1
                previousArrow.visibility = View.VISIBLE

                if(onboardingViewPager.currentItem + 1  == onboardingItemsAdapter.itemCount){
                    binding.skip.visibility = View.INVISIBLE
                }
            }
            else {
                if (flag){
                    flag = false
                    navigateToHomeActivity()
                }
            }
        }

        binding.previousArrow.setOnClickListener {
            if (onboardingViewPager.currentItem > 0) {
                onboardingViewPager.currentItem -= 1
                binding.skip.visibility = View.VISIBLE
            }
            if (onboardingViewPager.currentItem == 0){
                previousArrow.visibility = View.INVISIBLE
            }
        }

        if (onboardingViewPager.currentItem == 0){
            previousArrow.visibility = View.INVISIBLE
        }

        if (onboardingViewPager.currentItem > 0) {
            previousArrow.visibility = View.VISIBLE
        }

        findViewById<TextView>(R.id.skip).setOnClickListener {
            navigateToHomeActivity()
        }
    }

    private fun navigateToHomeActivity() {
        startActivity(Intent(this, SelectRoleActivity::class.java))
        finish()
    }

    private fun setUpIndicators() {
        indicatorsContainer = findViewById(R.id.indicatorsContainer)
        val indicators = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.indicator_inactive_background
                    )
                )
                it.layoutParams = layoutParams
                indicatorsContainer.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {

            if (onboardingViewPager.currentItem == 0){
                previousArrow.visibility = View.INVISIBLE
            }

            if (onboardingViewPager.currentItem > 0) {
                previousArrow.visibility = View.VISIBLE
            }

            if (onboardingViewPager.currentItem + 1 == onboardingItemsAdapter.itemCount){
                binding.skip.visibility = View.INVISIBLE
            }
            else{
                binding.skip.visibility = View.VISIBLE
            }

            val imageView = indicatorsContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_active_background)
                )
            } else
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_inactive_background))
        }
    }

    override fun onStart() {
        super.onStart()
        if (sharedPreferenceSettings.getIntroScreen()){
            startActivity(Intent(this, SelectRoleActivity::class.java))
            finish()
        }
    }
}