package com.example.paaha.findyourfriend.activities

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
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


class MapsActivity : LogoutMenuActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val LOCATION_PERMISSIONS =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val REQUEST_LOCATION_PERMISSIONS = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mapInit()

        //TODO: после того как координаты будут записываться в объект пользователя,
        // обеспечить считывание местоположения всех активных пользователей,
        // предумотреть отслеживание времени,
        // если время будет превышать некоторое значение, маркер отображаться не должен
        startListenerWork()
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

    fun mapInit() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
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

    private fun addMarker(lat: Double, lng: Double) {
        val me = LatLng(lat, lng)
        mMap.addMarker(MarkerOptions().position(me).title("i'm here!"))
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            val intent = Intent(packageContext, MapsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }
    }
}
