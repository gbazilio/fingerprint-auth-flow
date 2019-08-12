package com.example.authflowexample.data

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

@RequiresApi(Build.VERSION_CODES.M)
class CryptoKeyManager {

    private val keystore: KeyStore

    init {
        keystore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keystore.load(null)
    }

    fun retrieveExistingKey(iv: ByteArray): Cipher {
        val ivSpec = GCMParameterSpec(BLOCK_SIZE_IN_BITS, iv)
        val key = keystore.getKey(KEY_ALIAS, null)

        val cipher = Cipher.getInstance(CIPHER_SUITE)

        return try {
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
            cipher
        } catch (ex: KeyPermanentlyInvalidatedException) {
            throw ex
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    fun generateNewKey(): Cipher {
        val keyGenerator =
            KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
        val keySpecifications =
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setKeySize(KEY_SIZE_IN_BITS)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(true)
                .setRandomizedEncryptionRequired(false)
                .build()

        return try {
            keyGenerator.init(keySpecifications)
            val secretKey = keyGenerator.generateKey()
            val cipher = Cipher.getInstance(CIPHER_SUITE)

            val iv = ByteArray(IV_SIZE_IN_BYTES)
            SecureRandom().nextBytes(iv)

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(BLOCK_SIZE_IN_BITS, iv))

            cipher
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "myKey2"
        private const val CIPHER_SUITE = "AES/GCM/NoPadding"
        private const val BLOCK_SIZE_IN_BITS = 128
        private const val KEY_SIZE_IN_BITS = 256
        private const val IV_SIZE_IN_BYTES = 12
    }

}
