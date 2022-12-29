package com.ibs.hrmioneemployee.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.activities.MainActivity
import com.ibs.hrmioneemployee.databinding.FragmentHomeBinding
import com.ibs.hrmioneemployee.interfaces.SetFragmentData
import com.ibs.hrmioneemployee.models.api_models.attendance_check_in_out.PunchInOutResponse
import com.ibs.hrmioneemployee.models.api_models.homepage.HomepageResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.*
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.DESIGNATION
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.FIRST_NAME
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.LAST_NAME
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.PROFILE_PICTURE_PATH
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class HomeFragment : Fragment(), SetFragmentData {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sp: SharedPreferences
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private lateinit var sharedPreferenceSettings: SharedPreferenceSettings
    private var userId: Int = 0
    private lateinit var Authorization: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQ_CODE = 1000
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var locationRequest: LocationRequest
    //    private val desiredIMEINumber = "867455041271436"
    private lateinit var deviceIDFromApi: String
    private lateinit var deviceIDFromPhone: String
    private val LOCK_AUTHENTICATION_CODE = 100
    private val REQUEST_CODE = 101
    private lateinit var vibration: Vibrator
    private lateinit var dataLoading: DataLoading
    private lateinit var checkInOutLocation: String


    // when we want to perform any action on fragment from activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            (activity as MainActivity).setHomeFragmentListener(this)
        } catch (e: ClassCastException) {
            Log.e("HomeFragment", e.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)
        val date = "$day/$month/$year"

        dataLoading = DataLoading(activity)
        deviceIDFromPhone = getDeviceID()

        vibration = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        sharedPreferenceSettings = SharedPreferenceSettings(requireActivity())
        sharedPreferenceClass = SharedPreferenceClass(requireActivity())
        Authorization = sharedPreferenceClass.getLoginToken()

        sp = requireActivity().getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, AppCompatActivity.MODE_PRIVATE)

        userId = sp.getInt("UserId", -1)
        deviceIDFromApi = sp.getString("deviceAddress", "")!!

        if (sharedPreferenceSettings.getTodayDate() != date) {
            sharedPreferenceSettings.saveTodayDate(date)
            sharedPreferenceSettings.clearCheckInCheckOut()
        }

        if (sharedPreferenceSettings.getToggleButtonSettings() == "Home") {
            setHomeClickedData()
        } else if (sharedPreferenceSettings.getToggleButtonSettings() == "Office") {
            setOfficeClickedData()
        }

        binding.punchInOutMainButton.setOnClickListener {

            vibration.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))

            if (InternetConnection.checkConnection(requireActivity())) {

                if (checkAllRequiredPermissions()) {

                    if (isLocationEnabled()) {

                        if (deviceIDFromApi == deviceIDFromPhone) {
                            getLastLocation()
                        } else {
                            Toast.makeText(activity, "This phone is not allowed to check in/check out", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        showLocationPrompt()
                    }
                } else {
                    requestAllRequiredPermission()
                }
            } else {
                Toast.makeText(activity, "You're offline", Toast.LENGTH_SHORT).show()
            }
        }

        binding.dayDateText.text = getCurrentDate()
        binding.timeText.text = getCurrentTime()
        clock()

        return binding.root
    }

    @SuppressLint("InlinedApi")
    private fun requestAllRequiredPermission() {

        Dexter.withContext(requireActivity()).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        ).withListener(object :
            MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
//                    Toast.makeText(activity, "All permissions granted", Toast.LENGTH_SHORT).show()
                } else {
//                    Toast.makeText(activity, "All permissions not granted", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest?>?,
                token: PermissionToken?
            ) {
                token?.continuePermissionRequest()
            }
        }).check()
    }

    private fun checkAllRequiredPermissions(): Boolean {

        if (context?.let {
                ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
            } == PackageManager.PERMISSION_GRANTED ||
            context?.let {
                ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } == PackageManager.PERMISSION_GRANTED &&
            context?.let {
                ActivityCompat.checkSelfPermission(it, Manifest.permission.READ_PHONE_STATE)
            } == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

//    @SuppressLint("HardwareIds")
//    private fun getIMEI(requireActivity: FragmentActivity): String {
//        var imei = ""
//        val telephonyManager =
//            requireActivity.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//        // in the below line, we are checking for permissions
//        if (ActivityCompat.checkSelfPermission(
//                requireActivity,
//                Manifest.permission.READ_PHONE_STATE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // if permissions are not provided we are requesting for permissions.
//            ActivityCompat.requestPermissions(
//                requireActivity,
//                arrayOf(Manifest.permission.READ_PHONE_STATE),
//                REQUEST_CODE
//            )
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    imei = if (telephonyManager.phoneCount == 2) {
//                        telephonyManager.getImei(0)
//                    } else {
//                        telephonyManager.imei
//                    }
//                } else {
//                    imei = if (telephonyManager.phoneCount == 2) {
//                        telephonyManager.getDeviceId(0)
//                    } else {
//                        telephonyManager.deviceId
//                    }
//                }
//            } else {
//                imei = telephonyManager.deviceId
//            }
//        }
//        return imei
//    }

    @SuppressLint("HardwareIds")
    private fun getDeviceID(): String {
        return Settings.Secure.getString(
            requireActivity().contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    @SuppressLint("SetTextI18n")
    override fun setHomeClickedData() {
        binding.checkInText.text = "Home In"
        binding.punchImage.setImageResource(R.drawable.punch_white)
        binding.punchInOutMainButton.setBackgroundResource(R.drawable.home_office_in_background)
        binding.checkInTime.text = sharedPreferenceSettings.getCheckInDataForWFH()
        binding.checkOutTime.text = sharedPreferenceSettings.getCheckOutTimeForWFH()
        binding.workingHours.text = sharedPreferenceSettings.getWorkingHrTimeForWFH()
        binding.inTime.text = "Home In"
        binding.outTime.text = "Home Out"

        if (sharedPreferenceSettings.getCheckInCheckOutKeyForWFH() == 1) {
            binding.checkInText.text = "Home Out"
            binding.punchInOutMainButton.setBackgroundResource(R.drawable.home_office_out_background)
            binding.checkInTime.text = sharedPreferenceSettings.getCheckInDataForWFH()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setOfficeClickedData() {
        binding.checkInText.text = "Check In"
        binding.punchImage.setImageResource(R.drawable.punch)
        binding.punchInOutMainButton.setBackgroundResource(R.drawable.home_office_in_background)
        binding.checkInTime.text = sharedPreferenceSettings.getCheckInDataForWFO()
        binding.checkOutTime.text = sharedPreferenceSettings.getCheckOutTimeForWFO()
        binding.workingHours.text = sharedPreferenceSettings.getWorkingHrTimeForWFO()

        binding.inTime.text = "Check In"
        binding.outTime.text = "Check Out"

        if (sharedPreferenceSettings.getCheckInCheckOutKeyForWFO() == 1) {
            binding.checkInText.text = "Check Out"
            binding.punchInOutMainButton.setBackgroundResource(R.drawable.home_office_out_background)
            binding.checkInTime.text = sharedPreferenceSettings.getCheckInDataForWFO()
        }
    }

    private fun callApiForWFH() {
        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<PunchInOutResponse> = apiServices.checkInCheckOutWFH(
            Authorization,
            userId,
            "WFH",
            latitude,
            longitude,
            checkInOutLocation
        )

        call.enqueue(object : Callback<PunchInOutResponse> {
            @SuppressLint("SetTextI18n", "NewApi")
            override fun onResponse(
                call: Call<PunchInOutResponse>,
                response: Response<PunchInOutResponse>
            ) {

                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        Toast.makeText(activity, "" + response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()
                        if (sharedPreferenceSettings.getCheckInCheckOutKeyForWFH() != 1) {
                            sharedPreferenceSettings.saveCheckInDataForWFH(
                                convertLongToTime(response.body()!!.result.checkIn)
                            )
                            binding.checkInText.text = "Home Out"
                            binding.punchInOutMainButton.setBackgroundResource(R.drawable.home_office_out_background)
                            binding.checkInTime.text =
                                convertLongToTime(response.body()!!.result.checkIn)
                            sharedPreferenceSettings.saveCheckInCheckOutKeyForWFH(1)
                            vibration.vibrate(
                                VibrationEffect.createOneShot(
                                    150,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        } else {
//                            binding.punchInOutMainButton.setBackgroundResource(R.drawable.home_office_out_background)
                            sharedPreferenceSettings.saveCheckInDataForWFH(
                                convertLongToTime(
                                    response.body()!!.result.checkIn
                                )
                            )
                            sharedPreferenceSettings.saveCheckOutDataForWFH(
                                convertLongToTime(response.body()!!.result.checkOut),
                                convertMillisToDuration(response.body()!!.result.workingHours)
                            )
                            binding.checkInTime.text =
                                convertLongToTime(response.body()!!.result.checkIn)
                            binding.checkOutTime.text =
                                convertLongToTime(response.body()!!.result.checkOut)
                            binding.workingHours.text =
                                convertMillisToDuration(response.body()!!.result.workingHours)
                            vibration.vibrate(
                                VibrationEffect.createOneShot(
                                    150,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        }
                    } else {
                        Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(activity, jObjError.getString("message"), Toast.LENGTH_SHORT)
                        .show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<PunchInOutResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(activity, "Failure: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callApiForWFO() {
        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<PunchInOutResponse> = apiServices.checkInCheckOutWFO(
            Authorization,
            userId,
            "WFO",
            latitude,
            longitude,
            checkInOutLocation
        )

        call.enqueue(object : Callback<PunchInOutResponse> {
            @SuppressLint("SetTextI18n", "NewApi")
            override fun onResponse(
                call: Call<PunchInOutResponse>,
                response: Response<PunchInOutResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        Toast.makeText(activity, "" + response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()
                        if (sharedPreferenceSettings.getCheckInCheckOutKeyForWFO() != 1) {
                            sharedPreferenceSettings.saveCheckInDataForWFO(
                                convertLongToTime(
                                    response.body()!!.result.checkIn
                                )
                            )
                            binding.checkInText.text = "Check Out"
                            binding.punchInOutMainButton.setBackgroundResource(R.drawable.home_office_out_background)
                            binding.checkInTime.text =
                                convertLongToTime(response.body()!!.result.checkIn)
                            sharedPreferenceSettings.saveCheckInCheckOutKeyForWFO(1)
                            vibration.vibrate(
                                VibrationEffect.createOneShot(
                                    150,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        } else {
//                            binding.punchInOutMainButton.setBackgroundResource(R.drawable.home_office_out_background)
                            sharedPreferenceSettings.saveCheckInDataForWFO(
                                convertLongToTime(
                                    response.body()!!.result.checkIn
                                )
                            )
                            sharedPreferenceSettings.saveCheckOutDataForWFO(
                                convertLongToTime(
                                    response.body()!!.result.checkOut
                                ), convertMillisToDuration(response.body()!!.result.workingHours)
                            )
                            binding.checkInTime.text =
                                convertLongToTime(response.body()!!.result.checkIn)
                            binding.checkOutTime.text =
                                convertLongToTime(response.body()!!.result.checkOut)
                            binding.workingHours.text =
                                convertMillisToDuration(response.body()!!.result.workingHours)
                            vibration.vibrate(
                                VibrationEffect.createOneShot(
                                    150,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        }
//                        disableButtonForOneMinute()
                    } else {
                        Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(activity, jObjError.getString("message"), Toast.LENGTH_SHORT)
                        .show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<PunchInOutResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(activity, "Failure: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    private fun disableButtonForOneMinute() {
//        binding.punchInOutMainButton.isEnabled = false
//
//        Handler().postDelayed({ // This method will be executed once the timer is over
//            binding.punchInOutMainButton.isEnabled = true
//        }, 60000)
//    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getLastLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (checkPermission()) {

            if (isLocationEnabled()) {

                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        getNewLocation()
                    } else {
                        latitude = location.latitude
                        longitude = location.longitude
//                        checkInOutLocation = getCityName(latitude, longitude)
                        checkInOutLocation = getCompleteAddress(latitude, longitude)!!
                        biometricAuthentication()
//                        Toast.makeText(activity, "Latitude: $latitude\nLongitude: $longitude", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
//                Toast.makeText(activity, "Please Enable Your Location Service", Toast.LENGTH_SHORT).show()
                showLocationPrompt()
            }
        } else {
            requestPermission()
        }
    }

    private fun getNewLocation() {

        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2

        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation: Location = p0.lastLocation!!
        }
    }

    //  check whether user given location permission to app or not
    private fun checkPermission(): Boolean {

        if (context?.let {
                ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
            } == PackageManager.PERMISSION_GRANTED ||
            context?.let {
                ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        this.activity?.let {
            ActivityCompat.requestPermissions(
                it, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), LOCATION_PERMISSION_REQ_CODE
            )
        }
    }

    //  check whether user's location services enable or disable
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQ_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.d("Debug:", "you have a permission")
            }
        }
        if (requestCode == REQUEST_CODE) {
            // in the below line, we are checking if permission is granted.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if permissions are granted we are displaying below toast message.
                Toast.makeText(activity, "Permission granted.", Toast.LENGTH_SHORT).show()
            } else {
                // in the below line, we are displaying toast message if permissions are not granted.
                Toast.makeText(activity, "Permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    fun getMacAddress(): String {
//        try {
//            val all: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
//            for (nif in all) {
//                if (!nif.name.equals("wlan0", ignoreCase = true)) continue
//                val macBytes = nif.hardwareAddress ?: return ""
//                val res1 = java.lang.StringBuilder()
//                for (b in macBytes) {
//                    var hex = Integer.toHexString((b and 0xFF.toByte()).toInt())
//                    if (hex.length == 1) hex = "0$hex"
//                    res1.append("$hex:")
//                }
//                if (res1.length > 0) {
//                    res1.deleteCharAt(res1.length - 1)
//                }
//                return res1.toString()
//            }
//        } catch (ex: java.lang.Exception) {
//        }
//        return ""
//    }

    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("hh:mm aa")
        return format.format(date)
    }

    @OptIn(ExperimentalTime::class)
    @SuppressLint("SimpleDateFormat")
    fun convertMillisToDuration(time: Long): String {

        var hours1: String
        var minutes1: String
        val duration = Duration.milliseconds(time)
        val hours = duration.inWholeHours
        hours1 = hours.toString()
        if (hours < 10) {
            hours1 = "0$hours1"
        }
        var minutes = duration.inWholeMinutes
        minutes %= 60
        minutes1 = minutes.toString()
        if (minutes < 10) {
            minutes1 = "0$minutes1"
        }
        return "${hours1}h:${minutes1}m"
    }

//    private fun showBiometricDialog(){
//        executor = ContextCompat.getMainExecutor(requireActivity())
//        biometricPrompt = BiometricPrompt(this, executor,
//            object : BiometricPrompt.AuthenticationCallback() {
//
//                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//                    super.onAuthenticationError(errorCode, errString)
//                    Toast.makeText(activity, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onAuthenticationSucceeded(
//                    result: BiometricPrompt.AuthenticationResult) {
//                    super.onAuthenticationSucceeded(result)
//                    Toast.makeText(activity, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onAuthenticationFailed() {
//                    super.onAuthenticationFailed()
//                    Toast.makeText(activity, "Authentication failed", Toast.LENGTH_SHORT).show()
//                }
//            })
//
//        promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Verify!")
//            .setAllowedAuthenticators(BIOMETRIC_STRONG and DEVICE_CREDENTIAL )
//            .setNegativeButtonText("Cancel")
//            .build()
//    }

//    fun showBiometric(){
//        biometricPrompt.authenticate(promptInfo)
//    }

//    private fun biometricsAvailable(): Boolean {
//        val biometricManager = BiometricManager.from(requireActivity())
//        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL)) {
//            BiometricManager.BIOMETRIC_SUCCESS -> true
//            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false
//            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false
//            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
//            else -> false
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun biometricAuthentication() {

        val keyguardManager: KeyguardManager =
            requireActivity().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (keyguardManager.isKeyguardSecure) {
            val intent: Intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null)
            startActivityForResult(intent, LOCK_AUTHENTICATION_CODE)
        } else {
            if (sharedPreferenceSettings.getToggleButtonSettings() == "Home") {
                callApiForWFH()
            } else if (sharedPreferenceSettings.getToggleButtonSettings() == "Office") {
                callApiForWFO()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == LOCK_AUTHENTICATION_CODE) {
                if (sharedPreferenceSettings.getToggleButtonSettings() == "Home") {
                    callApiForWFH()
                } else if (sharedPreferenceSettings.getToggleButtonSettings() == "Office") {
                    callApiForWFO()
                }
            }
        }
    }
//
//    @SuppressLint("HardwareIds")
//    @Throws(SecurityException::class, NullPointerException::class)
//    fun getIMEINumber(context: Context): String? {
//        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        var imei: String?
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            assert(tm != null)
//            imei = tm.imei
//            //this change is for Android 10 as per security concern it will not provide the imei number.
//            if (imei == null) {
//                imei =
//                    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//            }
//        } else {
//            assert(tm != null)
//            imei = if (tm.deviceId != null && tm.deviceId != "000000000000000") {
//                tm.deviceId
//            } else {
//                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//            }
//        }
//        return imei
//    }

    override fun onStart() {
        super.onStart()
        if (InternetConnection.checkConnection(requireActivity())) {
            callHomepageApi()
        } else {
            ShowToast.showToast(requireActivity())
        }
    }

    private fun callHomepageApi() {
        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<HomepageResponse> = apiServices.homepage(Authorization, userId)

        call.enqueue(object : Callback<HomepageResponse> {
            override fun onResponse(call: Call<HomepageResponse>, response: Response<HomepageResponse>) {

                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {

                        sharedPreferenceClass.setString(PROFILE_PICTURE_PATH, response.body()!!.result.profilePicturePath)
                        sharedPreferenceClass.setString(FIRST_NAME, response.body()!!.result.emloyeeFirstname)
                        sharedPreferenceClass.setString(LAST_NAME, response.body()!!.result.employeeLastname)
                        sharedPreferenceClass.setString(DESIGNATION, response.body()!!.result.designation)

                        if (response.body()!!.result.workingLocation == "WFO") {
                            sharedPreferenceSettings.saveToggleButtonSettings("Office")
                            sharedPreferenceSettings.saveCheckInCheckOutKeyForWFO(1)
                            if (response.body()!!.result.isCheckedIn == 1) {
                                binding.punchInOutMainButton.setBackgroundResource(R.drawable.home_office_out_background)
                                sharedPreferenceSettings.saveCheckInDataForWFO(convertLongToTime(response.body()!!.result.checkIn))
                            }
                            if (response.body()!!.result.isCheckedOut == 1) {
                                binding.checkInTime.text = convertLongToTime(response.body()!!.result.checkIn)
                                binding.checkOutTime.text = convertLongToTime(response.body()!!.result.checkOut)
                                binding.workingHours.text = convertMillisToDuration(response.body()!!.result.workingHours)
                                sharedPreferenceSettings.saveCheckInDataForWFO(convertLongToTime(response.body()!!.result.checkIn))
                                sharedPreferenceSettings.saveCheckOutDataForWFO(convertLongToTime(response.body()!!.result.checkOut), convertMillisToDuration(response.body()!!.result.workingHours))
                            }
                        } else if (response.body()!!.result.workingLocation == "WFH") {
                            sharedPreferenceSettings.saveToggleButtonSettings("Home")
                            sharedPreferenceSettings.saveCheckInCheckOutKeyForWFH(1)
                            if (response.body()!!.result.isCheckedIn == 1) {
                                binding.punchInOutMainButton.setBackgroundResource(R.drawable.home_office_out_background)
                                sharedPreferenceSettings.saveCheckInDataForWFH(convertLongToTime(response.body()!!.result.checkIn))
                            }
                            if (response.body()!!.result.isCheckedOut == 1) {
                                binding.checkInTime.text = convertLongToTime(response.body()!!.result.checkIn)
                                binding.checkOutTime.text = convertLongToTime(response.body()!!.result.checkOut)
                                binding.workingHours.text = convertMillisToDuration(response.body()!!.result.workingHours)
                                sharedPreferenceSettings.saveCheckInDataForWFH(convertLongToTime(response.body()!!.result.checkIn)
                                )
                                sharedPreferenceSettings.saveCheckOutDataForWFH(convertLongToTime(response.body()!!.result.checkOut), convertMillisToDuration(response.body()!!.result.workingHours))
                            }
                        }
                    }
                }
//                else {
//                    val jObjError = JSONObject(response.errorBody()!!.string())
//                    Toast.makeText(activity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
//                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<HomepageResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(activity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCompleteAddress(Latitude: Double, Longitude: Double): String? {
        var adderess = ""
        val geoCoder = Geocoder(activity, Locale.getDefault())
        try {
            val addresses = geoCoder.getFromLocation(Latitude, Longitude, 1)
            if (adderess != null) {
                val returnAdderess = addresses[0]
                val stringBuilderReturnAdderess = StringBuilder("")
                for (i in 0..returnAdderess.maxAddressLineIndex) {
                    stringBuilderReturnAdderess.append(returnAdderess.getAddressLine(i))
                }
                adderess = stringBuilderReturnAdderess.toString()
            } else {
                Toast.makeText(activity, "Address not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return adderess
    }

    private fun getCityName(lat: Double, long: Double): String {
        var cityName = ""
        var geoCoder = Geocoder(activity, Locale.getDefault())
        var address: MutableList<Address> = geoCoder.getFromLocation(lat, long, 1)
//        cityName = "${address[0].getAddressLine(0)}"
        cityName = address[0].subLocality + " " + address[0].locality + " " + address[0].adminArea + " " + address[0].countryName
        return cityName
    }

    private fun showLocationPrompt() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(requireActivity()).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            // Cast to a resolvable exception.
                            val resolvable: ResolvableApiException =
                                exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                requireActivity(), LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.

                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, MMM d, yy")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val today = Calendar.getInstance().time
        return dateFormat.format(today)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String? {
        val dateFormat = SimpleDateFormat("hh:mm a")
        val today = Calendar.getInstance().time
        return dateFormat.format(today)
    }

    private fun  clock() {
        val handler = Handler()
        Thread {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            handler.post {
                binding.timeText.text = getCurrentTime()
                clock()
            }
        }.start()
    }
}