package com.example.authflowexample.features.auth

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.authflowexample.data.FingerprintCryptoObject
import com.example.authflowexample.data.Storage
import com.example.authflowexample.features.auth.fingerprint.checks.Check
import com.example.authflowexample.usecases.HandleAuthenticatedFingerprintUseCase
import com.example.authflowexample.usecases.Result
import com.example.authflowexample.usecases.SelectAuthenticationMethodUseCase
import javax.crypto.Cipher

sealed class ViewState {
    object AuthMethodFallback : ViewState()
    object AuthCancelled : ViewState()

    object LoginSuccessful : ViewState()
    object LoginFailed : ViewState()
    object TamperedFingerprintCatalog : ViewState()

    data class AuthMethodFingerprint(val cipher: Cipher) : ViewState()
    data class AuthMethodBiometric(val protectedCipher: Cipher) : ViewState()
    data class FingerprintRequested(val protectedCipher: Cipher) : ViewState()
}

class AuthViewModel(
    private val checks: Check,
    private val storage: Storage,
    private val selectAuthenticationMethodUseCase: SelectAuthenticationMethodUseCase,
    private val handleAuthenticatedFingerprintUseCase: HandleAuthenticatedFingerprintUseCase
) : ViewModel() {

    private var username: String = ""
    private var password: String = ""

    private lateinit var cipher: Cipher

    private val internalViewState = MutableLiveData<ViewState>()
    val changes: LiveData<ViewState> = internalViewState

    fun requestAuthentication(username: String, password: String, registerForFingerprint: Boolean) {
        this.username = username
        this.password = password

        if (!registerForFingerprint) {
            internalViewState.value = ViewState.LoginSuccessful
            return
        }

        if (!checks.isValid()) {
            internalViewState.value = ViewState.AuthMethodFallback
            return
        }

        selectAuthenticationMethod()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun startFingerprintAuthentication() {
        val lastState = internalViewState.value ?: return
        if (lastState is ViewState.FingerprintRequested) return

        internalViewState.value = ViewState.FingerprintRequested(cipher)
    }

    fun cancelCurrentAuthentication() {
        internalViewState.value = ViewState.AuthCancelled
    }

    fun notifyFingerprintAuthenticated(fingerprintCryptoObject: FingerprintCryptoObject) {
        val result = handleAuthenticatedFingerprintUseCase.execute(
            username,
            password,
            fingerprintCryptoObject
        )
        when (result) {
            is Result.LoginWithFingerprintFailed -> internalViewState.value =
                ViewState.LoginFailed
            is Result.LoginWithFingerprintSuccessful -> internalViewState.value =
                ViewState.LoginSuccessful
        }
    }

    fun autoRequestAuthenticationIfFingerprintIsLinked() {
        if (!checks.isValid()) {
            return
        }

        if (storage.getUsername() == null) return

        selectAuthenticationMethod()
    }

    private fun selectAuthenticationMethod() {
        when (val result = selectAuthenticationMethodUseCase.execute()) {
            is Result.AuthMethodFingerprint -> {
                cipher = result.cipher
                internalViewState.value = ViewState.AuthMethodFingerprint(cipher)
            }
            is Result.AuthMethodBiometric -> {
                cipher = result.cipher
                internalViewState.value = ViewState.AuthMethodBiometric(cipher)
            }
            is Result.TamperedFingerprintCatalog -> {
                internalViewState.value = ViewState.TamperedFingerprintCatalog
            }
        }
    }

}
