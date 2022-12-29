package com.ibs.hrmioneemployee.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.*
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.ActivityLoginBinding
import com.ibs.hrmioneemployee.models.api_models.login.LoginModel
import com.ibs.hrmioneemployee.models.api_models.login.LoginResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.InternetConnection
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.ACCOUNT_HOLDER_NAME
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.ACCOUNT_NUMBER
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.BANK_NAME
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.BRANCH_ADDRESS
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.COUNTRY_CODE
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.CURRENT_ADDRESS
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.DATE_OF_JOINING
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.DEGREE_EDUCATION
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.DEPARTMENT
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.DESIGNATION
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.DEVICE_ADDRESS
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.DOB
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.EMAIL_ID
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.EMPLOYEE_ID
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.FIRST_NAME
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.GENDER
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.HIGH_SECONDARY
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.IFSC_CODE
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.INTERMEDIATE
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.LAST_NAME
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.LOGIN_TOKEN
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.MARITAL_STATUS
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.MOBILE_NUMBER
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.NATIONALITY
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.PROFILE_PICTURE_PATH
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.SHIFT_DETAILS
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.USER_ID
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.USER_NAME
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.WORK_ADDRESS
import com.ibs.hrmioneemployee.utilities.SharedPreferenceSettings
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private lateinit var sharedPreferenceSettings: SharedPreferenceSettings
    private lateinit var loginModel: LoginModel
    private lateinit var dataLoading: DataLoading
    private lateinit var deviceAddress: String
    private lateinit var lastLoginLocation: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val LOCATION_PERMISSION_REQ_CODE = 1000
    lateinit var locationRequest: LocationRequest
    private lateinit var emailId: String
    private lateinit var password: String
    private lateinit var deviceToken: String
    private var googleApiClient: GoogleApiClient? = null
    private val REQUEST_LOCATION = 199

    @SuppressLint("SetTextI18n", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        //Get Device Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
            if(!it.isSuccessful){
//                Log.e("TokenDetails","Token Failed to Receive")
                return@OnCompleteListener
            }
            deviceToken = it.result
//            Log.d("FirebaseDeviceToken",deviceToken)
        })


        val alertDialog: AlertDialog = AlertDialog.Builder(this, R.style.DeviceIdDialog).create()
        val alertView: View = LayoutInflater.from(this).inflate(R.layout.device_id_dialog, null)

        val deviceId = alertView.findViewById<EditText>(R.id.deviceId)

        binding.getDeviceId.setOnClickListener {
            alertDialog.setView(alertView)
            alertDialog.show()
            deviceId.setText(getDeviceID())
        }

        dataLoading = DataLoading(this)
        sharedPreferenceSettings = SharedPreferenceSettings(this)
        sharedPreferenceSettings.setRole()

        sharedPreferenceClass = SharedPreferenceClass(this@LoginActivity)

        deviceAddress = getDeviceID()

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, GenerateOtpForResetPasswordActivity::class.java))
        }

