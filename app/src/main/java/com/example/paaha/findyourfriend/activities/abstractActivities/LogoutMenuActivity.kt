package com.example.paaha.findyourfriend.activities.abstractActivities

import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.paaha.findyourfriend.R
import com.example.paaha.findyourfriend.activities.LoginActivity
import com.example.paaha.findyourfriend.logic.UserLocationListener
import com.google.firebase.auth.FirebaseAuth

open class LogoutMenuActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.signout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.log_out_menu_item -> {
                UserLocationListener.stopLocation()
                FirebaseAuth.getInstance().signOut()
                startActivity(LoginActivity.newIntent(this))
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}