package com.ibs.hrmioneemployee.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.hrmioneemployee.adapters.HolidayListAdapter
import com.ibs.hrmioneemployee.databinding.ActivityHolidaysBinding
import com.ibs.hrmioneemployee.models.api_models.company_holidays_list.CompanyHolidaysListResponse
import com.ibs.hrmioneemployee.models.other_models.HolidayListModel
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import com.whiteelephant.monthpicker.MonthPickerDialog
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HolidaysActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHolidaysBinding
    private var collapseExpand: Boolean = true
    private lateinit var holidayListAdapter: HolidayListAdapter
    private lateinit var holidayList: ArrayList<HolidayListModel>
    private lateinit var janHolidayList: ArrayList<HolidayListModel>
    private lateinit var febHolidayList: ArrayList<HolidayListModel>
    private lateinit var marchHolidayList: ArrayList<HolidayListModel>
    private lateinit var aprilHolidayList: ArrayList<HolidayListModel>
    private lateinit var mayHolidayList: ArrayList<HolidayListModel>
    private lateinit var juneHolidayList: ArrayList<HolidayListModel>
    private lateinit var julyHolidayList: ArrayList<HolidayListModel>
    private lateinit var augustHolidayList: ArrayList<HolidayListModel>
    private lateinit var septemberHolidayList: ArrayList<HolidayListModel>
    private lateinit var octoberHolidayList: ArrayList<HolidayListModel>
    private lateinit var novemberHolidayList: ArrayList<HolidayListModel>
    private lateinit var decemberHolidayList: ArrayList<HolidayListModel>
    private val formatDate = SimpleDateFormat("yyyy", Locale.US)
    private lateinit var sp: SharedPreferences
    private var userId = 0
    private var year = 0
    private lateinit var dataLoading: DataLoading
    private lateinit var Authorization: String
    private lateinit var sharedPreferenceClass: SharedPreferenceClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHolidaysBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        dataLoading = DataLoading(this)
        sharedPreferenceClass = SharedPreferenceClass(this)
        Authorization = sharedPreferenceClass.getLoginToken()

        val calendar: Calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)

        sp = getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, MODE_PRIVATE)
        userId = sp.getInt("UserId", -1)

        callCompanyHolidaysListApi()

        binding.filter.setOnClickListener {
            openYearPicker()
        }

        binding.holidayCircle.setOnClickListener {
            openYearPicker()
        }

        holidayList = ArrayList()
        janHolidayList = ArrayList()
        febHolidayList = ArrayList()
        marchHolidayList = ArrayList()
        aprilHolidayList = ArrayList()
        mayHolidayList = ArrayList()
        juneHolidayList = ArrayList()
        julyHolidayList = ArrayList()
        augustHolidayList = ArrayList()
        septemberHolidayList = ArrayList()
        octoberHolidayList = ArrayList()
        novemberHolidayList = ArrayList()
        decemberHolidayList = ArrayList()

        //  january
        binding.llJanuary.setOnClickListener {

            holidayList = ArrayList()
            holidayList = janHolidayList

            binding.januaryRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false
                if (holidayList.size == 0) {
                    expand(binding.janNoHoliday.noHolidayText)
                } else {
                    expand(binding.januaryRecyclerView.recyclerView)
                }
                binding.januaryArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true
                if (holidayList.size == 0) {
                    collapse(binding.janNoHoliday.noHolidayText)
                } else {
                    collapse(binding.januaryRecyclerView.recyclerView)
                }
                binding.januaryArrow.animate().rotation(0f).start()
            }
        }

        //  february
        binding.llFebruary.setOnClickListener {

            holidayList = ArrayList()
            holidayList = febHolidayList

            binding.februaryRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false
                if (holidayList.size == 0) {
                    expand(binding.febNoHoliday.noHolidayText)
                } else {
                    expand(binding.februaryRecyclerView.recyclerView)
                }
                binding.februaryArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true
                if (holidayList.size == 0)
                    collapse(binding.febNoHoliday.noHolidayText)
                else
                    collapse(binding.februaryRecyclerView.recyclerView)
                binding.februaryArrow.animate().rotation(0f).start()
            }
        }

        //  march
        binding.llMarch.setOnClickListener {

            holidayList = ArrayList()
            holidayList = marchHolidayList

            binding.marchRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false
                if (holidayList.size == 0)
                    expand(binding.marchNoHoliday.noHolidayText)
                else
                    expand(binding.marchRecyclerView.recyclerView)
                binding.marchArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true
                if (holidayList.size == 0)
                    collapse(binding.marchNoHoliday.noHolidayText)
                else
                    collapse(binding.marchRecyclerView.recyclerView)
                binding.marchArrow.animate().rotation(0f).start()
            }
        }

        //  april
        binding.llApril.setOnClickListener {

            holidayList = ArrayList()
            holidayList = aprilHolidayList

            binding.aprilRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false
                if (holidayList.size == 0)
                    expand(binding.aprilNoHoliday.noHolidayText)
                else
                    expand(binding.aprilRecyclerView.recyclerView)
                binding.aprilArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true
                if (holidayList.size == 0)
                    collapse(binding.aprilNoHoliday.noHolidayText)
                else
                    collapse(binding.aprilRecyclerView.recyclerView)
                binding.aprilArrow.animate().rotation(0f).start()
            }
        }

        //  may
        binding.llMay.setOnClickListener {

            holidayList = ArrayList()
            holidayList = mayHolidayList

            binding.mayRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false
                if (holidayList.size == 0)
                    expand(binding.mayNoHoliday.noHolidayText)
                else
                    expand(binding.mayRecyclerView.recyclerView)
                binding.mayArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true

                if (holidayList.size == 0)
                    collapse(binding.mayNoHoliday.noHolidayText)
                else
                    collapse(binding.mayRecyclerView.recyclerView)
                binding.mayArrow.animate().rotation(0f).start()
            }
        }

        //  june
        binding.llJune.setOnClickListener {

            holidayList = ArrayList()
            holidayList = juneHolidayList

            binding.juneRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false

                if (holidayList.size == 0)
                    expand(binding.juneNoHoliday.noHolidayText)
                else
                    expand(binding.juneRecyclerView.recyclerView)
                binding.juneArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true

                if (holidayList.size == 0)
                    collapse(binding.juneNoHoliday.noHolidayText)
                else
                    collapse(binding.juneRecyclerView.recyclerView)
                binding.juneArrow.animate().rotation(0f).start()
            }
        }

        //  july
        binding.llJuly.setOnClickListener {

            holidayList = ArrayList()
            holidayList = julyHolidayList

            binding.julyRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false

                if (holidayList.size == 0)
                    expand(binding.julyNoHoliday.noHolidayText)
                else
                    expand(binding.julyRecyclerView.recyclerView)
                binding.julyArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true

                if (holidayList.size == 0)
                    collapse(binding.julyNoHoliday.noHolidayText)
                else
                    collapse(binding.julyRecyclerView.recyclerView)
                binding.julyArrow.animate().rotation(0f).start()
            }
        }

        //  august
        binding.llAugust.setOnClickListener {

            holidayList = ArrayList()
            holidayList = augustHolidayList

            binding.augustRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false

                if (holidayList.size == 0)
                    expand(binding.augustNoHoliday.noHolidayText)
                else
                    expand(binding.augustRecyclerView.recyclerView)
                binding.augustArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true

                if (holidayList.size == 0)
                    collapse(binding.augustNoHoliday.noHolidayText)
                else
                    collapse(binding.augustRecyclerView.recyclerView)
                binding.augustArrow.animate().rotation(0f).start()
            }
        }

        //  september
        binding.llSeptember.setOnClickListener {

            holidayList = ArrayList()
            holidayList = septemberHolidayList

            binding.septemberRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false

                if (holidayList.size == 0)
                    expand(binding.sepNoHoliday.noHolidayText)
                else
                    expand(binding.septemberRecyclerView.recyclerView)
                binding.septemberArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true

                if (holidayList.size == 0)
                    collapse(binding.sepNoHoliday.noHolidayText)
                else
                    collapse(binding.septemberRecyclerView.recyclerView)
                binding.septemberArrow.animate().rotation(0f).start()
            }
        }

        //  october
        binding.llOctober.setOnClickListener {

            holidayList = ArrayList()
            holidayList = octoberHolidayList

            binding.octoberRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false

                if (holidayList.size == 0)
                    expand(binding.octNoHoliday.noHolidayText)
                else
                    expand(binding.octoberRecyclerView.recyclerView)
                binding.octoberArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true

                if (holidayList.size == 0)
                    collapse(binding.octNoHoliday.noHolidayText)
                else
                    collapse(binding.octoberRecyclerView.recyclerView)
                binding.octoberArrow.animate().rotation(0f).start()
            }
        }

        //  november
        binding.llNovember.setOnClickListener {

            holidayList = ArrayList()
            holidayList = novemberHolidayList

            binding.novemberRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false

                if (holidayList.size == 0)
                    expand(binding.novNoHoliday.noHolidayText)
                else
                    expand(binding.novemberRecyclerView.recyclerView)
                binding.novemberArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true

                if (holidayList.size == 0)
                    collapse(binding.novNoHoliday.noHolidayText)
                else
                    collapse(binding.novemberRecyclerView.recyclerView)
                binding.novemberArrow.animate().rotation(0f).start()
            }
        }

        //  december
        binding.llDecember.setOnClickListener {

            holidayList = ArrayList()
            holidayList = decemberHolidayList

            binding.decemberRecyclerView.recyclerView.apply {

                setHolidaysListToAdapter(holidayList)
                layoutManager = LinearLayoutManager(this@HolidaysActivity)
                adapter = holidayListAdapter
            }

            if (collapseExpand) {
                collapseExpand = false

                if (holidayList.size == 0)
                    expand(binding.decNoHoliday.noHolidayText)
                else
                    expand(binding.decemberRecyclerView.recyclerView)
                binding.decemberArrow.animate().rotation(180f).start()
            } else {
                collapseExpand = true

                if (holidayList.size == 0)
                    collapse(binding.decNoHoliday.noHolidayText)
                else
                    collapse(binding.decemberRecyclerView.recyclerView)
                binding.decemberArrow.animate().rotation(0f).start()
            }
        }
    }

    private fun setHolidaysListToAdapter(holidayList: ArrayList<HolidayListModel>) {
        holidayListAdapter = HolidayListAdapter(this, holidayList)
    }

    private fun openYearPicker() {

        val calendar = Calendar.getInstance()
        val builder = MonthPickerDialog.Builder(this, { selectedMonth, selectedYear ->
            val selectdate: Calendar = Calendar.getInstance()
            selectdate.set(Calendar.YEAR, selectedYear)
            selectdate.set(Calendar.MONTH, selectedMonth)
            val date: Int = formatDate.format(selectdate.time).toInt()
            binding.holidayYear.text = date.toString()
            year = date
            callCompanyHolidaysListApi()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))

        builder.setActivatedYear(calendar.get(Calendar.YEAR)).setMinYear(2010)
            .setMaxYear(calendar.get(Calendar.YEAR)).showYearOnly().build().show()
    }

    fun expand(v: View) {
        if (v.visibility == View.VISIBLE) return
        val durations: Long
        val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            (v.parent as View).width,
            View.MeasureSpec.EXACTLY
        )
        val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            0,
            View.MeasureSpec.UNSPECIFIED
        )
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        durations = ((targetHeight / v.context.resources
            .displayMetrics.density)).toLong()

        v.alpha = 0.0F
        v.visibility = View.VISIBLE
        v.animate().alpha(1.0F).setDuration(durations).setListener(null)

        val a: Animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration = durations
        v.startAnimation(a)
    }

    private fun collapse(v: View) {
        if (v.visibility == View.GONE) return
        val durations: Long
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        durations = (initialHeight / v.context.resources
            .displayMetrics.density).toLong()

        v.alpha = 1.0F
        v.animate().alpha(0.0F).setDuration(durations)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    v.visibility = View.GONE
                    v.alpha = 1.0F
                }
            })

        // Collapse speed of 1dp/ms
        a.duration = durations
        v.startAnimation(a)
    }

    private fun callCompanyHolidaysListApi() {
        dataLoading.startLoading()
        collapseAllViews()
        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<CompanyHolidaysListResponse> = apiServices.companyHolidaysList(Authorization, userId, year)

        call.enqueue(object : Callback<CompanyHolidaysListResponse> {
            override fun onResponse(
                call: Call<CompanyHolidaysListResponse>,
                response: Response<CompanyHolidaysListResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        setMonthlyHolidays(response.body()!!)
                        Toast.makeText(
                            this@HolidaysActivity,
                            "" + response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(
                        this@HolidaysActivity,
                        jObjError.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<CompanyHolidaysListResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@HolidaysActivity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setMonthlyHolidays(body: CompanyHolidaysListResponse) {

        janHolidayList = ArrayList()
        febHolidayList = ArrayList()
        marchHolidayList = ArrayList()
        aprilHolidayList = ArrayList()
        mayHolidayList = ArrayList()
        juneHolidayList = ArrayList()
        julyHolidayList = ArrayList()
        augustHolidayList = ArrayList()
        septemberHolidayList = ArrayList()
        octoberHolidayList = ArrayList()
        novemberHolidayList = ArrayList()
        decemberHolidayList = ArrayList()

        for (i in 0..((body.result.size) - 1)) {
            when (body.result[i].month) {
                "January" -> {
                    janHolidayList.add(HolidayListModel(body.result[i].name, body.result[i].date))
                }
                "February" -> {
                    febHolidayList.add(HolidayListModel(body.result[i].name, body.result[i].date))
                }
                "March" -> {
                    marchHolidayList.add(HolidayListModel(body.result[i].name, body.result[i].date))
                }
                "April" -> {
                    aprilHolidayList.add(HolidayListModel(body.result[i].name, body.result[i].date))
                }
                "May" -> {
                    mayHolidayList.add(HolidayListModel(body.result[i].name, body.result[i].date))
                }
                "June" -> {
                    juneHolidayList.add(HolidayListModel(body.result[i].name, body.result[i].date))
                }
                "July" -> {
                    julyHolidayList.add(HolidayListModel(body.result[i].name, body.result[i].date))
                }
                "August" -> {
                    augustHolidayList.add(
                        HolidayListModel(
                            body.result[i].name,
                            body.result[i].date
                        )
                    )
                }
                "September" -> {
                    septemberHolidayList.add(
                        HolidayListModel(
                            body.result[i].name,
                            body.result[i].date
                        )
                    )
                }
                "October" -> {
                    octoberHolidayList.add(
                        HolidayListModel(
                            body.result[i].name,
                            body.result[i].date
                        )
                    )
                }
                "November" -> {
                    novemberHolidayList.add(
                        HolidayListModel(
                            body.result[i].name,
                            body.result[i].date
                        )
                    )
                }
                "December" -> {
                    decemberHolidayList.add(
                        HolidayListModel(
                            body.result[i].name,
                            body.result[i].date
                        )
                    )
                }
            }
        }
    }

    private fun collapseAllViews() {
        collapse(binding.janNoHoliday.noHolidayText)
        collapse(binding.januaryRecyclerView.recyclerView)
        collapse(binding.febNoHoliday.noHolidayText)
        collapse(binding.februaryRecyclerView.recyclerView)
        collapse(binding.marchNoHoliday.noHolidayText)
        collapse(binding.marchRecyclerView.recyclerView)
        collapse(binding.aprilNoHoliday.noHolidayText)
        collapse(binding.aprilRecyclerView.recyclerView)
        collapse(binding.mayNoHoliday.noHolidayText)
        collapse(binding.mayRecyclerView.recyclerView)
        collapse(binding.juneNoHoliday.noHolidayText)
        collapse(binding.juneRecyclerView.recyclerView)
        collapse(binding.julyNoHoliday.noHolidayText)
        collapse(binding.julyRecyclerView.recyclerView)
        collapse(binding.augustNoHoliday.noHolidayText)
        collapse(binding.augustRecyclerView.recyclerView)
        collapse(binding.sepNoHoliday.noHolidayText)
        collapse(binding.septemberRecyclerView.recyclerView)
        collapse(binding.octNoHoliday.noHolidayText)
        collapse(binding.octoberRecyclerView.recyclerView)
        collapse(binding.novNoHoliday.noHolidayText)
        collapse(binding.novemberRecyclerView.recyclerView)
        collapse(binding.decNoHoliday.noHolidayText)
        collapse(binding.decemberRecyclerView.recyclerView)
        binding.januaryArrow.animate().rotation(0f).start()
        binding.februaryArrow.animate().rotation(0f).start()
        binding.marchArrow.animate().rotation(0f).start()
        binding.aprilArrow.animate().rotation(0f).start()
        binding.mayArrow.animate().rotation(0f).start()
        binding.juneArrow.animate().rotation(0f).start()
        binding.julyArrow.animate().rotation(0f).start()
        binding.augustArrow.animate().rotation(0f).start()
        binding.septemberArrow.animate().rotation(0f).start()
        binding.octoberArrow.animate().rotation(0f).start()
        binding.novemberArrow.animate().rotation(0f).start()
        binding.decemberArrow.animate().rotation(0f).start()
    }
}