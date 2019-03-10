package com.example.paaha.findyourfriend

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
    }

    fun init(){
        mAuth = FirebaseAuth.getInstance()
    }

    companion object {
        fun newIntent(packageContext: Context) =
            Intent(packageContext, RegistrationActivity::class.java)
    }
}
