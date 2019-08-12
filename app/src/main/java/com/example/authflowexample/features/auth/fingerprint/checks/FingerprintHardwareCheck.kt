package com.example.authflowexample.features.auth.fingerprint.checks

import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.util.Log
import androidx.core.hardware.fingerprint.FingerprintManagerCompat


class FingerprintHardwareCheck(private val context: Context) : Check {
    override fun isValid(): Boolean {
        val result = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                val biometricManager =
                    context.getSystemService(BiometricManager::class.java) as BiometricManager
                biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
                        || biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.M ->
                @Suppress("DEPRECATION") {
                    val fingerprintManager =
                        context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                    fingerprintManager.isHardwareDetected
                }
            Build.VERSION.SDK_INT > Build.VERSION_CODES.M -> {
                val fingerprintManager = FingerprintManagerCompat.from(context)
                fingerprintManager.isHardwareDetected
            }
            else -> false
        }
        Log.v("Check", "FingerprintHardwareCheck: $result")
        return result
    }
}
