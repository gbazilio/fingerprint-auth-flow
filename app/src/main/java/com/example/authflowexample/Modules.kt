package com.example.authflowexample

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.example.authflowexample.data.*
import com.example.authflowexample.features.auth.AuthViewModel
import com.example.authflowexample.features.auth.fingerprint.checks.*
import com.example.authflowexample.usecases.HandleAuthenticatedFingerprintUseCase
import com.example.authflowexample.usecases.SelectAuthenticationMethodUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<Check> {
        CheckComposite().apply {
            addCheck(AndroidVersionCheck(Build.VERSION_CODES.M))
            addCheck(FingerprintPermissionCheck(androidApplication()))
            addCheck(FingerprintHardwareCheck(androidApplication()))
            addCheck(FingerprintEnrolledCheck(androidApplication()))
        }
    }

    single<SharedPreferences> {
        androidApplication().getSharedPreferences(
            "com.example.authflowexample.config",
            Context.MODE_PRIVATE
        )
    }

    single<Storage> {
        DefaultStorage(get())
    }

    single { FingerprintCatalogHack(androidApplication()) }

    single { SystemInfo() }

    single { CryptoKeyManager() }

    single {
        SelectAuthenticationMethodUseCase(
            systemInfo = get(),
            storage = get(),
            cryptoKeyManager = get(),
            fingerprintCatalogHack = get()
        )
    }

    single {
        HandleAuthenticatedFingerprintUseCase(
            storage = get()
        )
    }

    viewModel {
        AuthViewModel(
            checks = get(),
            storage = get(),
            selectAuthenticationMethodUseCase = get(),
            handleAuthenticatedFingerprintUseCase = get()
        )
    }
}
