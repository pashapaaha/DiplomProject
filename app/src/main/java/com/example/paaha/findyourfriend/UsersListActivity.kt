package com.example.paaha.findyourfriend

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import com.example.paaha.findyourfriend.activities.LoginActivity
import com.example.paaha.findyourfriend.model.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_users_list.*
import kotlinx.android.synthetic.main.friend_list_item_layout.view.*

class UsersListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)
        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(FriendItem(User("", "", "1")))
        adapter.add(FriendItem(User("", "", "2")))
        adapter.add(FriendItem(User("", "", "3")))
        adapter.add(FriendItem(User("", "", "4")))
        adapter.add(FriendItem(User("", "", "5")))

        friend_recycler_view.adapter = adapter
    }


    companion object {
        fun newIntent(packageContext: Context): Intent {
            val intent = Intent(packageContext, UsersListActivity::class.java)
            return intent
        }
    }
}

class FriendItem(val user: User): Item<ViewHolder>(){
    override fun getLayout() = R.layout.friend_list_item_layout

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.name_item_text_view.text = user.name
    }
}
