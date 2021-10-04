package com.pns.bbasdriver

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.bbasdriver.databinding.ActivityMainBinding
import java.nio.charset.Charset
import java.util.Arrays

class MainActivity : AppCompatActivity() {
    private lateinit var nfcPendingIntent: PendingIntent
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
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

    fun showMsg(mMessage: NdefMessage) {
        val recs = mMessage.records
        for (i in recs.indices) {
            val record = recs[i]
            if (Arrays.equals(record.type, NdefRecord.RTD_TEXT)) {
                val decodes = String(record.payload, Charset.forName("UTF-8")).split("_")
                if (decodes[1] == "ROUTENM") createDrivingDialog(decodes[2].toInt())
                else if (decodes[1] == "VEHICLENO") Log.d("차량 번호", decodes[2])
            }
        }
    }

    private fun createDrivingDialog(busNumber: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.driving_title))
            .setMessage(resources.getString(R.string.driving_msg, busNumber))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}