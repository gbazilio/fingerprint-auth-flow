package com.example.authflowexample.usecases

import android.os.Build
import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.annotation.RequiresApi
import com.example.authflowexample.data.CryptoKeyManager
import com.example.authflowexample.data.FingerprintCatalogHack
import com.example.authflowexample.data.Storage
import com.example.authflowexample.data.SystemInfo
import javax.crypto.Cipher


sealed class Result {
    object LoginWithFingerprintFailed : Result()
    object TamperedFingerprintCatalog : Result()

    data class AuthMethodBiometric(val cipher: Cipher) : Result()
    data class AuthMethodFingerprint(val cipher: Cipher) : Result()
    data class LoginWithFingerprintSuccessful(val username: String) : Result()

}

@RequiresApi(Build.VERSION_CODES.M)
class SelectAuthenticationMethodUseCase(
    private val systemInfo: SystemInfo,
    private val storage: Storage,
    private val cryptoKeyManager: CryptoKeyManager,
    private val fingerprintCatalogHack: FingerprintCatalogHack
) {

    fun execute(): Result {
        val lastEncryptionIV = storage.getIV()

        val cipher = if (lastEncryptionIV != null && lastEncryptionIV.isNotEmpty()) {
            try {
                cryptoKeyManager.retrieveExistingKey(lastEncryptionIV)
            } catch (ex: KeyPermanentlyInvalidatedException) {
                ex.printStackTrace()
                return Result.TamperedFingerprintCatalog
            }
        } else {
            val cipher = cryptoKeyManager.generateNewKey()
            val iv = cipher.iv
            storage.saveIV(iv)

            val allIdsAtOnce = fingerprintCatalogHack.getEnrolledFingerprintIds()
            storage.saveFingerprintCatalog(allIdsAtOnce)

            cipher
        }

        val previousFingerprintCatalog = storage.getFingerprintCatalog()
        val currentFingerprintCatalog = fingerprintCatalogHack.getEnrolledFingerprintIds()
        if (previousFingerprintCatalog != currentFingerprintCatalog) return Result.TamperedFingerprintCatalog

        return when (systemInfo.getCurrentAndroidVersion()) {
            SystemInfo.API.P_HIGHER -> Result.AuthMethodBiometric(cipher)
            else -> Result.AuthMethodFingerprint(cipher)
        }
    }

}
