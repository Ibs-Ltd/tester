package com.ibs.hrmioneemployee.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ibs.hrmioneemployee.activities.LoginActivity
import com.ibs.hrmioneemployee.activities.MainActivity
import com.ibs.hrmioneemployee.activities.MainActivity.Companion.galleryOpenOrNot
import com.ibs.hrmioneemployee.databinding.FragmentProfileBinding
import com.ibs.hrmioneemployee.models.api_models.login.LoginResponse
import com.ibs.hrmioneemployee.models.api_models.signout.SignoutResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.RealPathUtil
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import com.ibs.hrmioneemployee.utilities.SharedPreferenceSettings
import okhttp3.MediaType
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.SHARED_PREF_NAME
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass.Companion.USER_ID
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var setListener: DatePickerDialog.OnDateSetListener
    private lateinit var binding: FragmentProfileBinding
    private var personalEditSave: Boolean = true
    private var bankEditSave: Boolean = true
    private var path: String? = null
    private lateinit var call: Call<LoginResponse>
    private var userId: Int = 0
    private var setActivityDataListener: SetMainActivityData? = null
    private lateinit var sp: SharedPreferences
//    private lateinit var spKeyboard: SharedPreferences
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private lateinit var sharedPreferenceSettings: SharedPreferenceSettings
    private lateinit var Authorization: String
    private lateinit var dataLoading: DataLoading

    interface SetMainActivityData {
        fun editButtonClicked()
        fun saveButtonClicked()
        fun setNameAndDesignation()
        fun setProfilePicture()
    }

    fun setListener(mainActivity: MainActivity) {
        try {
            setActivityDataListener = mainActivity
        } catch (e: ClassCastException) {
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view: View = binding.root

        dataLoading = DataLoading(activity)
        sharedPreferenceClass = SharedPreferenceClass(requireActivity())
        sharedPreferenceSettings = SharedPreferenceSettings(requireActivity())

        Authorization = sharedPreferenceClass.getLoginToken()

        sp = requireActivity().getSharedPreferences(SHARED_PREF_NAME, AppCompatActivity.MODE_PRIVATE)
        userId = sp.getInt(USER_ID, -1)

        setProfilePersonalData()
        setProfileFragmentInitialData()

        val keyboard: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.includePersonalLayout.llLogout.setOnClickListener {
            callSignOutApi()
        }

        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        binding.includePersonalLayout.dateOfBirth.setOnClickListener {

            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                setListener,
                year,
                month,
                day
            )
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        }

        setListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

            val date = "$dayOfMonth ${getMonth(month)} $year"
            binding.includePersonalLayout.dateOfBirth.text = date
        }

        val gender = arrayOf("Male", "Female")
        val genderAdapter: ArrayAdapter<CharSequence> = ArrayAdapter<CharSequence>(
            requireActivity(),
            R.layout.profile_personal_spinner_layout,
            gender
        )
        genderAdapter.setDropDownViewResource(R.layout.profile_personal_spinner_layout)
        binding.includePersonalLayout.gender.adapter = genderAdapter


        val maritalStatus = arrayOf("Unmarried", "Married")
        val maritalStatusAdapter: ArrayAdapter<CharSequence> = ArrayAdapter<CharSequence>(
            requireActivity(),
            R.layout.profile_personal_spinner_layout,
            maritalStatus
        )
        maritalStatusAdapter.setDropDownViewResource(R.layout.profile_personal_spinner_layout)
        binding.includePersonalLayout.maritalStatus.adapter = maritalStatusAdapter


        val nationality = arrayOf("Indian", "Foreigner")
        val nationalityAdapter: ArrayAdapter<CharSequence> = ArrayAdapter<CharSequence>(
            requireActivity(),
            R.layout.profile_personal_spinner_layout,
            nationality
        )
        nationalityAdapter.setDropDownViewResource(R.layout.profile_personal_spinner_layout)
        binding.includePersonalLayout.nationality.adapter = nationalityAdapter

        setProfilePersonalSpinnerData()

