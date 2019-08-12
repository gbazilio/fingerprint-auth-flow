package com.example.authflowexample.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.authflowexample.data.Storage
import com.example.authflowexample.features.auth.fingerprint.checks.Check
import com.example.authflowexample.usecases.SelectAuthenticationMethodUseCase
import com.example.authflowexample.usecases.HandleAuthenticatedFingerprintUseCase

class AuthViewModelFactory(
    private val checks: Check,
    private val storage: Storage,
    private val selectAuthenticationMethodUseCase: SelectAuthenticationMethodUseCase,
    private val handleAuthenticatedFingerprintUseCase: HandleAuthenticatedFingerprintUseCase
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(
            checks,
            storage,
            selectAuthenticationMethodUseCase,
            handleAuthenticatedFingerprintUseCase
        ) as T
    }

}
