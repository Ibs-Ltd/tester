package com.ibs.hrmioneemployee.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ibs.hrmioneemployee.activities.drawer_activities.SettingsActivity
import com.ibs.hrmioneemployee.fragments.ActivityFragment
import com.ibs.hrmioneemployee.fragments.HomeFragment
import com.ibs.hrmioneemployee.fragments.ProfileFragment
import com.ibs.hrmioneemployee.interfaces.SetFragmentData
import com.ibs.hrmioneemployee.models.api_models.signout.SignoutResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.SHARED_PREF_NAME
import com.ibs.hrmioneemployee.utilities.SharedPreferenceSettings
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.activities.drawer_activities.CompanyPolicyActivity2
import com.ibs.hrmioneemployee.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(), ProfileFragment.SetMainActivityData {

    private lateinit var binding: ActivityMainBinding
    private var toggleHomeBoolean: Boolean = true
    private var toggleOfficeBoolean: Boolean = false
    private var GALLERY_REQUEST_CODE = 1000
    private lateinit var uri: Uri
    val ROOT_FRAGMENT_TAG: String = "HomeFragment"
    private lateinit var sharedPreferenceSettings: SharedPreferenceSettings
    private lateinit var sp: SharedPreferences
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private var userId = 0
    private lateinit var Authorization: String
    private lateinit var dataLoading: DataLoading

    companion object {
        var galleryOpenOrNot: Int = 0

        fun encodeToBase64(image: Bitmap): String {
            val bytes = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            val b = bytes.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }
    }

    private var setFragmentDataListener: SetFragmentData? = null

    fun setHomeFragmentListener(myFragment: HomeFragment?) {
        try {
            setFragmentDataListener = myFragment
        } catch (e: ClassCastException) {
        }
    }

    // when we want to perform any action on activity from fragment
    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        try {
          (fragment as ProfileFragment).setListener(this)
        } catch (e: ClassCastException) {
            Log.e("TAG", e.toString())
        }
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        dataLoading = DataLoading(this)

        sharedPreferenceClass = SharedPreferenceClass(this)
        Authorization = sharedPreferenceClass.getLoginToken()
        sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        userId = sp.getInt("UserId", -1)

        sharedPreferenceSettings = SharedPreferenceSettings(this)
        if (sharedPreferenceSettings.getToggleButtonSettings() == "Home") {
            whenClickedOnHome()
            toggleHomeBoolean = false
            toggleOfficeBoolean = true
        } else if (sharedPreferenceSettings.getToggleButtonSettings() == "Office") {
            whenClickedOnOffice()
        }

        setNameAndDesignation()
        setProfilePicture()


        val vibration = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        addFragment(HomeFragment())

        binding.activityBottomLine.visibility = View.GONE
        binding.mailBottomLine.visibility = View.GONE
        binding.profileBottomLine.visibility = View.GONE

        binding.llHomeOffice.visibility = View.INVISIBLE
        binding.toggleButton.visibility = View.VISIBLE

        binding.toggleButton.setOnClickListener {
            vibration.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
            binding.toggleButton.visibility = View.INVISIBLE
            binding.llHomeOffice.visibility = View.VISIBLE
        }

        binding.llDownArrow.setOnClickListener {
            vibration.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
            binding.toggleButton.visibility = View.VISIBLE
            binding.llHomeOffice.visibility = View.INVISIBLE
        }

        //  toggle home button
        binding.llHome.setOnClickListener {

            binding.toggleButton.visibility = View.VISIBLE
            binding.llHomeOffice.visibility = View.INVISIBLE

            vibration.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))

//            if (toggleHomeBoolean) {
//                toggleHomeBoolean = false
//                toggleOfficeBoolean = true
//                vibration.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE)
//                )
//            } else {
//                toggleOfficeBoolean = true
//            }

            if (sharedPreferenceSettings.getCheckInCheckOutKeyForWFO() != 1){

                sharedPreferenceSettings.saveToggleButtonSettings("Home")
                setFragmentDataListener!!.setHomeClickedData()

                whenClickedOnHome()
            }
            else{
                Toast.makeText(this, "You have already checked in from office", Toast.LENGTH_SHORT).show()
            }
        }

        //  toggle office button
        binding.llOffice.setOnClickListener {
            binding.toggleButton.visibility = View.VISIBLE
            binding.llHomeOffice.visibility = View.INVISIBLE
            vibration.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))

