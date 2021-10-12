package com.pns.bbasdriver

import RetrofitClient
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.bbasdriver.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.nio.charset.Charset
import java.util.Arrays

class MainActivity : AppCompatActivity() {
    private lateinit var nfcPendingIntent: PendingIntent
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var driverId: String
    private lateinit var cityCode: String
    private lateinit var routeId: String

    private val TAG = "Main"

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (!nfcAdapter.isEnabled) createOnNFCDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        CoroutineScope(Dispatchers.Main).launch {
            binding.username = DataStoreApplication.getInstance().getDataStore().mUserName.first()
            driverId = DataStoreApplication.getInstance().getDataStore().mUserID.first()
        }
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (!nfcAdapter.isEnabled) createOnNFCDialog()
        nfcPendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )

        binding.ibtnMyPage.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) ?: return
            for (i in messages.indices) showMsg(messages[i] as NdefMessage)
        }
    }

    private fun showMsg(mMessage: NdefMessage) {
        val recs = mMessage.records
        var vehicleNo = ""
        var routeNm = ""

        for (i in recs.indices) {
            val record = recs[i]
            if (Arrays.equals(record.type, NdefRecord.RTD_TEXT)) {
                val decodes = String(record.payload, Charset.forName("UTF-8")).split("_")
                when (decodes[1]) {
                    "ROUTENM" -> routeNm = decodes[2]
                    "VEHICLENO" -> vehicleNo = decodes[2]
                    "CITYCODE" -> cityCode = decodes[2]
                    "ROUTEID" -> routeId = decodes[2]
                }
            }
        }

        if ((cityCode != "") and (routeNm != "") and (routeId != "") and (vehicleNo != "")) {
            CoroutineScope(Dispatchers.IO).launch {
                DataStoreApplication.getInstance().getDataStore().setBusRouteName(routeNm)
                DataStoreApplication.getInstance().getDataStore().setVihicleNo(vehicleNo)
                DataStoreApplication.getInstance().getDataStore().setCityCode(cityCode)
                DataStoreApplication.getInstance().getDataStore().setRouteId(routeId)
            }
            createDrivingDialog(vehicleNo, routeNm)
        }
    }

    private fun requestDriving(vehicleId: String, busRouteId: String) {
        val attendanceAPI = RetrofitClient.mInstance.attendance(AttendanceRequestBody(driverId, vehicleId, busRouteId))
        Runnable {
            attendanceAPI.enqueue(object : retrofit2.Callback<UserBaseResponseModel<User>> {
                override fun onResponse(
                    call: Call<UserBaseResponseModel<User>>,
                    response: Response<UserBaseResponseModel<User>>
                ) {
                    Log.d(TAG, response.toString())
                    Log.d(TAG, response.body().toString())
                    if (response.body()?.success == true) {
                        startActivity(Intent(applicationContext, DrivingActivity::class.java))
                        finish()
                    } else {
                        Log.e(TAG, "Server Request Error")
                    }
                }

                override fun onFailure(call: Call<UserBaseResponseModel<User>>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }
            })
        }.run()
    }

    private fun createDrivingDialog(vehicleId: String, busRouteId: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.driving_title))
            .setMessage(resources.getString(R.string.driving_msg, busRouteId))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                requestDriving(vehicleId, busRouteId)
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createOnNFCDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.nfc_title))
            .setMessage(resources.getString(R.string.nfc_msg))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                resultLauncher.launch(intent)
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}