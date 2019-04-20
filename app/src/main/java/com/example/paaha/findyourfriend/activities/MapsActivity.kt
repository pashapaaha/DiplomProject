package com.example.paaha.findyourfriend.activities

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import com.example.paaha.findyourfriend.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.view.Menu
import android.view.MenuItem
import com.example.paaha.findyourfriend.activities.abstractActivities.LogoutMenuActivity
import com.example.paaha.findyourfriend.logic.UserLocationListener
import com.example.paaha.findyourfriend.logic.ValueEventAdapter
import com.example.paaha.findyourfriend.model.FriendInfo
import com.example.paaha.findyourfriend.model.FriendInfoList
import com.example.paaha.findyourfriend.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class MapsActivity : LogoutMenuActivity(), OnMapReadyCallback {
    private val TAG = this.javaClass.name

    private lateinit var mMap: GoogleMap

    private var currentUID = ""

    private val LOCATION_PERMISSIONS =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val REQUEST_LOCATION_PERMISSIONS = 0

    private val timer = Timer()

    private val allowedTimeDifference = 120_000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mapInit()

        startListenerWork()

        currentUID = FirebaseAuth.getInstance()?.uid ?: return

        fillFriendList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.to_list_button -> {
                startActivity(UsersListActivity.newIntent(this))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread { timerTick() }
            }
        }, 0, 5000)
    }

    private fun mapInit() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun startListenerWork() {
        if (hasLocationPermission()) {
            UserLocationListener.startLocation(this)
        } else {
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS)
        }
    }

    private fun hasLocationPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, LOCATION_PERMISSIONS[0])
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun fillFriendList() {
        val ref = FirebaseDatabase
            .getInstance()
            .getReference(getString(R.string.key_friends))
            .child(currentUID)

        ref.addListenerForSingleValueEvent(object : ValueEventAdapter() {
            override fun onDataChange(snapshot: DataSnapshot) {
                FriendInfoList.clear()
                snapshot.children.forEach {
                    val friendInfo = it.getValue(FriendInfo::class.java)
                    friendInfo?.let {
                        Log.d(TAG, "onCreate: friend is added")
                        FriendInfoList.add(friendInfo.friend)
                    }
                }
            }
        })
    }

    private fun timerTick() {
        Log.d(TAG, "timerTick: start")
        val ref = FirebaseDatabase
            .getInstance()
            .getReference(getString(R.string.key_users))

        mMap.clear()

        val shownUsers = FriendInfoList.getList()
        shownUsers.add(currentUID)
        shownUsers.forEach {
            ref.child(it).addListenerForSingleValueEvent(object : ValueEventAdapter() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    super.onDataChange(snapshot)
                    val user = snapshot.getValue(User::class.java)
                    checkAndShowUser(user)
                }
            })
        }
    }

    private fun checkAndShowUser(user: User?) {
        val lastLocationUpdate = user?.lastLocationUpdate ?: return
        val timeDifference = System.currentTimeMillis() - lastLocationUpdate

        Log.d(TAG, "checkAndShowUser(): time difference for (${user.name} is ${timeDifference / 1000 - 110} seconds)")

        val isCurrent = user.uid == currentUID
        if (isCurrent || timeDifference < allowedTimeDifference) {
            addMarker(user, isCurrent)
        }
    }

    private fun addMarker(user: User, isMe: Boolean) {
        Log.d(TAG, "addMarker(): for user ${user.name}")

        val lat = user.latitude!!
        val lng = user.longitude!!
        val userPosition = LatLng(lat, lng)
        mMap.addMarker(MarkerOptions().position(userPosition).title(user.name))
        if (isMe) { //TODO: попытаться поменять цвет!
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition))
        }
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            val intent = Intent(packageContext, MapsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }
    }
}