//            if (toggleOfficeBoolean) {
//                toggleHomeBoolean = true
//                toggleOfficeBoolean = false
//                vibration.vibrate(
//                    VibrationEffect.createOneShot(
//                        150,
//                        VibrationEffect.DEFAULT_AMPLITUDE
//                    )
//                )
//            } else {
//                toggleHomeBoolean = true
//            }

            if (sharedPreferenceSettings.getCheckInCheckOutKeyForWFH() != 1){

                sharedPreferenceSettings.saveToggleButtonSettings("Office")
                setFragmentDataListener!!.setOfficeClickedData()

                whenClickedOnOffice()
            }
            else{
                Toast.makeText(this, "You have already checked in from home", Toast.LENGTH_SHORT).show()
            }
        }

        binding.homeLinearLayout.setOnClickListener {

            replaceHomeFragment(HomeFragment())
            binding.homeBottomLine.visibility = View.VISIBLE
            binding.activityBottomLine.visibility = View.GONE
            binding.mailBottomLine.visibility = View.GONE
            binding.profileBottomLine.visibility = View.GONE

            binding.llHomeOffice.visibility = View.INVISIBLE
            binding.toggleButton.visibility = View.VISIBLE

            saveButtonClicked()
        }

        binding.activityLinearLayout.setOnClickListener {

            replaceFragment(ActivityFragment())
            binding.activityBottomLine.visibility = View.VISIBLE
            binding.homeBottomLine.visibility = View.GONE
            binding.mailBottomLine.visibility = View.GONE
            binding.profileBottomLine.visibility = View.GONE

            binding.toggleButton.visibility = View.INVISIBLE
            binding.llHomeOffice.visibility = View.INVISIBLE

            saveButtonClicked()
        }

        binding.mailLinearLayout.setOnClickListener {
            startActivity(Intent(this, MailActivity::class.java))
        }

        binding.profileLinearLayout.setOnClickListener {

            replaceFragment(ProfileFragment())
            binding.profileBottomLine.visibility = View.VISIBLE
            binding.activityBottomLine.visibility = View.GONE
            binding.mailBottomLine.visibility = View.GONE
            binding.homeBottomLine.visibility = View.GONE

            binding.toggleButton.visibility = View.INVISIBLE
            binding.llHomeOffice.visibility = View.INVISIBLE
        }

        binding.notification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        val headerView: View = binding.navigationView.getHeaderView(0)
        val crossImage: ImageView = headerView.findViewById(R.id.crossImage)
        val llHome: LinearLayout = headerView.findViewById(R.id.llHome)
        val llLogout: LinearLayout = headerView.findViewById(R.id.llLogout)
        val llCompanyPolicies: LinearLayout = headerView.findViewById(R.id.llCompanyPolicies)
        val llSettings: LinearLayout = headerView.findViewById(R.id.llSettings)

        binding.hamburgerIcon.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        crossImage.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        llHome.setOnClickListener {
            replaceHomeFragment(HomeFragment())
            binding.homeBottomLine.visibility = View.VISIBLE
            binding.activityBottomLine.visibility = View.GONE
            binding.mailBottomLine.visibility = View.GONE
            binding.profileBottomLine.visibility = View.GONE

            binding.llHomeOffice.visibility = View.INVISIBLE
            binding.toggleButton.visibility = View.VISIBLE

            saveButtonClicked()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        llCompanyPolicies.setOnClickListener {
            startActivity(Intent(this, CompanyPolicyActivity2::class.java))
//            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        llSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
//            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        llLogout.setOnClickListener {

            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.logout_bottom_sheet_layout, null)
            val cancel: TextView = view.findViewById(R.id.cancelText)
            val confirm: TextView = view.findViewById(R.id.confirmText)

            cancel.setOnClickListener {
                dialog.dismiss()
            }

            confirm.setOnClickListener {
                callSignOutApi()
            }
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }

        binding.profileImage.isEnabled = false
        binding.profileImage.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun callSignOutApi() {
        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<SignoutResponse> = apiServices.signOut(Authorization, userId)

        call.enqueue(object : Callback<SignoutResponse>{
            override fun onResponse(call: Call<SignoutResponse>, response: Response<SignoutResponse>) {

                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.code == 200){
                        sharedPreferenceClass.logOut()
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<SignoutResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@MainActivity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    @SuppressLint("SetTextI18n")
//    private fun setUserNameAndDesignation() {
//        val sp: SharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
//        val firstName: String? = sp.getString("FirstName", "")
//        val lastName: String? = sp.getString("LastName", "")
//        binding.userFullName.text = "$firstName $lastName"
//        binding.userDesignation.text = sp.getString("Designation", "")
//    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
    }

    private fun replaceHomeFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(fragment)
        fragmentTransaction.commit()
        fragmentManager.popBackStack()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(ROOT_FRAGMENT_TAG)
        fragmentManager.popBackStack(ROOT_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentTransaction.commit()
    }

    override fun editButtonClicked() {
        binding.uploadImageIcon.visibility = View.VISIBLE
        binding.profileImage.isEnabled = true
    }

    override fun saveButtonClicked() {
        binding.uploadImageIcon.visibility = View.INVISIBLE
        binding.profileImage.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    override fun setNameAndDesignation() {

        val sp: SharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val firstName: String? = sp.getString("FirstName", "")
        val lastName: String? = sp.getString("LastName", "")
        binding.userFullName.text = "$firstName $lastName"
        binding.userDesignation.text = sp.getString("Designation", "")
    }

    override fun setProfilePicture() {
        val sp: SharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val profilePicturePath: String? = sp.getString("profilePicturePath", "")
        if (!profilePicturePath.equals("")) {
            Glide.with(this).load(profilePicturePath).apply(
                RequestOptions.placeholderOf(R.drawable.dashboard_profile).error(R.drawable.dashboard_profile))
                .into(binding.profileImage)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        var fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        supportFragmentManager.findFragmentById(R.id.fragment_container)?.let {
            // the fragment exists

            when (it) {
                is HomeFragment -> {
                    binding.homeBottomLine.visibility = View.VISIBLE
                    binding.activityBottomLine.visibility = View.GONE
                    binding.mailBottomLine.visibility = View.GONE
                    binding.profileBottomLine.visibility = View.GONE

                    binding.llHomeOffice.visibility = View.INVISIBLE
                    binding.toggleButton.visibility = View.VISIBLE
                }
                is ActivityFragment -> {
                    binding.activityBottomLine.visibility = View.VISIBLE
                    binding.homeBottomLine.visibility = View.GONE
                    binding.mailBottomLine.visibility = View.GONE
                    binding.profileBottomLine.visibility = View.GONE

                    binding.toggleButton.visibility = View.INVISIBLE
                    binding.llHomeOffice.visibility = View.INVISIBLE
                }
                is ProfileFragment -> {
                    binding.profileBottomLine.visibility = View.VISIBLE
                    binding.activityBottomLine.visibility = View.GONE
                    binding.mailBottomLine.visibility = View.GONE
                    binding.homeBottomLine.visibility = View.GONE

                    binding.toggleButton.visibility = View.INVISIBLE
                    binding.llHomeOffice.visibility = View.INVISIBLE
                }
            }
        }
    }

//    override fun onBackPressed() {
//        if (this.binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
//            this.binding.drawerLayout.closeDrawer(GravityCompat.START)
//        }
//        else
//            onBackPressed()
//    }

    private fun pickImageFromGallery() {

        //  old
//        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_PICK
//            startActivityForResult(intent, GALLERY_REQUEST_CODE)
//
//        } else {
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
//        }

        ImagePicker.with(this)
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
            .start(400)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //  old
//        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK ){
//            uri = data?.data!!
//            binding.profileImage.setImageURI(uri)
//
//            galleryOpenOrNot = 1
//
//            val bitmap = (binding.profileImage.drawable as BitmapDrawable).bitmap
//            storeImageToSharedPreference(bitmap)
//        }

        if (requestCode == 400) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val uri: Uri = data.data!!
                    // Use Uri object instead of File to avoid storage permissions
                    binding.profileImage.setImageURI(uri)
                    galleryOpenOrNot = 1
                    val bitmap = (binding.profileImage.drawable as BitmapDrawable).bitmap
                    storeImageToSharedPreference(bitmap)

                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // new way without startActivityFOrResult
//    private fun pickImageFromGallery() =
//        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            val photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.type = "image/*"
//            someActivityResultLauncher.launch(photoPickerIntent)
//        }   else{
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
//        }
//    private var someActivityResultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//        if (result.resultCode == RESULT_OK) {
//            // There are no request codes
//            // doSomeOperations();
//            val data = result.data
////            val uri: Uri? = data!!.data
//            val selectedImage = Objects.requireNonNull(data)!!.data
//            var imageStream: InputStream? = null
//            try {
//                imageStream = contentResolver.openInputStream(selectedImage!!)
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            }
//            BitmapFactory.decodeStream(imageStream)
//            binding.profileImage.setImageURI(selectedImage) // To display selected image in image view
//
//            val bitmap = (binding.profileImage.drawable as BitmapDrawable).bitmap
//
//            storeImageToSharedPreference(bitmap)
//        }
//    }

    private fun storeImageToSharedPreference(bitmap: Bitmap?) {
        val sp: SharedPreferences = getSharedPreferences("ProfileImage", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.putString("image", encodeToBase64(bitmap!!))
        editor.apply()
    }

    private fun whenClickedOnOffice() {
        binding.llHome.setBackgroundResource(R.drawable.office_home_transparent)
        binding.homeImage.setImageResource(R.drawable.home_white)
        binding.llOffice.setBackgroundResource(R.drawable.office_home_background)
        binding.officeImage.setImageResource(R.drawable.office_white_clr)
    }

    private fun whenClickedOnHome() {
        binding.llHome.setBackgroundResource(R.drawable.office_home_background)
        binding.homeImage.setImageResource(R.drawable.home_white_clr)
        binding.llOffice.setBackgroundResource(R.drawable.office_home_transparent)
        binding.officeImage.setImageResource(R.drawable.office_white)
    }
}
