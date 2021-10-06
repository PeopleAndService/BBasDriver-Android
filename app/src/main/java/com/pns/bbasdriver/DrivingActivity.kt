package com.pns.bbasdriver

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.bbasdriver.databinding.ActivityDrivingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DrivingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrivingBinding
    private lateinit var busRouteId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrivingBinding.inflate(layoutInflater)
        CoroutineScope(Dispatchers.Main).launch {
            busRouteId = DataStoreApplication.getInstance().getDataStore().mBusRouteId.first()
            binding.bus = busRouteId
        }

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

    private fun createEndDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.driving_finish_title))
            .setMessage(resources.getString(R.string.driving_finish_msg, busRouteId))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}