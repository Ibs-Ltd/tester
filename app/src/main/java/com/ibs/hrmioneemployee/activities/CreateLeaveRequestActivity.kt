package com.ibs.hrmioneemployee.activities

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.hrmioneemployee.adapters.LeaveTypeDialogAdapter
import com.ibs.hrmioneemployee.models.api_models.create_leave.CreateLeaveModel
import com.ibs.hrmioneemployee.models.api_models.create_leave.CreateLeaveResponse
import com.ibs.hrmioneemployee.models.api_models.leave_type_list.LeaveTypeListResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import com.google.android.material.datepicker.MaterialDatePicker
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.ActivityCreateLeaveRequestBinding
import com.ibs.hrmioneemployee.databinding.ChooseLeaveTypeDialogBinding
import com.ibs.hrmioneemployee.models.api_models.leave_type_list.Result
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CreateLeaveRequestActivity : AppCompatActivity(), LeaveTypeDialogAdapter.OnItemClickListener {

    private lateinit var binding: ActivityCreateLeaveRequestBinding
    private lateinit var dialogBinding: ChooseLeaveTypeDialogBinding
    private lateinit var sp: SharedPreferences
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private var employeeId = 0
    private lateinit var Authorization: String
    private lateinit var leaveTypeList: ArrayList<Result>
    private lateinit var leaveTypeAdapter: LeaveTypeDialogAdapter
    private lateinit var leaveTypeDialog: Dialog
    private var leaveTypeId: Int = 0
    private var startDate: Long = 0
    private var endDate: Long = 0
    private lateinit var reason: String
    private lateinit var dataLoading: DataLoading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateLeaveRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        View.SYSTEM_UI_FLAG_FULLSCREEN
//        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        View.SYSTEM_UI_FLAG_LAYOUT_STABLE

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        leaveTypeList = ArrayList()
        dataLoading = DataLoading(this)

        sharedPreferenceClass = SharedPreferenceClass(this)
        Authorization = sharedPreferenceClass.getLoginToken()
        sp = getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, MODE_PRIVATE)
        employeeId = sp.getInt("UserId", -1)

        dialogBinding = ChooseLeaveTypeDialogBinding.inflate(layoutInflater)

        leaveTypeDialog = Dialog(this)
        leaveTypeDialog.setContentView(dialogBinding.root)

        leaveTypeDialog.window!!.setLayout(880, 880)
        leaveTypeDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvSubmitForApproval.setOnClickListener {

            val date: String = binding.tvChooseDate.text.toString()
            val leaveType: String = binding.tvLeaveType.text.toString()
            val etReason: EditText = findViewById(R.id.etReason)
            reason = etReason.text.toString().trim()

            if (date.isEmpty() || leaveType.isEmpty() || reason.isEmpty()){
                Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            }
            else{
                callCreateLeaveApi()
            }
        }

        binding.llChooseLeaveType.setOnClickListener {
            callLeaveTypeListApi()
            leaveTypeDialog.show()
        }

        dialogBinding.doneTv.setOnClickListener {
            leaveTypeDialog.dismiss()
        }

        binding.llChooseDate.setOnClickListener {

            val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")

            datePicker.addOnPositiveButtonClickListener {
                startDate = datePicker.selection!!.first
                endDate = datePicker.selection!!.second
                binding.tvChooseDate.text = datePicker.headerText
            }
        }
    }

    private fun callLeaveTypeListApi() {

//        dialogBinding.progressBar.visibility = View.VISIBLE
        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<LeaveTypeListResponse> = apiServices.getLeaveTypeList(Authorization, employeeId)

        call.enqueue(object : Callback<LeaveTypeListResponse>{
            override fun onResponse(call: Call<LeaveTypeListResponse>, response: Response<LeaveTypeListResponse>) {

                if (response.isSuccessful && response.body() != null){

                    if (response.body()!!.code == 200) {
                        leaveTypeList = response.body()!!.result

                        dialogBinding.leaveTypeRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@CreateLeaveRequestActivity)
                            leaveTypeAdapter = LeaveTypeDialogAdapter(this@CreateLeaveRequestActivity, leaveTypeList, this@CreateLeaveRequestActivity)
                            adapter = leaveTypeAdapter
                        }
                    }
                }
                else{
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@CreateLeaveRequestActivity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
//                dialogBinding.progressBar.visibility = View.INVISIBLE
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<LeaveTypeListResponse>, t: Throwable) {
//                dialogBinding.progressBar.visibility = View.INVISIBLE
                dataLoading.hideLoading()
                Toast.makeText(this@CreateLeaveRequestActivity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(result: Result) {
        binding.tvLeaveType.text = result.leaveTypeName
        leaveTypeId = result.id
        leaveTypeDialog.dismiss()
    }

    private fun callCreateLeaveApi() {

        dataLoading.startLoading()

        val createLeaveModel = CreateLeaveModel(employeeId, startDate, endDate, leaveTypeId, reason)

        val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<CreateLeaveResponse> = apiServices.createLeave(Authorization, createLeaveModel)

        call.enqueue(object : Callback<CreateLeaveResponse>{
            override fun onResponse(call: Call<CreateLeaveResponse>, response: Response<CreateLeaveResponse>) {
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.code == 200){
                        Toast.makeText(this@CreateLeaveRequestActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent()
                        intent.putExtra("check", 1)
                        setResult(RESULT_OK, intent)
//                        finish()
                        onBackPressed()
                    }
                }
                else{
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@CreateLeaveRequestActivity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<CreateLeaveResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@CreateLeaveRequestActivity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }
}