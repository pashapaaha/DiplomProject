package com.example.paaha.findyourfriend.algoritms

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

class UserLocationListener : LocationListener {

    companion object {
        var currentLocation: Location? = null
        private val listener = UserLocationListener()
        private var manager: LocationManager? = null

        @SuppressLint("MissingPermission")
        fun startLocation(context: Context) {
            manager = context.getSystemService(Context.LOCATION_SERVICE)
                    as LocationManager
            manager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                10F,
                listener
            )
            currentLocation = manager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }

        fun stopLocation() {
            manager?.removeUpdates(listener)
            currentLocation = null
        }
    }

    override fun onLocationChanged(location: Location?) {
        currentLocation = location
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {}
}