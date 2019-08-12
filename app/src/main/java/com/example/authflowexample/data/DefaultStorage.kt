package com.example.authflowexample.data

import android.content.SharedPreferences
import android.util.Base64

class DefaultStorage(private val sharedPreferences: SharedPreferences) : Storage {
    override fun saveFingerprintCatalog(fingerprintCatalog: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_FINGERPRINT_CATALOG, fingerprintCatalog)
        editor.apply()
    }

    override fun getFingerprintCatalog(): String? {
        return sharedPreferences.getString(KEY_FINGERPRINT_CATALOG, null)
    }

    override fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    override fun getPassword(): ByteArray? {
        val password = sharedPreferences.getString(KEY_PASSWORD, null) ?: return null
        return Base64.decode(password, Base64.NO_WRAP)
    }

    override fun getIV(): ByteArray? {
        val iv: String = sharedPreferences.getString(KEY_CIPHER_IV, null) ?: return null
        return Base64.decode(iv, Base64.NO_WRAP)
    }

    override fun saveIV(iv: ByteArray) {
        val editor = sharedPreferences.edit()

        val base64Iv = Base64.encodeToString(iv, Base64.NO_WRAP)
        editor.putString(KEY_CIPHER_IV, base64Iv)
        editor.apply()
    }

    override fun saveCredentials(username: String, password: ByteArray) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)

        val base64password = Base64.encodeToString(password, Base64.NO_WRAP)
        editor.putString(KEY_PASSWORD, base64password)
        editor.apply()
    }

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_CIPHER_IV = "iv"
        private const val KEY_FINGERPRINT_CATALOG = "fingerprint_catalog"
    }
}
