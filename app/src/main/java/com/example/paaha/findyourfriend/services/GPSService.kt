package com.example.paaha.findyourfriend.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.os.Bundle

class GPSService : Service() {

    @SuppressLint("MissingPermission")
    private var listener: LocationListener? = null

    private var locationManager: LocationManager? = null

    override fun onBind(intent: Intent) = null

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        listener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProviderEnabled(provider: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProviderDisabled(provider: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0f, listener)

    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager?.removeUpdates(listener)
    }

    companion object {
        fun newIntent(packageContext: Context) =
            Intent(packageContext, GPSService::class.java)
    }
}
