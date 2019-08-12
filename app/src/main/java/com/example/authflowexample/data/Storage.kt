package com.example.authflowexample.data

interface Storage {
    fun saveCredentials(username: String, password: ByteArray)
    fun getIV(): ByteArray?
    fun saveIV(iv: ByteArray)
    fun getUsername(): String?
    fun getPassword(): ByteArray?
    fun getFingerprintCatalog(): String?
    fun saveFingerprintCatalog(fingerprintCatalog: String)
}
