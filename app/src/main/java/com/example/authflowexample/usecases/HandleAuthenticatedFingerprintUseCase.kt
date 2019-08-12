package com.example.authflowexample.usecases

import com.example.authflowexample.data.FingerprintCryptoObject
import com.example.authflowexample.data.Storage

class HandleAuthenticatedFingerprintUseCase(
    private val storage: Storage
) {

    fun execute(
        username: String,
        password: String,
        fingerprintCryptoObject: FingerprintCryptoObject
    ): Result {
        val storedUsername = storage.getUsername()
        val storedCipheredPassword = storage.getPassword()

        if (storedUsername == null || storedCipheredPassword == null) {
            val cipheredPassword = fingerprintCryptoObject.encrypt(password)
            storage.saveCredentials(username, cipheredPassword)
            return Result.LoginWithFingerprintSuccessful(username)
        }

        return try {
            fingerprintCryptoObject.decrypt(storedCipheredPassword)
            Result.LoginWithFingerprintSuccessful(storedUsername)
        } catch (ex: Exception) {
            Result.LoginWithFingerprintFailed
        }
    }

}
