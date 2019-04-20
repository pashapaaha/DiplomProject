package com.example.paaha.findyourfriend.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.paaha.findyourfriend.R
import com.example.paaha.findyourfriend.model.FriendInfo
import com.example.paaha.findyourfriend.model.FriendInfoList
import com.example.paaha.findyourfriend.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_search_friend.*
import kotlinx.android.synthetic.main.one_string_layout.view.*

class SearchNewFriendsActivity : AppCompatActivity() {

    private val TAG = this.javaClass.name

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_friend)

        add_friend_recycler_view.adapter = adapter

        findByEmailButton.setOnClickListener {
            Log.d(TAG, "search on click")
            val emailPart = searchByEmailEditText.text.toString().trim()
            if (emailPart.isEmpty()) {
                searchByEmailEditText.error = getString(R.string.input_value)
                return@setOnClickListener
            } else {
                searchByEmailEditText.error = null
            }

            Log.d(TAG, "success validation in on click")
            searchByEmailPart(emailPart.toLowerCase())
        }
    }


    private fun searchByEmailPart(emailPart: String) {
        val ref = FirebaseDatabase
            .getInstance()
            .getReference(getString(R.string.key_users))

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clear()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    Log.d(TAG, "find user")
                    if (user != null && user.email.contains(emailPart)) {
                        val currentEmail = FirebaseAuth.getInstance().currentUser?.email
                        if (user.email == currentEmail || FriendInfoList.contains(user.uid))
                            return@forEach
                        Log.d(TAG, "add user with email")
                        adapter.add(EmailItem(user))
                    }
                }
            }
        })
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            val intent = Intent(packageContext, SearchNewFriendsActivity::class.java)
            return intent
        }
    }
}

class EmailItem(val user: User) : Item<ViewHolder>() {
    override fun getLayout() = R.layout.one_string_layout

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.stringTextView.text = user.email

        viewHolder.itemView.setOnClickListener {
            addFriend(viewHolder.itemView.context)
        }
    }

    private fun addFriend(context: Context) {
        val uid = FirebaseAuth.getInstance().uid ?: return

        val ref = FirebaseDatabase
            .getInstance()
            .getReference(context.getString(R.string.key_friends))
            .child(uid)
            .push()
        if (ref.key == null)
            return
        val id = ref.key!!
        ref.setValue(FriendInfo(id, user.uid))
            .addOnSuccessListener {
                (context as AppCompatActivity).finish()
            }
            .addOnFailureListener {
                Log.d(this.javaClass.name, "add friend was failed")
            }
    }
}
