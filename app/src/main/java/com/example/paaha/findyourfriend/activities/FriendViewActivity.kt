package com.example.paaha.findyourfriend.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.paaha.findyourfriend.R
import com.example.paaha.findyourfriend.logic.ValueEventAdapter
import com.example.paaha.findyourfriend.model.FriendInfo
import com.example.paaha.findyourfriend.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_friend_view.*

class FriendViewActivity : AppCompatActivity() {

    var user: User? = null
    var friendInfo: FriendInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_view)

        val userId = intent.getSerializableExtra(USER_ID) ?: return
        initUser(userId as String)

        pinInputButton.setOnClickListener {
            val userPin = user?.pin
            if (userPin == null || userPin.isEmpty())
                return@setOnClickListener

            val inputPin = pinInput.text.toString()
            if (userPin == inputPin) {
                updateUserActive()
            }
        }
    }

    private fun updateUserActive() {
        val authUserId = FirebaseAuth.getInstance().uid ?: return
        friendInfo?.active = true

        FirebaseDatabase.getInstance()
            .getReference(getString(R.string.key_friends))
            .child(authUserId)
            .child(friendInfo!!.id)
            .setValue(friendInfo!!)

        updateUI()

    }

    private fun updateUI() {
        if (user != null) {
            friendViewName.text = user!!.name
            friendViewEmail.text = user!!.email
        }
        if (friendInfo != null) {
            if (friendInfo!!.active) {
                pinLayout.visibility = View.GONE
                introduceTextView.text = getString(R.string.friend_view_active)

            } else {
                pinLayout.visibility = View.VISIBLE
                introduceTextView.text = getString(R.string.friend_view_not_active)
            }
        }
    }

    private fun initUser(userId: String) {
        val authUserId = FirebaseAuth.getInstance().uid ?: return

        val refUser = FirebaseDatabase
            .getInstance()
            .getReference(getString(R.string.key_users))
            .child(userId)

        refUser.addListenerForSingleValueEvent(object : ValueEventAdapter() {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                initFriendInfo(userId, authUserId)
            }
        })

    }

    private fun initFriendInfo(userId: String, authUserId: String) {

        val refFriendInfo = FirebaseDatabase
            .getInstance()
            .getReference(getString(R.string.key_friends))
            .child(authUserId)

        refFriendInfo.addListenerForSingleValueEvent(object : ValueEventAdapter() {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val friend = it.getValue(FriendInfo::class.java)
                    if (friend != null && friend.friend == userId) {
                        friendInfo = friend
                        updateUI()
                        return@forEach
                    }
                }
            }
        })
    }

    companion object {
        const val USER_ID = "USER_ID"
        fun newIntent(packageContext: Context, userID: String): Intent {
            val intent = Intent(packageContext, FriendViewActivity::class.java)
            intent.putExtra(USER_ID, userID)
            return intent
        }
    }
}
