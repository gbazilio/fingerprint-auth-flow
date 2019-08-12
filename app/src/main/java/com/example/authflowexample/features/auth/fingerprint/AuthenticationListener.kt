package com.example.authflowexample.features.auth.fingerprint

import com.example.authflowexample.data.FingerprintCryptoObject

interface AuthenticationListener {
    fun onSuccess(fingerprintCryptoObject: FingerprintCryptoObject)
    fun onFailure(errorMessage: String)
}