//        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this, R.array.Font_Size, R.layout.setting_spinner_layout)
//        adapter.setDropDownViewResource(R.layout.setting_spinner_layout)
//        binding.fontSpinner.adapter = adapter

//        val genderSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, R.array.Gender)
//        binding.includePersonalLayout.gender.adapter = genderSpinnerAdapter
//
//        val maritalStatusSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, R.array.Marital_Status)
//        binding.includePersonalLayout.maritalStatus.adapter = maritalStatusSpinnerAdapter
//
//        val nationalitySpinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, R.array.Nationality)
//        binding.includePersonalLayout.nationality.adapter = nationalitySpinnerAdapter

        //  personal
        binding.includePersonalLayout.llPersonalGeneralEdit.setOnClickListener {
            if (!personalEditSave) {
                personalEditSave = true

                keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                binding.includePersonalLayout.personalGeneralEdit.text = "Edit"

                personalEditProfileApiCall()
                setActivityDataListener!!.saveButtonClicked()

                binding.includePersonalLayout.firstName.isEnabled = false
                binding.includePersonalLayout.lastName.isEnabled = false
                binding.includePersonalLayout.dateOfBirth.isEnabled = false
                binding.includePersonalLayout.gender.isEnabled = false
                binding.includePersonalLayout.maritalStatus.isEnabled = false
                binding.includePersonalLayout.nationality.isEnabled = false
                binding.includePersonalLayout.userName.isEnabled = false
                binding.includePersonalLayout.emailId.isEnabled = false
                binding.includePersonalLayout.mobileNumber.isEnabled = false
                binding.includePersonalLayout.currentAddress.isEnabled = false
                binding.includePersonalLayout.degreeEducation.isEnabled = false
                binding.includePersonalLayout.intermediate.isEnabled = false
                binding.includePersonalLayout.highSecondary.isEnabled = false
                binding.includePersonalLayout.ccpView.visibility = View.VISIBLE

            } else {
                personalEditSave = false

                setActivityDataListener!!.editButtonClicked()

                keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
                binding.includePersonalLayout.personalGeneralEdit.text = "Save"

                binding.includePersonalLayout.firstName.isEnabled = true
                binding.includePersonalLayout.lastName.isEnabled = true
                binding.includePersonalLayout.dateOfBirth.isEnabled = true
                binding.includePersonalLayout.gender.isEnabled = true
                binding.includePersonalLayout.maritalStatus.isEnabled = true
                binding.includePersonalLayout.nationality.isEnabled = true
                binding.includePersonalLayout.userName.isEnabled = true
                binding.includePersonalLayout.emailId.isEnabled = true
                binding.includePersonalLayout.mobileNumber.isEnabled = true
                binding.includePersonalLayout.currentAddress.isEnabled = true
                binding.includePersonalLayout.degreeEducation.isEnabled = true
                binding.includePersonalLayout.intermediate.isEnabled = true
                binding.includePersonalLayout.highSecondary.isEnabled = true
                binding.includePersonalLayout.ccpView.visibility = View.GONE
            }
        }

        //  work
