package com.example.paaha.findyourfriend.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.paaha.findyourfriend.R
import com.example.paaha.findyourfriend.logic.ValueEventAdapter
import com.example.paaha.findyourfriend.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val uid = FirebaseAuth.getInstance()?.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference(getString(R.string.key_users))
            .child(uid)

        ref.addListenerForSingleValueEvent(object : ValueEventAdapter() {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java) ?: return
                updateUI(user)
            }
        })
    }

    private fun updateUI(user: User) {
        myNameView.text = user.name
        myEmailView.text = user.email
        myPinView.text = user.pin
    }

    companion object {
        fun newIntent(packageContext: Context) = Intent(packageContext, AboutActivity::class.java)
    }
}
