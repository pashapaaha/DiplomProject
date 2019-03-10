package com.example.paaha.findyourfriend

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.FirebaseAuth



class LoginActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {

            authorizedLayout.visibility = View.GONE
            notAuthorizedLayout.visibility = View.VISIBLE
        }

        registrationTextView.setOnClickListener{
            val intent = RegistrationActivity.newIntent(this)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            authorizedLayout.visibility = View.VISIBLE
            notAuthorizedLayout.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth?.currentUser
    }

    companion object {
        fun newIntent(packageContext: Context) =
            Intent(packageContext, LoginActivity::class.java)
    }
}
