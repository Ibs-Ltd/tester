package com.ibs.hrmioneemployee.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.hrmioneemployee.adapters.EmployeeListAdapter
import com.ibs.hrmioneemployee.databinding.ActivityDirectoryBinding
import com.ibs.hrmioneemployee.models.api_models.employees_list.EmployeesListResponse
import com.ibs.hrmioneemployee.models.api_models.employees_list.TotalEmployeeResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.MyApplication
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import com.ibs.hrmioneemployee.view_models.MainViewModel
import com.ibs.hrmioneemployee.view_models.MainViewModelFactory
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class DirectoryActivity : AppCompatActivity(), EmployeeListAdapter.OnItemClickListener {

    private lateinit var binding: ActivityDirectoryBinding
    private lateinit var employeeListAdapter: EmployeeListAdapter
    private lateinit var employeeList: ArrayList<TotalEmployeeResponse>
    private lateinit var sp: SharedPreferences
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private var userId: Int = 0
    private lateinit var Authorization: String
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDirectoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        val repository = (application as MyApplication).repository
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]

        employeeList = ArrayList()
        sharedPreferenceClass = SharedPreferenceClass(this)
        Authorization = sharedPreferenceClass.getLoginToken()
        sp = getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, MODE_PRIVATE)
        userId = sp.getInt("UserId", -1)

        callEmployeeListApi()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(text: String?) {

        val filteredList: ArrayList<TotalEmployeeResponse> = ArrayList()
        val emptyList: ArrayList<TotalEmployeeResponse> = ArrayList()
        for (result in employeeList) {
            val name = "${result.firstName} ${result.lastName}"
            if (name.lowercase().contains(text!!.lowercase())) {
                filteredList.add(result)
            }
        }
        if (filteredList.isEmpty()) {
            employeeListAdapter.setFilteredList(emptyList)
        } else {
            employeeListAdapter.setFilteredList(filteredList)
        }
    }

    override fun onItemClick(totalEmployeeResponse: TotalEmployeeResponse) {
        val intent = Intent(this, EmployeeFullDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("EmployeeFullDetails", totalEmployeeResponse)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun callEmployeeListApi() {
        mainViewModel.callEmployeesListApi(Authorization, userId, this)

        mainViewModel.getEmployeesList().observe(this, Observer {
            if (it.code == 200){
                binding.noOfEmployees.text = "${it.result.numberOfEmployees} Employees"
                employeeList = it.result.totalEmployeeResponse
                employeeListAdapter = EmployeeListAdapter(this@DirectoryActivity, employeeList, this@DirectoryActivity)
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(this@DirectoryActivity)
                    adapter = employeeListAdapter
                }
            }
        })

//        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
//        val call: Call<EmployeesListResponse> = apiServices.getEmployeesList(Authorization, userId)
//
//        call.enqueue(object : Callback<EmployeesListResponse> {
//            @SuppressLint("SetTextI18n")
//            override fun onResponse(
//                call: Call<EmployeesListResponse>,
//                response: Response<EmployeesListResponse>
//            ) {
//
//                if (response.isSuccessful && response.body() != null) {
//                    if (response.code() == 200) {
////                        binding.noOfEmployees.text = "${response.body()!!.result.numberOfEmployees} Employees"
////                        employeeList = response.body()!!.result.totalEmployeeResponse
////                        employeeListAdapter = EmployeeListAdapter(this@DirectoryActivity, employeeList, this@DirectoryActivity)
////                        binding.recyclerView.apply {
////                            layoutManager = LinearLayoutManager(this@DirectoryActivity)
////                            adapter = employeeListAdapter
////                        }
//                    }
//                } else {
//                    val jObjError = JSONObject(response.errorBody()!!.string())
//                    Toast.makeText(
//                        this@DirectoryActivity,
//                        jObjError.getString("message"),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                dataLoading.hideLoading()
//            }
//
//            override fun onFailure(call: Call<EmployeesListResponse>, t: Throwable) {
//                dataLoading.hideLoading()
//                Toast.makeText(this@DirectoryActivity, "Something went wrong", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        })
    }
}