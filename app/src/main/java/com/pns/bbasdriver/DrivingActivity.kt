package com.pns.bbasdriver

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
import java.net.URLDecoder

class DrivingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrivingBinding
    private lateinit var busRouteName: String
    private lateinit var cityCode: String
    private lateinit var busRouteId: String
    private lateinit var vehicleNo: String
    private lateinit var busRoutes: List<BusRoute>
    private var index: Int = 0
    private val TAG = "Driving"
    private var isDriving = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrivingBinding.inflate(layoutInflater)

        CoroutineScope(Dispatchers.Default).launch {
            busRouteName = DataStoreApplication.getInstance().getDataStore().mBusRouteName.first()
            cityCode = DataStoreApplication.getInstance().getDataStore().mCityCode.first()
            busRouteId = DataStoreApplication.getInstance().getDataStore().mRouteId.first()
            vehicleNo = DataStoreApplication.getInstance().getDataStore().mVehicleNo.first()
            binding.bus = busRouteName
            requestBusAPI()
        }

        CoroutineScope(Dispatchers.Main).launch {
            while(isDriving){
                requestBusLocation()
                delay(15000)
            }
        }

        binding.groupTxtBusStop.setHeight(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                24 * 4f,
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

    private fun requestBusAPI() {
        val busRouteAPI = BusRetrofitClient.mInstance.getRoute(
            URLDecoder.decode(resources.getString(R.string.bus_key), "UTF-8"),
            cityCode,
            busRouteId,
            100,
            "json"
        )

         Runnable {
            busRouteAPI.enqueue(object : retrofit2.Callback<BusBaseResponseModel<List<BusRoute>>> {
                override fun onResponse(
                    call: Call<BusBaseResponseModel<List<BusRoute>>>,
                    response: Response<BusBaseResponseModel<List<BusRoute>>>
                ) {
                    Log.d(TAG, response.toString())
                    Log.d(TAG, response.body()?.response?.body?.items?.result.toString())
                    busRoutes = response.body()?.response?.body?.items?.result!!
                    settingBusStop()
                }

                override fun onFailure(call: Call<BusBaseResponseModel<List<BusRoute>>>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }
            })
        }.run()
    }

    private fun requestBusLocation() {
        val busLocationAPI = BusRetrofitClient.mInstance.getLocation(
            URLDecoder.decode(resources.getString(R.string.bus_key), "UTF-8"),
            cityCode,
            busRouteId,
            100,
            "json"
        )

        Runnable {
            busLocationAPI.enqueue(object : retrofit2.Callback<BusBaseResponseModel<List<BusLocation>>> {
                override fun onResponse(
                    call: Call<BusBaseResponseModel<List<BusLocation>>>,
                    response: Response<BusBaseResponseModel<List<BusLocation>>>
                ) {
                    Log.d(TAG, response.toString())
                    Log.d(TAG, response.body()?.response?.body?.items?.result.toString())
                    val locations = response.body()?.response?.body?.items?.result!!
                    if (locations.any { it.vehicleno.contains(vehicleNo) }){
                        index = locations.filter { it.vehicleno.contains(vehicleNo) }[0].nodeord - 1
                        settingBusStop()
                    }
                }
                override fun onFailure(call: Call<BusBaseResponseModel<List<BusLocation>>>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }
            })
        }.run()
    }

    private fun settingBusStop(){
        when (index) {
            busRoutes.size - 1 -> {
                isDriving = false
                binding.busStop1 = BusStop(busRoutes[index].nodeid, busRoutes[index].nodeName, false)
                binding.busStop2 = null
                binding.busStop3 = null
                binding.busStop4 = null
                createEndDialog()
            }
            busRoutes.size - 2 -> {
                binding.busStop1 = BusStop(busRoutes[index].nodeid, busRoutes[index].nodeName, false)
                binding.busStop2 = BusStop(busRoutes[index + 1].nodeid, busRoutes[index + 1].nodeName, false)
                binding.busStop3 = null
                binding.busStop4 = null
            }
            busRoutes.size - 3 -> {
                binding.busStop1 = BusStop(busRoutes[index].nodeid, busRoutes[index].nodeName, false)
                binding.busStop2 = BusStop(busRoutes[index + 1].nodeid, busRoutes[index + 1].nodeName, false)
                binding.busStop3 = BusStop(busRoutes[index + 2].nodeid, busRoutes[index + 2].nodeName, false)
                binding.busStop4 = null
            }
            else -> {
                binding.busStop1 = BusStop(busRoutes[index].nodeid, busRoutes[index].nodeName, false)
                binding.busStop2 = BusStop(busRoutes[index + 1].nodeid, busRoutes[index + 1].nodeName, false)
                binding.busStop3 = BusStop(busRoutes[index + 2].nodeid, busRoutes[index + 2].nodeName, false)
                binding.busStop4 = BusStop(busRoutes[index + 3].nodeid, busRoutes[index + 3].nodeName, false)
            }
        }

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
        binding.isRiding = true
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
                startActivity(Intent(applicationContext, MainActivity::class.java))
                dialog.dismiss()
            }
            .show()
    }
}