package com.example.authflowexample.data

import androidx.core.hardware.fingerprint.FingerprintManagerCompat

class FingerprintCryptoObject23(
    private val cryptoObject: FingerprintManagerCompat.CryptoObject
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
