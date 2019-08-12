package com.example.authflowexample.data

import android.os.Build

class SystemInfo {

    enum class API {
        P_HIGHER, P_LOWER
    }

    fun getCurrentAndroidVersion() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        API.P_HIGHER
    } else {
        API.P_LOWER
    }

}
