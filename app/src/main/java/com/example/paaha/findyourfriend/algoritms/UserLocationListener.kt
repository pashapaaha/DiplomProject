package com.example.paaha.findyourfriend.algoritms

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.example.paaha.findyourfriend.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserLocationListener : LocationListener {

    companion object {

        private val TAG = "UserLocationListener"

        private val listener = UserLocationListener()
        private var manager: LocationManager? = null

        private var mAuth: FirebaseAuth? = null

        @SuppressLint("MissingPermission")
        fun startLocation(context: Context) {
            Log.d(TAG, "startLocation method")
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

    private fun updateUserLocations(uid: String, location: Location){

        Log.d(TAG, "updateUserLocations: started")
        val ref = FirebaseDatabase
            .getInstance()
            .getReference("/users")
            .child(uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG, "updateUserLocations: start get user")
                val user = p0.getValue(User::class.java)
                if(user != null){
                    Log.d(TAG, "updateUserLocations: user not null")
                    ref.child("latitude").setValue(location.latitude)
                    ref.child("longitude").setValue(location.longitude)
                    ref.child("lastLocationUpdate").setValue(System.currentTimeMillis())
                }
            }
        })
    }
}