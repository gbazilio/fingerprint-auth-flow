package com.example.authflowexample.data

import javax.crypto.Cipher

class CryptoOperation(val cipher: Cipher) {

    fun encrypt(message: String): ByteArray {
        try {
            return cipher.doFinal(message.toByteArray())
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    fun decrypt(cipheredPassword: ByteArray): String {
        try {
            val plaintextBytes = cipher.doFinal(cipheredPassword)
            return String(plaintextBytes)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

}