//        binding.includeWorkLayout.llWorkGeneralEdit.setOnClickListener {
//            if (!workEditSave) {
//                workEditSave = true
//
//                keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
//                binding.includeWorkLayout.workGeneralEdit.text = "Edit"
//
//                binding.includeWorkLayout.fullName.isEnabled = false
//                binding.includeWorkLayout.employeeId.isEnabled = false
//                binding.includeWorkLayout.dateOfJoining.isEnabled = false
//                binding.includeWorkLayout.department.isEnabled = false
//                binding.includeWorkLayout.designation.isEnabled = false
//                binding.includeWorkLayout.shiftDetails.isEnabled = false
//                binding.includeWorkLayout.workAddress.isEnabled = false
//
//                binding.includeWorkLayout.fullName.setText(binding.includeWorkLayout.fullName.text.toString())
//                binding.includeWorkLayout.employeeId.setText(binding.includeWorkLayout.employeeId.text.toString())
//                binding.includeWorkLayout.dateOfJoining.setText(binding.includeWorkLayout.dateOfJoining.text.toString())
//                binding.includeWorkLayout.department.setText(binding.includeWorkLayout.department.text.toString())
//                binding.includeWorkLayout.designation.setText(binding.includeWorkLayout.designation.text.toString())
//                binding.includeWorkLayout.shiftDetails.setText(binding.includeWorkLayout.shiftDetails.text.toString())
//                binding.includeWorkLayout.workAddress.setText(binding.includeWorkLayout.workAddress.text.toString())
//
//            } else {
//                workEditSave = false
//
//                keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
//                binding.includeWorkLayout.workGeneralEdit.text = "Save"
//
//                binding.includeWorkLayout.fullName.isEnabled = true
//                binding.includeWorkLayout.employeeId.isEnabled = true
//                binding.includeWorkLayout.dateOfJoining.isEnabled = true
//                binding.includeWorkLayout.department.isEnabled = true
//                binding.includeWorkLayout.designation.isEnabled = true
//                binding.includeWorkLayout.shiftDetails.isEnabled = true
//                binding.includeWorkLayout.workAddress.isEnabled = true
//            }
//        }

        //  bank
        binding.includeBankLayout.llBankContactEdit.setOnClickListener {
            if (!bankEditSave) {
                bankEditSave = true

                keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                binding.includeBankLayout.bankContactEdit.text = "Edit"

                bankEditProfileApiCall()

                binding.includeBankLayout.bankName.isEnabled = false
                binding.includeBankLayout.accountHolderName.isEnabled = false
                binding.includeBankLayout.accountNumber.isEnabled = false
                binding.includeBankLayout.branchAddress.isEnabled = false
                binding.includeBankLayout.ifscCode.isEnabled = false

            } else {
                bankEditSave = false

                keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
                binding.includeBankLayout.bankContactEdit.text = "Save"

                binding.includeBankLayout.bankName.isEnabled = true
                binding.includeBankLayout.accountHolderName.isEnabled = true
                binding.includeBankLayout.accountNumber.isEnabled = true
                binding.includeBankLayout.branchAddress.isEnabled = true
                binding.includeBankLayout.ifscCode.isEnabled = true
            }
        }

        binding.llPersonal.setOnClickListener {

            //  for changing button background color on click
            binding.llPersonal.setBackgroundResource(R.drawable.profile_personal_background)
            binding.llWork.setBackgroundResource(R.drawable.profile_disable_background)
            binding.llBank.setBackgroundResource(R.drawable.profile_disable_background)


            //  for changing layout on button click
            binding.includePersonalLayout.root.visibility = View.VISIBLE
            binding.includeBankLayout.root.visibility = View.GONE
            binding.includeWorkLayout.root.visibility = View.GONE

            setProfilePersonalData()
        }

        binding.llWork.setOnClickListener {

            //  for changing button background color on click
            binding.llWork.setBackgroundResource(R.drawable.profile_work_background)
            binding.llPersonal.setBackgroundResource(R.drawable.profile_disable_background)
            binding.llBank.setBackgroundResource(R.drawable.profile_disable_background)

            //  for changing layout on button click
            binding.includePersonalLayout.root.visibility = View.GONE
            binding.includeBankLayout.root.visibility = View.GONE
            binding.includeWorkLayout.root.visibility = View.VISIBLE

            setProfileWorkData()
        }

        binding.llBank.setOnClickListener {

            //  for changing button background color on click
            binding.llBank.setBackgroundResource(R.drawable.profile_bank_background)
            binding.llWork.setBackgroundResource(R.drawable.profile_disable_background)
            binding.llPersonal.setBackgroundResource(R.drawable.profile_disable_background)

            //  for changing layout on button click
            binding.includePersonalLayout.root.visibility = View.GONE
            binding.includeBankLayout.root.visibility = View.VISIBLE
            binding.includeWorkLayout.root.visibility = View.GONE

            setProfileBankData()
        }
        return view
    }

    private fun setProfileFragmentInitialData() {

        binding.includeBankLayout.root.visibility = View.GONE
        binding.includeWorkLayout.root.visibility = View.GONE

        binding.llWork.setBackgroundResource(R.drawable.profile_disable_background)
        binding.llBank.setBackgroundResource(R.drawable.profile_disable_background)

        binding.includePersonalLayout.gender.isEnabled = false
        binding.includePersonalLayout.maritalStatus.isEnabled = false
        binding.includePersonalLayout.nationality.isEnabled = false

        binding.includePersonalLayout.ccpView.setOnClickListener {
            // nothing will happen... only for disable country code picker onclick
        }
    }

    private fun personalEditProfileApiCall() {

        val firstName = binding.includePersonalLayout.firstName.text.trim().toString()
        val lastName = binding.includePersonalLayout.lastName.text.trim().toString()
        val dob = binding.includePersonalLayout.dateOfBirth.text.trim().toString()
        val gender = binding.includePersonalLayout.gender.selectedItem.toString()
        val maritalStatus = binding.includePersonalLayout.maritalStatus.selectedItem.toString()
        val nationality = binding.includePersonalLayout.nationality.selectedItem.toString()
        val userName = binding.includePersonalLayout.userName.text.trim().toString()
        val emailId = binding.includePersonalLayout.emailId.text.trim().toString()
        val countryCode: Int = binding.includePersonalLayout.ccp.selectedCountryCodeAsInt
        val mobileNumber = binding.includePersonalLayout.mobileNumber.text.trim().toString()
        val currentAddress = binding.includePersonalLayout.currentAddress.text.trim().toString()
        val degreeEducation = binding.includePersonalLayout.degreeEducation.text.trim().toString()
        val intermediate = binding.includePersonalLayout.intermediate.text.trim().toString()
        val highSecondary = binding.includePersonalLayout.highSecondary.text.trim().toString()

        val sp: SharedPreferences = requireActivity().getSharedPreferences("ProfileImage", AppCompatActivity.MODE_PRIVATE)
        val photo: String? = sp.getString("image", "photo")!!

        if (!photo.equals("photo")) {
            val b = Base64.decode(photo, Base64.DEFAULT)
            val inputStream: InputStream = ByteArrayInputStream(b)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            val uri: Uri = getImageUri(bitmap)
            path = RealPathUtil.getPath(requireActivity(), uri).toString()
        }

        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)

        if (galleryOpenOrNot == 1) {

            galleryOpenOrNot = 0

            //  for image only
            val file = File(path!!)
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val image: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", file.name, requestFile)

            // for other details of user
            val firstName2: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), firstName)
            val lastName2: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), lastName)
            val dob2: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), dob)
            val gender2: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), gender)
            val maritalStatus2: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), maritalStatus)
            val nationality2: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), nationality)
            val userName2: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), userName)
            val emailId2: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), emailId)
