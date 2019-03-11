package com.example.paaha.findyourfriend

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO: добавить автоматическую авторизацию
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    override fun onStart() {
        super.onStart()
        currentUser = mAuth?.currentUser
        updateUI(currentUser != null)
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            if (isValid()) {
                logIn()
            }
        }

        registrationTextView.setOnClickListener {
            val intent = RegistrationActivity.newIntent(this)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            logOut()
        }
    }

    private fun logIn() {
        mAuth?.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    updateUI(true)
                } else {
                    Toast.makeText(this, getString(R.string.failed_login), Toast.LENGTH_SHORT).show()
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
                        Log.e(RegistrationActivity::class.java.name, e.message)
                    }
                }
            }
    }

    private fun logOut() {
        mAuth?.signOut()
        updateUI(false)
    }

    private fun updateUI(isAuthorized: Boolean) {
        if (isAuthorized) {

            nameTextView.text = currentUser?.displayName ?: "null"
            emailTextView.text = currentUser?.email ?: "null"

            authorizedLayout.visibility = View.GONE
            notAuthorizedLayout.visibility = View.VISIBLE
        } else {
            authorizedLayout.visibility = View.VISIBLE
            notAuthorizedLayout.visibility = View.GONE
        }
    }

    private fun isValid(): Boolean {
        var result = true
        if (!emailEditText.text.toString().matches("[a-zA-Z0-9]+(.[a-zA-Z0-9]*)*@[a-zA-Z0-9]*.[a-zA-Z]{2,}".toRegex())) {
            emailEditText.error = getString(R.string.not_valid_email)
            result = false
        } else {
            emailEditText.error = null
        }
        if (passwordEditText.text.toString().isEmpty()) {
            passwordEditText.error = getString(R.string.input_password)
            result = false
        } else {
            passwordEditText.error = null
        }
        return result
    }

    companion object {
        fun newIntent(packageContext: Context) =
            Intent(packageContext, LoginActivity::class.java)
    }
}
