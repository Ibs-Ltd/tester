package com.ibs.hrmioneemployee.activities
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ibs.hrmioneemployee.adapters.NotificationsRecyclerAdapter
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.databinding.ActivityNotificationBinding
import com.ibs.hrmioneemployee.models.api_models.notification.DeleteAllNotificationResponse
import com.ibs.hrmioneemployee.models.api_models.notification.DeleteSingleNotificationResponse
import com.ibs.hrmioneemployee.models.api_models.notification.NotificationListResponse
import com.ibs.hrmioneemployee.models.api_models.notification.Result
import com.ibs.hrmioneemployee.retrofit.ApiServices
import com.ibs.hrmioneemployee.retrofit.RetrofitClient
import com.ibs.hrmioneemployee.utilities.DataLoading
import com.ibs.hrmioneemployee.utilities.InternetConnection
import com.ibs.hrmioneemployee.utilities.SharedPreferenceClass
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notificationList: ArrayList<Result>
    private lateinit var notificationAdapter: NotificationsRecyclerAdapter
    private lateinit var dataLoading: DataLoading
    private lateinit var sp: SharedPreferences
    private lateinit var sharedPreferenceClass: SharedPreferenceClass
    private lateinit var Authorization: String
    private var userId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.llBack.setOnClickListener {
            onBackPressed()
        }

        binding.clearAllText.setOnClickListener {
            if (InternetConnection.checkConnection(this)){
                callDeleteAllNotificationApi()
            }
            else{
                Toast.makeText(this, "You're offline", Toast.LENGTH_SHORT).show()
            }
        }

        notificationList = ArrayList()

        dataLoading = DataLoading(this)
        sharedPreferenceClass = SharedPreferenceClass(this)
        Authorization = sharedPreferenceClass.getLoginToken()

        sp = this.getSharedPreferences(SharedPreferenceClass.SHARED_PREF_NAME, MODE_PRIVATE)
        userId = sp.getInt("UserId", -1)

        if (InternetConnection.checkConnection(this)){
            callNotificationListApi()
        }
        else{
            Toast.makeText(this, "You're offline", Toast.LENGTH_SHORT).show()
            binding.noNotification.visibility = View.VISIBLE
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    // this method is called
                    // when the item is moved.
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    // below line is to get the position
                    // of the item at that position.
                    val position = viewHolder.adapterPosition

                    // this method is called when item is swiped.
                    // below line is to remove item from our array list.
//                    notificationList.removeAt(position)

                    // below line is to notify our item is removed from adapter.
//                    notificationAdapter.notifyItemRemoved(position)

//                    Toast.makeText(this@NotificationActivity, ""+position, Toast.LENGTH_SHORT).show()

                    if (InternetConnection.checkConnection(this@NotificationActivity)){
                        callDeleteSingleNotificationApi(position)
                    }
                    else{
//                        notificationList.add(position, notificationList[position])
//                        notificationAdapter.notifyItemInserted(position)
                        Toast.makeText(this@NotificationActivity, "You're offline", Toast.LENGTH_SHORT).show()
                    }
//
//                    // below line is to display our snackbar with action.
//                    val snackbar = Snackbar.make(binding.notificationRecyclerView, "${deletedCourse.title} Deleted", Snackbar.LENGTH_LONG)
//
//                    snackbar.setAction("UNDO", View.OnClickListener {
//                        // adding on click listener to our action of snack bar.
//                        // below line is to add our item to array list with a position.
//                        notificationList.add(position, deletedCourse)
//
//                        // below line is to notify item is
//                        // added to our adapter class.
//                        notificationAdapter.notifyItemInserted(position)
//                    })
//                    snackbar.show()
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                    RecyclerViewSwipeDecorator.Builder(this@NotificationActivity, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@NotificationActivity, R.color.delete_background))
                        .addSwipeLeftActionIcon(R.drawable.delete)
                        .addCornerRadius(1,15)
//                        .addBackgroundColor(ContextCompat.getColor(this@NotificationActivity, R.color.black))
//                        .addActionIcon(R.drawable.email)
                        .create()
                        .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            })

        // at last we are adding this
        // to our recycler view.

        itemTouchHelper.attachToRecyclerView(binding.notificationRecyclerView)
    }

    private fun callDeleteAllNotificationApi() {
        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<DeleteAllNotificationResponse> = apiServices.deleteAllNotification(Authorization, userId)

        call.enqueue(object : Callback<DeleteAllNotificationResponse>{
            override fun onResponse(
                call: Call<DeleteAllNotificationResponse>,
                response: Response<DeleteAllNotificationResponse>) {

                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.code == 200){
                        binding.notificationRecyclerView.visibility = View.INVISIBLE
                        binding.noNotification.visibility = View.VISIBLE
                        binding.clearAllText.visibility = View.GONE
                        Toast.makeText(this@NotificationActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@NotificationActivity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<DeleteAllNotificationResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@NotificationActivity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callDeleteSingleNotificationApi(position: Int) {

//        Toast.makeText(this, ""+(notificationList.size+1), Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, ""+(notificationList[position].id), Toast.LENGTH_SHORT).show()

        dataLoading.startLoading()
//
        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<DeleteSingleNotificationResponse> = apiServices.deleteSingleNotification(Authorization, notificationList[position].id)

        call.enqueue(object : Callback<DeleteSingleNotificationResponse>{ override fun onResponse(call: Call<DeleteSingleNotificationResponse>,
                response: Response<DeleteSingleNotificationResponse>) {

            if (response.isSuccessful && response.body() != null){
                if (response.body()!!.code == 200){
                    notificationList.remove(notificationList[position])
                    notificationAdapter.notifyItemRemoved(position)
                    notificationAdapter.notifyItemRangeChanged(position, notificationList.size)
//                    notificationAdapter = NotificationsRecyclerAdapter(this@NotificationActivity, notificationList)
                    Toast.makeText(this@NotificationActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    if (notificationList.isEmpty()){
                        binding.noNotification.visibility = View.VISIBLE
                        binding.clearAllText.visibility = View.GONE
                        Toast.makeText(this@NotificationActivity, "You're all caught up", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                val jObjError = JSONObject(response.errorBody()!!.string())
                Toast.makeText(this@NotificationActivity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
            }
            dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<DeleteSingleNotificationResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@NotificationActivity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callNotificationListApi() {
        dataLoading.startLoading()

        val apiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
        val call: Call<NotificationListResponse> = apiServices.notificationList(Authorization, userId)

        call.enqueue(object: Callback<NotificationListResponse>{
            override fun onResponse(call: Call<NotificationListResponse>, response: Response<NotificationListResponse>) {

                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.code == 200){
                        notificationList = response.body()!!.result
                        if (response.body()!!.result != null){
                            Toast.makeText(this@NotificationActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                            binding.notificationRecyclerView.apply {
                                layoutManager = LinearLayoutManager(this@NotificationActivity)
                                notificationAdapter = NotificationsRecyclerAdapter(this@NotificationActivity, notificationList)
                                adapter = notificationAdapter
                                binding.clearAllText.visibility = View.VISIBLE
                            }
                        }
                        else{
                            binding.clearAllText.visibility = View.GONE
                            binding.notificationRecyclerView.visibility = View.INVISIBLE
                            binding.noNotification.visibility = View.VISIBLE
                            Toast.makeText(this@NotificationActivity, "You're all caught up", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@NotificationActivity, jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                }
                dataLoading.hideLoading()
            }

            override fun onFailure(call: Call<NotificationListResponse>, t: Throwable) {
                dataLoading.hideLoading()
                Toast.makeText(this@NotificationActivity, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }
}