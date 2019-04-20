package com.example.paaha.findyourfriend.logic

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.example.paaha.findyourfriend.R
import com.example.paaha.findyourfriend.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class UserLocationListener : LocationListener {

    companion object {

        private val TAG = "UserLocationListener"

        private val listener = UserLocationListener()
        private var manager: LocationManager? = null

        private var mAuth: FirebaseAuth? = null

        private var context: Context? = null

        @SuppressLint("MissingPermission")
        fun startLocation(context: Context) {
            Log.d(TAG, "startLocation method")
            this.context = context

            mAuth = FirebaseAuth.getInstance()

            manager = context.getSystemService(Context.LOCATION_SERVICE)
                    as LocationManager
            manager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                10F,
                listener
            )
        }

        fun stopLocation() {
            manager?.removeUpdates(listener)
            mAuth = null
        }
    }

    override fun onLocationChanged(location: Location?) {
        Log.d(TAG, "onLocationChanged: started")
        val uid = mAuth?.uid ?: return
        Log.d(TAG, "onLocationChanged: mAuth is not null")

        location?.let {
            Log.d(TAG, "onLocationChanged: location is not null")
            updateUserLocations(uid, location)
        }


    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {}

    private fun updateUserLocations(uid: String, location: Location) {

        Log.d(TAG, "updateUserLocations: started")
        val ref = FirebaseDatabase
            .getInstance()
            .getReference(context!!.getString(R.string.key_users))
            .child(uid)
        ref.addListenerForSingleValueEvent(object : ValueEventAdapter() {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "updateUserLocations: start get user")
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    Log.d(TAG, "updateUserLocations: user not null")
                    ref.child("latitude").setValue(location.latitude)
                    ref.child("longitude").setValue(location.longitude)
                    ref.child("lastLocationUpdate").setValue(System.currentTimeMillis())
                }
            }
        })
    }
}