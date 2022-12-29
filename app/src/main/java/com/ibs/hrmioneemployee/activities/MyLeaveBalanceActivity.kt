package com.ibs.hrmioneemployee.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.adapters.LeaveBalanceAdapter
import com.ibs.hrmioneemployee.databinding.ActivityMyLeaveBalanceBinding
import com.ibs.hrmioneemployee.fragments.AppliedFragment
import com.ibs.hrmioneemployee.fragments.LeavesHistoryFragment
import com.ibs.hrmioneemployee.models.api_models.my_leave_balance.LeaveBalance
import com.ibs.hrmioneemployee.models.api_models.my_leave_balance.MyLeaveBalanceResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyLeaveBalanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyLeaveBalanceBinding
    private lateinit var sp: SharedPreferences
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private var employeeId = 0
    private lateinit var authorization: String
    private lateinit var leaveBalanceList: ArrayList<LeaveBalance>
    private lateinit var leaveBalanceAdapter: LeaveBalanceAdapter
    private lateinit var dataLoading: DataLoading
    private lateinit var s: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyLeaveBalanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.backImage.setOnClickListener {
            onBackPressed()
        }

        dataLoading = DataLoading(this)

        sharedPreferenceClass = SharedPreferenceClass(this)
        authorization = sharedPreferenceClass.getLoginToken()
        sp = getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, MODE_PRIVATE)
        employeeId = sp.getInt("UserId", -1)

        callMyLeaveBalanceApi()

        s = intent.getStringExtra("ClickStatus").toString()
        if (s == "LeaveBalance"){
            appliedClicked()
            addFragment(AppliedFragment())
        }
        else if (s == "LeaveHistory"){
            leavesHistoryClicked()
            addFragment(LeavesHistoryFragment())
        }

        binding.clickToApplyText.setOnClickListener {
            startActivityForResult(Intent(this, CreateLeaveRequestActivity::class.java), 100)
        }

        binding.applied.setOnClickListener {
            appliedClicked()
        }

        binding.leavesHistory.setOnClickListener {
            leavesHistoryClicked()
        }
    }

    private fun appliedClicked() {

        binding.applied.setBackgroundResource(R.drawable.screen18_view_all_bg)
        binding.leavesHistory.setBackgroundResource(R.drawable.applied_leaves_history_button_bg)
        binding.approvalsText.setTextColor(Color.WHITE)
        binding.leavesHistoryText.setTextColor(Color.BLACK)

        replaceFragment(AppliedFragment())
    }

    private fun leavesHistoryClicked() {

        binding.applied.setBackgroundResource(R.drawable.applied_leaves_history_button_bg)
        binding.leavesHistory.setBackgroundResource(R.drawable.screen18_view_all_bg)
        binding.leavesHistoryText.setTextColor(Color.WHITE)
        binding.approvalsText.setTextColor(Color.BLACK)

        replaceFragment(LeavesHistoryFragment())
    }

    private fun callMyLeaveBalanceApi() {

        dataLoading.startLoading()

        val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<MyLeaveBalanceResponse> = apiServices.myLeaveBalance(authorization, employeeId)

        call.enqueue(object : Callback<MyLeaveBalanceResponse>{
            override fun onResponse(call: Call<MyLeaveBalanceResponse>, response: Response<MyLeaveBalanceResponse>) {

                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.code == 200){
                        setAllData(response.body()!!)
                    }
                }
                else{
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@MyLeaveBalanceActivity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<MyLeaveBalanceResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@MyLeaveBalanceActivity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAllData(body: MyLeaveBalanceResponse) {

        binding.leaveBalance.text = body.result.remainingLeaves.toString()
        binding.totalLeaves.text = body.result.totalLeaves.toString()
        binding.leavesUsed.text = body.result.usedLeaves.toString()
        if (body.result.totalLeaves != 0){
            binding.leaveBalanceMainProgressBar.progress = (body.result.usedLeaves*100)/body.result.totalLeaves
        }
        leaveBalanceList = ArrayList()
        leaveBalanceList = body.result.leaveBalances

        binding.leaveBalanceRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyLeaveBalanceActivity, LinearLayoutManager.HORIZONTAL, false)
            leaveBalanceAdapter = LeaveBalanceAdapter(this@MyLeaveBalanceActivity, leaveBalanceList)
            adapter = leaveBalanceAdapter
        }
    }

//    override fun onResume() {
//        super.onResume()
//        callMyLeaveBalanceApi()
//    }

    //      OR

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && data != null){
            if (data.getIntExtra("check", 0) == 1){
                callMyLeaveBalanceApi()
            }
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(R.id.myLeaveBalanceFragContainer, fragment).commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.myLeaveBalanceFragContainer, fragment).commit()
    }
}