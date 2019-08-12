package com.example.authflowexample.features.auth.fingerprint.checks

class CheckComposite : Check {

    private var checks = emptyList<Check>()

    fun addCheck(check: Check) {
        checks = checks + check
    }

    override fun isValid(): Boolean {
        return checks.all { it.isValid() }
    }
}
