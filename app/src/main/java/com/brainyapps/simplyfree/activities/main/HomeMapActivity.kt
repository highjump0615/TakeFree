package com.brainyapps.simplyfree.activities.main

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.utils.Globals
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryDataEventListener

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class HomeMapActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val TAG = HomeMapActivity::class.java.simpleName

    private lateinit var mMap: GoogleMap

    private val maryMarker = ArrayList<Marker>()
    private var geoQuery: GeoQuery? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setNavbar("", true)
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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true

            Globals.mLocation?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                val cameraUpdate = CameraUpdateFactory.newLatLng(latLng)
                mMap.animateCamera(cameraUpdate)
            }
        }

        mMap.setOnMarkerClickListener(this)

        // add item markers on the map
        for (item in Globals.items) {
            var latlng = LatLng(0.0, 0.0)

            item.location?.let {
                // add on map
                latlng = LatLng(it.latitude, it.longitude)
            }

            val marker = mMap.addMarker(MarkerOptions().position(latlng).title(item.name))

            // add to list
            maryMarker.add(marker)
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        // get item for the marker
        var item = Globals.items[0]
        for (i in 0 until maryMarker.size) {
            if (maryMarker[i] == marker) {
                item = Globals.items[i]
            }
        }

        // go to item detail page
        Globals.selectedItem = item
        val intent = Intent(this, ItemDetailActivity::class.java)
        startActivity(intent)

        return true
    }
}
