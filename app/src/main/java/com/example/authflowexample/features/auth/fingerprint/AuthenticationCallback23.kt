package com.example.authflowexample.features.auth.fingerprint

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.example.authflowexample.data.FingerprintCryptoObject23

@RequiresApi(Build.VERSION_CODES.M)
class AuthenticationCallback23(
    private val listener: AuthenticationListener
) :
    FingerprintManagerCompat.AuthenticationCallback() {

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

    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)

        result.cryptoObject.cipher?.let {
            try {
                listener.onSuccess(FingerprintCryptoObject23(result.cryptoObject))
            } catch (ex: Exception) {
                ex.printStackTrace()
                listener.onFailure(ex.message ?: "")
            }
        } ?: throw Exception("No cipher found!")

    }
}
