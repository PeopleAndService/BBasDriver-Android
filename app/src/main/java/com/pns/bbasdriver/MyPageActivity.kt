package com.pns.bbasdriver

import RetrofitClient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pns.bbasdriver.databinding.ActivityMyPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageBinding
    private val TAG = "MyPage"
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)

        CoroutineScope(Dispatchers.IO).launch {
            userId = DataStoreApplication.getInstance().getDataStore().mUserID.first()
        }
        binding.btnOpenSource.setOnClickListener { startActivity(Intent(applicationContext, OssLicensesMenuActivity::class.java)) }
        binding.btnApiSource.setOnClickListener { createApiSource() }
        binding.btnLogout.setOnClickListener { createLogoutDialog() }
        binding.btnWithdraw.setOnClickListener { createWithdrawDialog() }
        binding.btnBack.setOnClickListener { finish() }
        setContentView(binding.root)
    }

    private fun createLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.logout_title))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                deleteDataStore()
                navigateToLoginActivity()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createWithdrawDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.withdraw_title))
            .setPositiveButton(resources.getString(R.string.btn_confirm)) { dialog, _ ->
                deleteGoogleUser()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createErrorDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.error_title))
            .setMessage(getString(R.string.error_message))
            .setPositiveButton(getString(R.string.btn_confirm)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun createApiSource() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.my_api_source))
            .setMessage(getString(R.string.api_source_msg))
            .setPositiveButton(getString(R.string.btn_close)){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteDataStore() {
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreApplication.getInstance().getDataStore().delete()
        }
    }

    private fun deleteGoogleUser() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        googleSignInClient.revokeAccess()
            .addOnSuccessListener {
                deleteBBasUser()
            }
            .addOnFailureListener {
                createErrorDialog()
            }
            .addOnCompleteListener {
            }
    }

    private fun deleteBBasUser() {
        val deleteAPI = RetrofitClient.mInstance.withdraw(UserIdRequestBody(userId))
        Runnable {
            deleteAPI.enqueue(object : retrofit2.Callback<UserBaseResponseModel<JSONObject>> {
                override fun onResponse(
                    call: Call<UserBaseResponseModel<JSONObject>>,
                    response: Response<UserBaseResponseModel<JSONObject>>
                ) {
                    Log.d(TAG, response.toString())
                    if (response.body()?.success == true) {
                        deleteDataStore()
                        navigateToLoginActivity()
                    } else {
                        createErrorDialog()
                        Log.e(TAG, "Server Login Error")
                    }
                }

                override fun onFailure(call: Call<UserBaseResponseModel<JSONObject>>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }
            })
        }.run()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this@MyPageActivity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}