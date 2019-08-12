package com.example.authflowexample.features.auth.fingerprint.checks

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat

class FingerprintPermissionCheck(private val context: Context) : Check {
    override fun isValid(): Boolean {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.USE_BIOMETRIC
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                @Suppress("DEPRECATION")
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.USE_FINGERPRINT
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                return false
            }
        }
        Log.v("Check", "FingerprintPermissionCheck: $result")
        return result
    }
}
