package com.pns.bbasdriver

import RetrofitClient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.bbasdriver.databinding.ActivityDrivingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class DrivingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrivingBinding
    private lateinit var busRouteName: String
    private lateinit var cityCode: String
    private lateinit var busRouteId: String
    private lateinit var vehicleNo: String
    private lateinit var userId: String
    private var lastBusStop1: String = ""
    private var lastBusStop2: String = ""
    private var isWaitDialog = false
    private var isRidingDialog = false
    private val TAG = "Driving"
    private var isDriving = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrivingBinding.inflate(layoutInflater)

        CoroutineScope(Dispatchers.Main).launch {
            busRouteName = DataStoreApplication.getInstance().getDataStore().mBusRouteName.first()
            cityCode = DataStoreApplication.getInstance().getDataStore().mCityCode.first()
            busRouteId = DataStoreApplication.getInstance().getDataStore().mRouteId.first()
            vehicleNo = DataStoreApplication.getInstance().getDataStore().mVehicleNo.first()
            userId = DataStoreApplication.getInstance().getDataStore().mUserID.first()
            binding.bus = busRouteName
        }

        CoroutineScope(Dispatchers.Main).launch {
            while (isDriving) {
                requestBusLocation()
                delay(15000)
            }
            if (!isDriving) createEndDialog()
        }

        binding.groupTxtBusStop.setHeight(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                24 * 4.5f,
                this.resources.displayMetrics
            ).toInt()
        )

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(binding.root)
    }

    private fun View.setHeight(value: Int) {
        val lp = layoutParams
        lp?.let {
            lp.height = value
            layoutParams = lp
        }
    }

    private fun requestBusLocation() {
        val busAPI = RetrofitClient.mInstance.getInfo(BusRequestBody(cityCode, busRouteId, vehicleNo))
        Runnable {
            busAPI.enqueue(object : retrofit2.Callback<BusBaseResponseModel> {
                override fun onResponse(
                    call: Call<BusBaseResponseModel>,
                    response: Response<BusBaseResponseModel>
                ) {
                    Log.d(TAG, response.toString())
                    Log.d(TAG, response.body().toString())
                    Log.d(TAG, response.body()?.result.toString())
                    if (response.isSuccessful){
                        if ((response.code() != 404) and (response.body()?.result?.size != 0)) {
                            settingBusStop(response.body()?.result!!)
                            if (response.body()?.message?.recentResult != null) settingNotice(response.body()?.message?.recentResult!!)
                            if (!isRidingDialog and (response.body()?.boardingStatus == true)) {
                                createRindingDialog()
                                lastBusStop1 = response.body()?.result!![0].stationName
                                isRidingDialog = true
                                binding.isRiding = true
                            }
                            if (isRidingDialog and (response.body()?.result!![0].stationName != lastBusStop1)) {
                                binding.isRiding = false
                                isRidingDialog = false
                            }
                        }
                    } else {
                        Log.e(TAG, "request error")
                    }

                }

                override fun onFailure(call: Call<BusBaseResponseModel>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }
            })
        }.run()
    }

    private fun settingBusStop(busStops: List<BusStop>) {
        when (busStops.size) {
            1 -> {
                binding.busStop1 = busStops[0]
                binding.busStop2 = null
                binding.busStop3 = null
                binding.busStop4 = null
                isDriving = false
            }
            2 -> {
                binding.busStop1 = busStops[0]
                binding.busStop2 = busStops[1]
                binding.busStop3 = null
                binding.busStop4 = null
            }
            3 -> {
                binding.busStop1 = busStops[0]
                binding.busStop2 = busStops[1]
                binding.busStop3 = busStops[2]
                binding.busStop4 = null
            }
            4 -> {
                binding.busStop1 = busStops[0]
                binding.busStop2 = busStops[1]
                binding.busStop3 = busStops[2]
                binding.busStop4 = busStops[3]
            }
        }
        if (busStops.size > 1) {
            if (!isWaitDialog and busStops[1].waiting) {
                createWaitDialog()
                lastBusStop2 = busStops[1].stationName
                isWaitDialog = true
            }
            if (isWaitDialog and (busStops[1].stationName != lastBusStop2)) {
                isWaitDialog = false
            }
        }
    }

    private fun settingNotice(notice: Notice) {
        binding.noticeBusStop = notice.stationName
        binding.noticeTime = notice.queueTime.substring(11, 19)
    }

    private fun requestDriving() {
        val attendanceAPI = RetrofitClient.mInstance.attendance(AttendanceRequestBody(userId, "null", "null"))
        Runnable {
            attendanceAPI.enqueue(object : retrofit2.Callback<UserBaseResponseModel<User>> {
                override fun onResponse(
                    call: Call<UserBaseResponseModel<User>>,
                    response: Response<UserBaseResponseModel<User>>
                ) {
                    Log.d(TAG, response.toString())
                    Log.d(TAG, response.body().toString())
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }

                override fun onFailure(call: Call<UserBaseResponseModel<User>>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }
            })
        }.run()
    }

    private fun createWaitDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.wait_title))
            .setMessage(resources.getString(R.string.wait_msg))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createRindingDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.riding_tile))
            .setMessage(resources.getString(R.string.riding_msg))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createEndDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.driving_finish_title))
            .setMessage(resources.getString(R.string.driving_finish_msg, busRouteName))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                requestDriving()
                dialog.dismiss()
            }
            .show()
    }
}