package com.example.paaha.findyourfriend.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.paaha.findyourfriend.R
import com.example.paaha.findyourfriend.activities.abstractActivities.LogoutMenuActivity
import com.example.paaha.findyourfriend.logic.ValueEventAdapter
import com.example.paaha.findyourfriend.model.FriendInfo
import com.example.paaha.findyourfriend.model.FriendInfoList
import com.example.paaha.findyourfriend.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_users_list.*
import kotlinx.android.synthetic.main.friend_list_item_layout.view.*

class UsersListActivity : LogoutMenuActivity() {

    private val TAG = this.javaClass.name

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)

        fab.setOnClickListener {
            startActivity(SearchNewFriendsActivity.newIntent(this))
        }
    }

    override fun onResume() {
        super.onResume()

        fetchFriends()
        friend_recycler_view.adapter = adapter
    }

    private fun fetchFriends() {
        Log.d(TAG, "fetchFriends method")
        val uid = FirebaseAuth.getInstance()?.uid ?: return
        val ref = FirebaseDatabase
            .getInstance()
            .getReference(getString(R.string.key_friends))
            .child(uid)

        ref.addListenerForSingleValueEvent(object : ValueEventAdapter() {
            override fun onDataChange(snapshot: DataSnapshot) {
                FriendInfoList.clear()
                adapter.clear()
                snapshot.children.forEach {
                    val friendInfo = it.getValue(FriendInfo::class.java)
                    friendInfo?.let { addUserToAdapter(friendInfo.friend) }
                }
            }
        })
    }

    private fun addUserToAdapter(friend: String) {
        val ref = FirebaseDatabase
            .getInstance()
            .getReference(getString(R.string.key_users))
            .child(friend)

        ref.addListenerForSingleValueEvent(object : ValueEventAdapter() {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                user?.let { adapter.add(FriendItem(user)) }
                FriendInfoList.add(friend)
            }
        })
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            val intent = Intent(packageContext, UsersListActivity::class.java)
            return intent
        }
    }
}

class FriendItem(private val user: User) : Item<ViewHolder>() {
    override fun getLayout() = R.layout.friend_list_item_layout

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.name_item_text_view.text = user.name

        viewHolder.itemView.setOnClickListener{
            (it.context as AppCompatActivity)
                .startActivity(FriendViewActivity.newIntent(it.context, user.uid))
        }
    }
}
