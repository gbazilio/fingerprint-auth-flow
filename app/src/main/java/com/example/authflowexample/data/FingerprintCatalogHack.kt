package com.example.authflowexample.data

import android.content.Context

/*
 Code from:
 https://issuetracker.google.com/issues/65578763#comment23
 */
class FingerprintCatalogHack(val context: Context) {
    fun getEnrolledFingerprintIds(): String {
        try {
            val fingerprintManager = context.getSystemService(Context.FINGERPRINT_SERVICE)
            val method = fingerprintManager.javaClass.getDeclaredMethod("getEnrolledFingerprints")
            val obj = method.invoke(fingerprintManager) ?: return ""

            val clazz = Class.forName("android.hardware.fingerprint.Fingerprint")
            val getFingerId = clazz.getDeclaredMethod("getFingerId")

            return (obj as List<Any>).joinToString(separator = "") {
                getFingerId.invoke(it).toString()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
    }
}
