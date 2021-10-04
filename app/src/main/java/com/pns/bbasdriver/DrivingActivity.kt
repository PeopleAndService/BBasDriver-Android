package com.pns.bbasdriver

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.bbasdriver.databinding.ActivityDrivingBinding

class DrivingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrivingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrivingBinding.inflate(layoutInflater)
        binding.bus = intent.getSerializableExtra("bus") as Bus

        binding.groupTxtBusStop.setHeight(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                24 * 4f,
                this.resources.displayMetrics
            ).toInt()
        )
        setContentView(binding.root)
    }

    private fun View.setHeight(value: Int) {
        val lp = layoutParams
        lp?.let {
            lp.height = value
            layoutParams = lp
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

    private fun createEndDialog(bus: Bus) {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.driving_finish_title))
            .setMessage(resources.getString(R.string.driving_finish_msg, bus.routeNm))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}