package com.example.authflowexample.data

interface FingerprintCryptoObject {
    fun encrypt(message: String): ByteArray
    fun decrypt(cipheredPassword: ByteArray): String
}
