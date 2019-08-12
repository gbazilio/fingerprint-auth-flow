package com.example.authflowexample.features.auth.login

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.authflowexample.R
import com.example.authflowexample.data.FingerprintCryptoObject
import com.example.authflowexample.features.auth.AuthViewModel
import com.example.authflowexample.features.auth.ViewState
import com.example.authflowexample.features.auth.fingerprint.AuthenticationCallback28
import com.example.authflowexample.features.auth.fingerprint.AuthenticationListener
import com.example.authflowexample.features.auth.fingerprint.FingerprintFragment
import com.example.authflowexample.features.money.MoneyTransferActivity
import com.example.authflowexample.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import javax.crypto.Cipher


class MainActivity : AppCompatActivity(), AuthenticationListener {

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authViewModel.changes.observe(this, Observer { viewState ->
            when (viewState) {
                ViewState.AuthMethodFallback -> keepItPasswordBased()
                is ViewState.AuthMethodFingerprint ->
                    @RequiresApi(Build.VERSION_CODES.M) {
                        showFingerprintScreen()
                    }
                is ViewState.AuthMethodBiometric ->
                    @SuppressLint("NewApi") {
                        showBiometricPrompt(viewState.protectedCipher)
                    }
                is ViewState.LoginSuccessful -> {
                    navigateToTransferScreen()
                }
                is ViewState.TamperedFingerprintCatalog -> {
                    toast("Tampered fingerprint catalog")
                }
            }
        })

        authViewModel.autoRequestAuthenticationIfFingerprintIsLinked()

        buttonLogin.setOnClickListener {
            requestAuthentication()
        }
    }

    private fun requestAuthentication() {
        val username = editTextUsername.text.toString()
        val password = editTextPassword.text.toString()
        val registerForFingerprint = switchFingerprint.isChecked
        authViewModel.requestAuthentication(username, password, registerForFingerprint)
    }

    private fun navigateToTransferScreen() {
        val intent = Intent(this, MoneyTransferActivity::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showFingerprintScreen() {
        FingerprintFragment().show(supportFragmentManager, AUTH_DIALOG_TAG)
    }

    private fun keepItPasswordBased() {
        toast("Fingerprint not supported")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showBiometricPrompt(protectedCipher: Cipher) {
        val promptBuilder = BiometricPrompt.Builder(this)
            .setTitle("Login Auth")
            .setSubtitle("Fingerprint Authentication")
            .setDescription("Touch to requestAuthentication")
            .setNegativeButton("Cancel", this.mainExecutor,
                DialogInterface.OnClickListener { _, _ ->
                    showCancellationToast()
                })

        val biometricPrompt = promptBuilder.build()

        val cryptoObject = BiometricPrompt.CryptoObject(protectedCipher)
        val callback = AuthenticationCallback28(this)

        biometricPrompt.authenticate(
            cryptoObject,
            CancellationSignal(),
            this.mainExecutor,
            callback
        )
    }

    override fun onSuccess(fingerprintCryptoObject: FingerprintCryptoObject) {
        authViewModel.notifyFingerprintAuthenticated(fingerprintCryptoObject)
    }

    override fun onFailure(errorMessage: String) {}

    private fun showCancellationToast() {
        toast("Authentication cancelled!")
    }

    companion object {
        private const val AUTH_DIALOG_TAG = "AuthDialog"
    }
}
