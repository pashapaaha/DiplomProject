package com.example.paaha.findyourfriend.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.paaha.findyourfriend.R
import com.example.paaha.findyourfriend.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*


class RegistrationActivity : AppCompatActivity() {

    private val TAG = this.javaClass.name

    private var mAuth: FirebaseAuth? = null

    private var email: String = ""
    private var password: String = ""
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        init()
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()

        registrationButton.setOnClickListener {
            Log.d(TAG, "button on click")
            if (!isValid())
                return@setOnClickListener

            email = emailEditText.text.toString().trim()
            password = passwordEditText.text.toString().trim()
            name = nameEditText.text.toString().trim()

            mAuth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "is success")
                        isSuccess()
                    } else {
                        Log.d(TAG, "is wrong")
                        isFailed(it)
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


    private fun isSuccess() {
        val uid = FirebaseAuth.getInstance().uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference(getString(R.string.key_users))
            .child(uid)
        Log.d(TAG, "reference get")
        val user = User(uid, email, name)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "user $user was added")
            }
            .addOnFailureListener {
                Log.d(TAG, "wrong added")
            }

        this.finish()
    }

    private fun isFailed(task: Task<AuthResult>) {
        Toast.makeText(this, getString(R.string.failed_reg), Toast.LENGTH_SHORT).show()
        try {
            if (task.exception != null) {
                throw task.exception!!
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

    companion object {
        fun newIntent(packageContext: Context) =
            Intent(packageContext, RegistrationActivity::class.java)
    }
}
