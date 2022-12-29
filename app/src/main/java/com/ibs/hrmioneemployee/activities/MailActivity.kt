package com.ibs.hrmioneemployee.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.hrmioneemployee.adapters.Mail_Send_Adapter
import com.ibs.hrmioneemployee.models.api_models.mail.MailResponse
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.ibs.hrmioneemployee.databinding.ActivityMailBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File

class MailActivity : AppCompatActivity(),Mail_Send_Adapter.OnItemClickListener,PickiTCallbacks {

    private lateinit var binding: ActivityMailBinding
    lateinit var pickIt: PickiT
    lateinit var loading: DataLoading
    private lateinit var Authorization: String
    private lateinit var sharedPreferenceClass: SharedPreferenceClass

    // private var filePath1: String? = null
    private var arrayList = ArrayList<String>()
    private lateinit var filePath1: ArrayList<String>
    private lateinit var adapter: Mail_Send_Adapter
    private lateinit var filePathArray: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        filePath1 = ArrayList()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        sharedPreferenceClass = SharedPreferenceClass(this)
        Authorization = sharedPreferenceClass.getLoginToken()
        loading = DataLoading(this)

        pickIt = PickiT(this, this, this)

        binding.selectFile.setOnClickListener {
            if (checkPermission2()) {
                selectPdf()
            } else {
                requestPermissionForReadExternalStorage()

            }
        }

        binding.backImage.setOnClickListener {
            onBackPressed()
        }

        binding.sendMail.setOnClickListener {
            if (binding.senderMailId.text.isEmpty() || binding.receiverMailId.text.isEmpty() || binding.subject.text.isEmpty() || binding.message.text.isEmpty()) {
                Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            }
            else {
                mailSendApiCall()
            }
        }
        /*binding.close.setOnClickListener(){
            binding.filePathLayout.isVisible = false
            binding.filePath.text = ""
        }*/
    }

    private fun mailSendApiCall() {
        if (filePath1.isNotEmpty() == true) {
            loading.startLoading()
            val arrayList = ArrayList<MultipartBody.Part>()
            for (i in 0 until filePath1.size) {
                val file = File(filePath1[i].toString())
                val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val filePath = MultipartBody.Part.createFormData("file", file.name, requestFile)
                arrayList.add(filePath)
            }
            val senderId = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                binding.senderMailId.text.toString()
            )
            val receiverId = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                binding.receiverMailId.text.toString()
            )
            val subject = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                binding.subject.text.toString()
            )
            val message = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                binding.message.text.toString()
            )
            // val senderId = RequestBody.create(MediaType.parse("multipart/form-data"), binding.senderMailId.text.toString())
            val retrofit = RetrofitClient.getRetrofit().create(ApiServices::class.java)
            val call = retrofit.sendMail(Authorization, senderId, receiverId, subject, message, arrayList)

            call.enqueue(object : retrofit2.Callback<MailResponse> {
                override fun onResponse(
                    call: Call<MailResponse>,
                    response: Response<MailResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@MailActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        loading.hideLoading()
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@MailActivity,
                            "Error Code: " + response.code()
                                    + "\nMessage: " + jObjError.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("sadhna", jObjError.getString("message"))
                        loading.hideLoading()
                    }
                }

                override fun onFailure(call: Call<MailResponse>, t: Throwable) {
                    Toast.makeText(this@MailActivity, t.message, Toast.LENGTH_SHORT).show()
                    loading.hideLoading()
                    Log.d("sadhna", t.message.toString())
                }

            })

        } else {
            loading.startLoading()
            val retrofit = RetrofitClient.getRetrofit().create(ApiServices::class.java)
            val call = retrofit.sendMailWithoutFile(
                Authorization,
                binding.senderMailId.text.toString(),
                binding.receiverMailId.text.toString(),
                binding.subject.text.toString(),
                binding.message.text.toString()
            )

            call.enqueue(object : retrofit2.Callback<MailResponse> {
                override fun onResponse(
                    call: Call<MailResponse>,
                    response: Response<MailResponse>
                ) {
                    loading.hideLoading()
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@MailActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()

                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@MailActivity,
                            "Error Code: " + response.code()
                                    + "\nMessage: " + jObjError.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MailResponse>, t: Throwable) {
                    Toast.makeText(this@MailActivity, t.message, Toast.LENGTH_SHORT).show()
                    loading.hideLoading()
                }

            })
        }

    }

    private fun selectPdf() {
        val pdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        pdfIntent.type = "*/*"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        pdfIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(pdfIntent, 12)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                pickIt.getMultiplePaths(data.clipData)
            } else {
                pickIt.getPath(data?.data!!, Build.VERSION.SDK_INT)
            }
        }
    }

    private fun requestPermissionForReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions(
                (this), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                11
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun checkPermission2(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result =
                this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    override fun OnItemClick(position: Int) {
        filePath1.remove(filePath1[position])
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, filePath1.size)
    }

    override fun PickiTonUriReturned() {
    }

    override fun PickiTonStartListener() {

    }

    override fun PickiTonProgressUpdate(progress: Int) {

    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        val path1 = path.toString()
        filePath1.add(path1)
        binding.mailRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Mail_Send_Adapter(filePath1, this)
        binding.mailRecyclerView.adapter = adapter
        Log.d("sadhna", filePath1.toString())
    }

    override fun PickiTonMultipleCompleteListener(
        paths: java.util.ArrayList<String>?,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        if (paths != null) {
            for (i in 0 until paths.size){
                filePath1.add(paths[i])
                binding.mailRecyclerView.layoutManager = LinearLayoutManager(this)
                adapter = Mail_Send_Adapter(filePath1,this)
                binding.mailRecyclerView.adapter = adapter
                Log.d("sadhna1",filePath1.toString())


            }
        }
    }
}
