package com.example.authflowexample.features.auth.fingerprint


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.example.authflowexample.R
import com.example.authflowexample.data.FingerprintCryptoObject
import com.example.authflowexample.features.auth.AuthViewModel
import com.example.authflowexample.features.auth.ViewState
import kotlinx.android.synthetic.main.fragment_fingerprint.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import javax.crypto.Cipher

@RequiresApi(Build.VERSION_CODES.M)
class FingerprintFragment : DialogFragment(), AuthenticationListener {

    val authViewModel: AuthViewModel by sharedViewModel()

    private var cancellationSignal: CancellationSignal? = null

    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_fingerprint, container, false)
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel.changes.observe(this, Observer { viewState ->
            when (viewState) {
                is ViewState.FingerprintRequested -> listenFingerprint(viewState.protectedCipher)
                ViewState.AuthCancelled -> cancelAuthentication()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        authViewModel.startFingerprintAuthentication()
    }

    override fun onPause() {
        super.onPause()
        authViewModel.cancelCurrentAuthentication()
    }

    private fun listenFingerprint(protectedCipher: Cipher) {
        context?.let {
            val fingerprintManager = FingerprintManagerCompat.from(it)
            val cryptoObject = FingerprintManagerCompat.CryptoObject(protectedCipher)
            val callback = AuthenticationCallback23(this)
            cancellationSignal = CancellationSignal()

            fingerprintManager.authenticate(cryptoObject, 0, cancellationSignal, callback, null)
        }
    }

    private fun cancelAuthentication() {
        cancellationSignal?.cancel()
    }

    override fun onSuccess(fingerprintCryptoObject: FingerprintCryptoObject) {
        authViewModel.notifyFingerprintAuthenticated(fingerprintCryptoObject)
        dismiss()
    }

    override fun onFailure(errorMessage: String) {
        rootView.textViewErrorMessage.text = errorMessage
        rootView.textViewErrorMessage.visibility = View.VISIBLE
        rootView.textViewErrorMessage.postDelayed({
            rootView.textViewErrorMessage.text = ""
            rootView.textViewErrorMessage.visibility = View.INVISIBLE
        }, 2000)
    }

}