//        binding.register.setOnClickListener {
//            startActivity(Intent(this, SignUpActivity::class.java))
//        }

        binding.etEmailId.setText("ibs.aamir6@gmail.com")
        binding.etPassword.setText("123456")

        binding.loginText.setOnClickListener {

            emailId = binding.etEmailId.text.trim().toString()
            password = binding.etPassword.text.trim().toString()

            if (emailId.isEmpty()) {
                binding.etEmailId.error = "Email can't be empty"
                binding.etEmailId.requestFocus()
                return@setOnClickListener
            }
            else if (!emailId.contains("@") && !emailId.contains(".com")) {
                binding.etEmailId.error = "Enter a valid email"
                binding.etEmailId.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Password can't be empty"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

//            else if (password.length < 6) {
//                binding.etPassword.error = "Password must be minimum 6 characters"
//                binding.etPassword.requestFocus()
//                return@setOnClickListener
//            }

            if (InternetConnection.checkConnection(this)){
                getLastLocation()
            }
            else{
                Toast.makeText(this, "You're offline", Toast.LENGTH_SHORT).show()
            }

        }
    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun getLastLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkPermission()){

            if (isLocationEnabled()){

                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        getNewLocation()
                    }
                    else {
                        latitude = location.latitude
                        longitude = location.longitude
//                        lastLoginLocation = getCityName(latitude, longitude)
                        lastLoginLocation = getCompleteAddress(latitude, longitude)!!
                        Log.d("aamir", lastLoginLocation)
                        biometricAuthentication()
                    }
                }
            }
            else{
//                showLoginLocationPrompt()
                enableLoc()
//                Toast.makeText(this, "Location service is not enabled", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            requestPermission()
        }
    }

    //  check whether user given location permission to app or not
    private fun checkPermission(): Boolean {

        if (this.let {
                ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
            } == PackageManager.PERMISSION_GRANTED ||
            this.let {
                ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    //  request permission
    private fun requestPermission() {
        this.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQ_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQ_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.d("Debug:", "you have a permission")
            }
        }
    }

    private fun getCompleteAddress(Latitude: Double, Longitude: Double): String? {
        var adderess = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
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
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return adderess
    }

    @SuppressLint("NewApi")
    private fun getCityName(lat: Double, long: Double) : String {
        var cityName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var address: MutableList<Address> = geoCoder.getFromLocation(lat, long, 1)
//        cityName = "${address[0].getAddressLine(0)}"
        cityName = address[0].subLocality + " " + address[0].locality + " " + address[0].adminArea + " " + address[0].countryName
        return cityName
    }

    private fun getNewLocation() {

        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2

        if (this.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && this.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED
        ) {
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

    private fun enableLoc() {
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(bundle: Bundle?) {}
                override fun onConnectionSuspended(i: Int) {
                    googleApiClient?.connect()
                }
            })
            .addOnConnectionFailedListener {
            }.build()
        googleApiClient?.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30 * 1000.toLong()
        locationRequest.fastestInterval = 5 * 1000.toLong()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient!!, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(
                        this, REQUEST_LOCATION)
                } catch (e: IntentSender.SendIntentException) {
                }
            }
        }
    }

    private fun showLoginLocationPrompt() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task -> try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            // Cast to a resolvable exception.
                            val resolvable: ResolvableApiException = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                this, LocationRequest.PRIORITY_HIGH_ACCURACY
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

    //  check whether user's location services enable or disable
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceID(): String{
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun biometricAuthentication() {
        val keyguardManager: KeyguardManager = this.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (keyguardManager.isKeyguardSecure){
            val intent: Intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null)
            startActivityForResult(intent, 100)
        }
        else{
            callLoginApi()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK){
            if (requestCode == 100){
                callLoginApi()
            }
        }
    }

    private fun callLoginApi() {

        loginModel = LoginModel(emailId, password, deviceAddress, lastLoginLocation, "1", deviceToken)

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<LoginResponse> = apiServices.loginUser(loginModel)
        dataLoading.startLoading()

        call.enqueue(object : Callback<LoginResponse>{ override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

            if (response.isSuccessful && response.body() != null){
                if (response.body()!!.code == 200) {
                    saveLoginData(response.body()!!)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    Toast.makeText(this@LoginActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else{
                val jObjError = JSONObject(response.errorBody()!!.string())
                Toast.makeText(this@LoginActivity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
            }
            dataLoading.hideLoading()
        }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@LoginActivity, "Failure $t", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun saveLoginData(body: LoginResponse) {

        sharedPreferenceClass.loggedIn()
        sharedPreferenceClass.setString(LOGIN_TOKEN, body.token)

        //  personal
        sharedPreferenceClass.setString(PROFILE_PICTURE_PATH, body.result.profilePicturePath)
        sharedPreferenceClass.setInt(USER_ID, body.result.id)
        sharedPreferenceClass.setString(FIRST_NAME, body.result.firstName)
        sharedPreferenceClass.setString(LAST_NAME, body.result.lastName)
        sharedPreferenceClass.setString(DOB, body.result.dob)
        sharedPreferenceClass.setString(GENDER, body.result.gender)
        sharedPreferenceClass.setString(MARITAL_STATUS, body.result.maritalStatus)
        sharedPreferenceClass.setString(NATIONALITY, body.result.nationality)
        sharedPreferenceClass.setString(USER_NAME, body.result.userName)
        sharedPreferenceClass.setString(EMAIL_ID, body.result.email)
        sharedPreferenceClass.setInt(COUNTRY_CODE, body.result.countryCode)
        sharedPreferenceClass.setString(MOBILE_NUMBER, body.result.mobileNumber)
        sharedPreferenceClass.setString(CURRENT_ADDRESS, body.result.currentAddress)
        sharedPreferenceClass.setString(DEGREE_EDUCATION, body.result.degreeEducation)
        sharedPreferenceClass.setString(INTERMEDIATE, body.result.intermediate)
        sharedPreferenceClass.setString(HIGH_SECONDARY, body.result.highSchool)

        //  work
        sharedPreferenceClass.setInt(EMPLOYEE_ID, body.result.employeeId)
        sharedPreferenceClass.setString(DATE_OF_JOINING, body.result.dateOfJoining)
        sharedPreferenceClass.setString(DEPARTMENT, body.result.department)
        sharedPreferenceClass.setString(DESIGNATION, body.result.designation)
        sharedPreferenceClass.setString(SHIFT_DETAILS, body.result.shiftDetails)
        sharedPreferenceClass.setString(WORK_ADDRESS, body.result.workAddress)

        //  bank
        sharedPreferenceClass.setString(BANK_NAME, body.result.bankName)
        sharedPreferenceClass.setString(ACCOUNT_HOLDER_NAME, body.result.accountHolderName)
        sharedPreferenceClass.setString(ACCOUNT_NUMBER, body.result.accountNumber)
        sharedPreferenceClass.setString(BRANCH_ADDRESS, body.result.branchAddress)
        sharedPreferenceClass.setString(IFSC_CODE, body.result.ifscCode)
        sharedPreferenceClass.setString(DEVICE_ADDRESS, body.result.deviceAddress)

    }

    override fun onStart() {
        super.onStart()
        if (sharedPreferenceClass.isLoggedIn()){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }
}