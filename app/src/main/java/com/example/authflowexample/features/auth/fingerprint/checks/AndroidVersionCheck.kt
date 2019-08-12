package com.example.authflowexample.features.auth.fingerprint.checks

import android.os.Build
import android.util.Log

class AndroidVersionCheck(private val targetVersion: Int) : Check {
    override fun isValid(): Boolean {
        val result = Build.VERSION.SDK_INT >= targetVersion
        Log.v("Check", "AndroidVersionCheck: $result")
        return result
    }
}
