package com.example.authflowexample.features.auth.fingerprint

import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.authflowexample.data.FingerprintCryptoObject28

@RequiresApi(Build.VERSION_CODES.P)
class AuthenticationCallback28(private val listener: AuthenticationListener) :
    BiometricPrompt.AuthenticationCallback() {

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        listener.onFailure(errString.toString())
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        listener.onFailure("Invalid fingerprint")
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
        super.onAuthenticationHelp(helpCode, helpString)
        listener.onFailure(helpString.toString())
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)

        result.cryptoObject.cipher?.let {
            try {
                listener.onSuccess(FingerprintCryptoObject28(result.cryptoObject))
            } catch (ex: Exception) {
                ex.printStackTrace()
                listener.onFailure(ex.message ?: "")
            }
        } ?: throw Exception("No cipher found!")
    }
}
