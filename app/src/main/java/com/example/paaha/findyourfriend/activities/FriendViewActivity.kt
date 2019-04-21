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
    }

    private fun initUI() {
        if (user != null){
            friendViewName.text = user!!.name
            friendViewEmail.text = user!!.email
        }
        if (friendInfo != null){
            val visibility = if(friendInfo!!.active) View.GONE else View.VISIBLE
            pinLayout.visibility = visibility
        }
    }

    private fun initUser(userId: String) {
        val authUserId = FirebaseAuth.getInstance().uid ?: return

        val refUser = FirebaseDatabase
            .getInstance()
            .getReference(getString(R.string.key_users))
            .child(userId)

        refUser.addListenerForSingleValueEvent(object: ValueEventAdapter(){
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                initFriendInfo(userId, authUserId)
            }
        })

    }

    private fun initFriendInfo(userId: String, authUserId: String){

        val refFriendInfo = FirebaseDatabase
            .getInstance()
            .getReference(getString(R.string.key_friends))
            .child(authUserId)
            .child(userId)

        refFriendInfo.addListenerForSingleValueEvent(object: ValueEventAdapter(){
            override fun onDataChange(snapshot: DataSnapshot) {
                friendInfo = snapshot.getValue(FriendInfo::class.java)
                initUI()
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