//        val countryCode2: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), countryCode)
            val mobileNumber2: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), mobileNumber)
            val currentAddress2: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), currentAddress)
            val degreeEducation2: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), degreeEducation)
            val intermediate2: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), intermediate)
            val highSecondary2: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), highSecondary)

            call = apiServices.editProfileWithImage(
                sharedPreferenceClass.getLoginToken(),
                image,
                userId,
                firstName2,
                lastName2,
                dob2,
                gender2,
                maritalStatus2,
                nationality2,
                userName2,
                emailId2,
                countryCode,
                mobileNumber2,
                currentAddress2,
                degreeEducation2,
                intermediate2,
                highSecondary2
            )
        } else {
            call = apiServices.editProfileWithoutImage(
                sharedPreferenceClass.getLoginToken(),
                userId,
                firstName,
                lastName,
                dob,
                gender,
                maritalStatus,
                nationality,
                userName,
                emailId,
                countryCode,
                mobileNumber,
                currentAddress,
                degreeEducation,
                intermediate,
                highSecondary
            )
        }

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        saveLoginData(response.body()!!)
                        if (galleryOpenOrNot == 1) {
                            setActivityDataListener!!.setNameAndDesignation()
                            setActivityDataListener!!.setProfilePicture()
                        } else {
                            setActivityDataListener!!.setNameAndDesignation()
                        }
                        Toast.makeText(
                            requireActivity(), "Successfully updated", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    setProfilePersonalData()
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(
                        requireActivity(), "Error Code: " + response.code() + "\nMessage: "
                                + jObjError.getString("message"), Toast.LENGTH_SHORT
                    ).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                dataLoading.hideLoading()
                setProfilePersonalData()
                setActivityDataListener!!.setProfilePicture()
                Toast.makeText(requireActivity(), "Failed to save changes", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun bankEditProfileApiCall() {
        val bankName = binding.includeBankLayout.bankName.text.trim().toString()
        val accountHolderName = binding.includeBankLayout.accountHolderName.text.trim().toString()
        val accountNumber = binding.includeBankLayout.accountNumber.text.trim().toString()
        val branchAddress = binding.includeBankLayout.branchAddress.text.trim().toString()
        val ifscCode = binding.includeBankLayout.ifscCode.text.trim().toString()

//        if (bankName.isEmpty()){
//            binding.includeBankLayout.bankName.error = "Bank name can't be empty"
//            binding.includeBankLayout.bankName.requestFocus()
//            return
//        }
//
//        if (accountHolderName.isEmpty()){
//            binding.includeBankLayout.accountHolderName.error = "Account holder name can't be empty"
//            binding.includeBankLayout.accountHolderName.requestFocus()
//            return
//        }
//
//        if (accountNumber.isEmpty()){
//            binding.includeBankLayout.accountNumber.error = "Account number can't be empty"
//            binding.includeBankLayout.accountNumber.requestFocus()
//            return
//        }
//
//        if (branchAddress.isEmpty()){
//            binding.includeBankLayout.branchAddress.error = "Branch address can't be empty"
//            binding.includeBankLayout.branchAddress.requestFocus()
//            return
//        }
//
//        if (ifscCode.isEmpty()){
//            binding.includeBankLayout.ifscCode.error = "IFSC code can't be empty"
//            binding.includeBankLayout.ifscCode.requestFocus()
//            return
//        }
        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<LoginResponse> = apiServices.editProfileBank(
            sharedPreferenceClass.getLoginToken(), userId, bankName,
            accountHolderName, accountNumber, branchAddress, ifscCode
        )

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        saveLoginData(response.body()!!)
                        Toast.makeText(requireActivity(), "Successfully updated", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    setProfileBankData()
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(
                        requireActivity(), "Error Code: " + response.code()
                                + "\nMessage: " + jObjError.getString("message"), Toast.LENGTH_SHORT
                    ).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                dataLoading.hideLoading()
                setProfileBankData()
                Toast.makeText(requireActivity(), "Failed to save changes", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

//    fun imageFromBitmapToUri(context: Context, bitmap: Bitmap): Uri? {
//        val bytes = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, null, null)
//        return Uri.parse(path)
//    }

    // Get uri of images from camera function
    private fun getImageUri(inImage: Bitmap): Uri {

        val tempFile = File.createTempFile("ProfilePicture", ".png")
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(tempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()
        return Uri.fromFile(tempFile)
    }

    private fun setProfilePersonalData() {

        binding.includePersonalLayout.firstName.setText(sp.getString("FirstName", ""))
        binding.includePersonalLayout.lastName.setText(sp.getString("LastName", ""))
        binding.includePersonalLayout.dateOfBirth.text = sp.getString("DOB", "")
        setProfilePersonalSpinnerData()
        binding.includePersonalLayout.userName.setText(sp.getString("Username", ""))
        binding.includePersonalLayout.emailId.setText(sp.getString("EmailId", ""))
        binding.includePersonalLayout.ccp.setCountryForPhoneCode(sp.getInt("CountryCode", -1))
        binding.includePersonalLayout.mobileNumber.setText(sp.getString("MobileNumber", ""))
        binding.includePersonalLayout.currentAddress.setText(sp.getString("CurrentAddress", ""))
        binding.includePersonalLayout.degreeEducation.setText(sp.getString("DegreeEducation", ""))
        binding.includePersonalLayout.intermediate.setText(sp.getString("Intermediate", ""))
        binding.includePersonalLayout.highSecondary.setText(sp.getString("HighSecondary", ""))

    }

    private fun setProfilePersonalSpinnerData() {
        if (sp.getString("Gender", "").equals("Male")) {
            binding.includePersonalLayout.gender.setSelection(0)
        } else if (sp.getString("Gender", "").equals("Female")) {
            binding.includePersonalLayout.gender.setSelection(1)
        }
        if (sp.getString("MaritalStatus", "").equals("Unmarried")) {
            binding.includePersonalLayout.maritalStatus.setSelection(0)
        } else if (sp.getString("MaritalStatus", "").equals("Married")) {
            binding.includePersonalLayout.maritalStatus.setSelection(1)
        }
        if (sp.getString("Nationality", "").equals("Indian")) {
            binding.includePersonalLayout.nationality.setSelection(0)
        } else if (sp.getString("Nationality", "").equals("Foreigner")) {
            binding.includePersonalLayout.nationality.setSelection(1)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setProfileWorkData() {
        binding.includeWorkLayout.fullName.setText("${sp.getString("FirstName", "")} ${sp.getString("LastName", "")}")

        if (sp.getInt("EmployeeId", -1) != -1)
            binding.includeWorkLayout.employeeId.setText("" + sp.getInt("EmployeeId", -1))
        else binding.includeWorkLayout.employeeId.setText("NA")

        if (!sp.getString("DateOfJoining", "").equals(""))
            binding.includeWorkLayout.dateOfJoining.setText(sp.getString("DateOfJoining", ""))
        else binding.includeWorkLayout.dateOfJoining.setText("NA")

        if (!sp.getString("Department", "").equals(""))
            binding.includeWorkLayout.department.setText(sp.getString("Department", ""))
        else binding.includeWorkLayout.department.setText("NA")

        if (!sp.getString("Designation", "").equals(""))
            binding.includeWorkLayout.designation.setText(sp.getString("Designation", ""))
        else binding.includeWorkLayout.designation.setText("NA")

        if (!sp.getString("ShiftDetails", "").equals(""))
            binding.includeWorkLayout.shiftDetails.setText(sp.getString("ShiftDetails", ""))
        else binding.includeWorkLayout.shiftDetails.setText("NA")

        if (!sp.getString("WorkAddress", "").equals(""))
            binding.includeWorkLayout.workAddress.setText(sp.getString("WorkAddress", ""))
        else binding.includeWorkLayout.workAddress.setText("NA")
    }

    private fun setProfileBankData() {
        binding.includeBankLayout.bankName.setText(sp.getString("BankName", ""))
        binding.includeBankLayout.accountHolderName.setText(sp.getString("AccountHolderName", ""))
        binding.includeBankLayout.accountNumber.setText(sp.getString("AccountNumber", ""))
        binding.includeBankLayout.branchAddress.setText(sp.getString("BranchAddress", ""))
        binding.includeBankLayout.ifscCode.setText(sp.getString("IfscCode", ""))
    }

    private fun getMonth(month: Int): String {

        var month2 = ""

        when (month) {
            0 -> month2 = "January"
            1 -> month2 = "February"
            2 -> month2 = "March"
            3 -> month2 = "April"
            4 -> month2 = "May"
            5 -> month2 = "June"
            6 -> month2 = "July"
            7 -> month2 = "August"
            8 -> month2 = "September"
            9 -> month2 = "October"
            10 -> month2 = "November"
            11 -> month2 = "December"
        }
        return month2
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
                        startActivity(Intent(activity, LoginActivity::class.java))
                        activity!!.finish()
                    }
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<SignoutResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(requireActivity(), "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveLoginData(body: LoginResponse) {

        //  personal
        sharedPreferenceClass.setString(SharedPreferenceClass.PROFILE_PICTURE_PATH, body.result.profilePicturePath)
        sharedPreferenceClass.setInt(USER_ID, body.result.id)
        sharedPreferenceClass.setString(SharedPreferenceClass.FIRST_NAME, body.result.firstName)
        sharedPreferenceClass.setString(SharedPreferenceClass.LAST_NAME, body.result.lastName)
        sharedPreferenceClass.setString(SharedPreferenceClass.DOB, body.result.dob)
        sharedPreferenceClass.setString(SharedPreferenceClass.GENDER, body.result.gender)
        sharedPreferenceClass.setString(SharedPreferenceClass.MARITAL_STATUS, body.result.maritalStatus)
        sharedPreferenceClass.setString(SharedPreferenceClass.NATIONALITY, body.result.nationality)
        sharedPreferenceClass.setString(SharedPreferenceClass.USER_NAME, body.result.userName)
        sharedPreferenceClass.setString(SharedPreferenceClass.EMAIL_ID, body.result.email)
        sharedPreferenceClass.setInt(SharedPreferenceClass.COUNTRY_CODE, body.result.countryCode)
        sharedPreferenceClass.setString(SharedPreferenceClass.MOBILE_NUMBER, body.result.mobileNumber)
        sharedPreferenceClass.setString(SharedPreferenceClass.CURRENT_ADDRESS, body.result.currentAddress)
        sharedPreferenceClass.setString(SharedPreferenceClass.DEGREE_EDUCATION, body.result.degreeEducation)
        sharedPreferenceClass.setString(SharedPreferenceClass.INTERMEDIATE, body.result.intermediate)
        sharedPreferenceClass.setString(SharedPreferenceClass.HIGH_SECONDARY, body.result.highSchool)

        //  work
        sharedPreferenceClass.setInt(SharedPreferenceClass.EMPLOYEE_ID, body.result.employeeId)
        sharedPreferenceClass.setString(SharedPreferenceClass.DATE_OF_JOINING, body.result.dateOfJoining)
        sharedPreferenceClass.setString(SharedPreferenceClass.DEPARTMENT, body.result.department)
        sharedPreferenceClass.setString(SharedPreferenceClass.DESIGNATION, body.result.designation)
        sharedPreferenceClass.setString(SharedPreferenceClass.SHIFT_DETAILS, body.result.shiftDetails)
        sharedPreferenceClass.setString(SharedPreferenceClass.WORK_ADDRESS, body.result.workAddress)

        //  bank
        sharedPreferenceClass.setString(SharedPreferenceClass.BANK_NAME, body.result.bankName)
        sharedPreferenceClass.setString(SharedPreferenceClass.ACCOUNT_HOLDER_NAME, body.result.accountHolderName)
        sharedPreferenceClass.setString(SharedPreferenceClass.ACCOUNT_NUMBER, body.result.accountNumber)
        sharedPreferenceClass.setString(SharedPreferenceClass.BRANCH_ADDRESS, body.result.branchAddress)
        sharedPreferenceClass.setString(SharedPreferenceClass.IFSC_CODE, body.result.ifscCode)
        sharedPreferenceClass.setString(SharedPreferenceClass.DEVICE_ADDRESS, body.result.deviceAddress)

    }
}