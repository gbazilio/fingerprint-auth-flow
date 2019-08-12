package com.example.authflowexample.data

import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)
class FingerprintCryptoObject28(
    private val cryptoObject: BiometricPrompt.CryptoObject
) : FingerprintCryptoObject {

    override fun encrypt(message: String): ByteArray {
        val cipher = cryptoObject.cipher ?: throw Exception("Cipher not found")

        val cryptoOperation = CryptoOperation(cipher)
        return cryptoOperation.encrypt(message)
    }

    override fun decrypt(cipheredPassword: ByteArray): String {
        val cipher = cryptoObject.cipher ?: throw Exception("Cipher not found")

        val cryptoOperation = CryptoOperation(cipher)
        return cryptoOperation.decrypt(cipheredPassword)
    }

}
