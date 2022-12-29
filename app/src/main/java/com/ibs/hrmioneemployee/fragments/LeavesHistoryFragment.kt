package com.ibs.hrmioneemployee.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.hrmioneemployee.adapters.LeaveHistoryAdapter
import com.ibs.hrmioneemployee.databinding.FragmentLeavesHistoryBinding
import com.ibs.hrmioneemployee.models.api_models.my_leave_balance.LeaveHistory
import com.ibs.hrmioneemployee.models.api_models.my_leave_balance.MyLeaveBalanceResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeavesHistoryFragment : Fragment() {

    private lateinit var binding: FragmentLeavesHistoryBinding
    private lateinit var dataLoading: DataLoading
    private lateinit var sp: SharedPreferences
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private var employeeId = 0
    private lateinit var Authorization: String
    private lateinit var leaveHistoryAdapter: LeaveHistoryAdapter
    private lateinit var leaveHistoryList: ArrayList<LeaveHistory>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLeavesHistoryBinding.inflate(inflater, container, false)

        dataLoading = DataLoading(activity)

        sharedPreferenceClass = SharedPreferenceClass(requireActivity())
        Authorization = sharedPreferenceClass.getLoginToken()
        sp = requireActivity().getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, AppCompatActivity.MODE_PRIVATE)
        employeeId = sp.getInt("UserId", -1)

        callMyLeaveBalanceApi()

        return binding.root
    }

    private fun callMyLeaveBalanceApi() {

        dataLoading.startLoading()

        val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<MyLeaveBalanceResponse> = apiServices.myLeaveBalance(Authorization, employeeId)

        call.enqueue(object : Callback<MyLeaveBalanceResponse> {
            override fun onResponse(call: Call<MyLeaveBalanceResponse>, response: Response<MyLeaveBalanceResponse>) {

                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.code == 200){

                        leaveHistoryList = ArrayList()
                        leaveHistoryList = response.body()!!.result.leaveHistory

                        if (leaveHistoryList.size == 0){
                            binding.leavesHistoryNoData.visibility = View.VISIBLE
                        }
                        else{
                            binding.leavesHistoryRecyclerView.recyclerView.apply {
                                layoutManager = LinearLayoutManager(activity)
                                addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
                                leaveHistoryAdapter = LeaveHistoryAdapter(requireActivity(), leaveHistoryList)
                                adapter = leaveHistoryAdapter
                            }
                        }
                    }
                }
                else{
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(activity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<MyLeaveBalanceResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(activity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })

    }

}