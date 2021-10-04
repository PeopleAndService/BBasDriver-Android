package com.pns.bbasdriver

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
import java.nio.charset.Charset
import java.util.Arrays

class MainActivity : AppCompatActivity() {
    private lateinit var nfcPendingIntent: PendingIntent
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var driverId: String

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (!nfcAdapter.isEnabled) createOnNFCDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        driverId = "test 용 아이디"
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (!nfcAdapter.isEnabled) createOnNFCDialog()
        nfcPendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )

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
            Log.d("messages", messages.toString())
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
                if (decodes[1] == "ROUTENM") routeNm = decodes[2]
                else if (decodes[1] == "VEHICLENO") vehicleNo = decodes[2]
            }
        }

        if ((vehicleNo != "") and (routeNm != "")) createDrivingDialog(Bus(driverId, routeNm, vehicleNo))
    }

    private fun createDrivingDialog(bus: Bus) {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.driving_title))
            .setMessage(resources.getString(R.string.driving_msg, bus.routeNm))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                val intent = Intent(this, DrivingActivity::class.java)
                intent.putExtra("bus", bus)
                startActivity(intent)
                dialog.dismiss()
                finish()
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