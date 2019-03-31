package com.example.paaha.findyourfriend.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.paaha.findyourfriend.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registration.*
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


class RegistrationActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        init()
    }

    fun init() {
        mAuth = FirebaseAuth.getInstance()

        registrationButton.setOnClickListener {
            if (isValid()) {
                mAuth?.createUserWithEmailAndPassword(
                    emailEditText.text.toString().trim(),
                    passwordEditText.text.toString().trim()
                )
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val profile = UserProfileChangeRequest.Builder()
                                .setDisplayName(nameEditText.text.toString().trim())
                                .build()
                            FirebaseAuth.getInstance().currentUser?.updateProfile(profile)
                            this.finish()
                        } else {
                            Toast.makeText(this, getString(R.string.failed_reg), Toast.LENGTH_SHORT).show()
                            try {
                                if (it.exception != null) {
                                    throw it.exception!!
                                }
                            } catch (e: FirebaseAuthWeakPasswordException) {
                                passwordEditText.error = e.message
                                passwordEditText.requestFocus()
                            } catch (e: FirebaseAuthUserCollisionException) {
                                emailEditText.error = e.message
                                emailEditText.requestFocus()
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                emailEditText.error = e.message
                                emailEditText.requestFocus()
                            } catch (e: Exception) {
                                Log.e(RegistrationActivity::class.java.getName(), e.message)
                            }
                        }
                    }
            }
        }
    }

    private fun isValid(): Boolean {
        var result = true

        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (name.isEmpty()) {
            nameEditText.error = getString(R.string.input_name)
            result = false
        } else {
            nameEditText.error = null
        }
        if (!email.matches("[a-zA-Z0-9]+(.[a-zA-Z0-9]*)*@[a-zA-Z0-9]*.[a-zA-Z]{2,}".toRegex())) {
            emailEditText.error = getString(R.string.not_valid_email)
            result = false
        } else {
            emailEditText.error = null
        }
        if (password.isEmpty()) {
            passwordEditText.error = getString(R.string.input_password)
            result = false
        } else {
            passwordEditText.error = null
        }
        if (confirmPassword != password) {
            confirmPasswordEditText.error = getString(R.string.not_equals)
            result = false
        } else {
            confirmPasswordEditText.error = null
        }

        return result
    }

    companion object {
        fun newIntent(packageContext: Context) =
            Intent(packageContext, RegistrationActivity::class.java)
    }
}
