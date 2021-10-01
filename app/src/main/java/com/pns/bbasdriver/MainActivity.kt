package com.pns.bbasdriver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.bbasdriver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private fun createDrivingDialog(busNumber: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.driving_title))
            .setMessage(resources.getString(R.string.driving_msg, busNumber))
            .setPositiveButton(resources.getString(R.string.btn_confirm)){ dialog, _ ->
                // TODO: 주행 화면으로 전환
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.btn_cancel)){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}