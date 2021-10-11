package com.pns.bbasdriver

import RetrofitClient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.pns.bbasdriver.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val TAG = "Login"

    private val startForSignInResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val uid = account.id?.toString()
                val name = account.displayName?.toString()
                if (name != null) {
                    if (uid != null) {
                        requestSingUp(uid, name)
                    }
                }
            } catch (e: ApiException) {
                Log.e(TAG, e.toString())
                e.printStackTrace()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            googleSignIn()
        }
    }

    private fun googleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        val googleSignInIntent = mGoogleSignInClient.signInIntent
        startForSignInResult.launch(googleSignInIntent)
    }

    private fun requestSingUp(id: String, name: String) {
        val signUpAPI = RetrofitClient.mInstance.sign(UserRequestBody(id, name))
        Runnable {
            signUpAPI.enqueue(object : retrofit2.Callback<UserBaseResponseModel<User>> {
                override fun onResponse(
                    call: Call<UserBaseResponseModel<User>>,
                    response: Response<UserBaseResponseModel<User>>
                ) {
                    if (response.body()?.success == true) {
                        Log.d(TAG, response.body().toString())
                        CoroutineScope(Dispatchers.IO).launch {
                            DataStoreApplication.getInstance().getDataStore().setUserID(response.body()!!.result.userId)
                            DataStoreApplication.getInstance().getDataStore()
                                .setUserName(response.body()!!.result.userName)
                        }
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    } else {
                        Log.e(TAG, "Server Login Error")
                    }
                }

                override fun onFailure(call: Call<UserBaseResponseModel<User>>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }
            })
        }.run()
    }
}