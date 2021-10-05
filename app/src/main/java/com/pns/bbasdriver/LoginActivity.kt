package com.pns.bbasdriver

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

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val startForSignInResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val uid = account.id
                val name = account.displayName?.toString()
                CoroutineScope(Dispatchers.Main).launch {
                    if (name != null) {
                        DataStoreApplication.getInstance().getDataStore().setUserID(name)
                    }
                }
                startActivity(Intent(this, MainActivity::class.java))
            } catch (e: ApiException) {
                Log.e("Google login error", e.toString())
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
}