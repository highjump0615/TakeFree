package com.brainyapps.simplyfree.activities.main

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.utils.Globals
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryDataEventListener

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class HomeMapActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private val TAG = HomeMapActivity::class.java.simpleName

    private lateinit var mMap: GoogleMap

    private val aryLocation = ArrayList<GeoLocation>()

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

        mMap.setOnCameraIdleListener(this)
    }

    override fun onCameraIdle() {

        val visibleRegion = mMap.projection.visibleRegion

        val farRight = visibleRegion.farRight
        val farLeft = visibleRegion.farLeft
        val nearRight = visibleRegion.nearRight
        val nearLeft = visibleRegion.nearLeft

        val distanceWidth = FloatArray(2)
        Location.distanceBetween(
                (farRight.latitude + nearRight.latitude) / 2,
                (farRight.longitude + nearRight.longitude) / 2,
                (farLeft.latitude + nearLeft.latitude) / 2,
                (farLeft.longitude + nearLeft.longitude) / 2,
                distanceWidth
        )


        val distanceHeight = FloatArray(2)
        Location.distanceBetween(
                (farRight.latitude + nearRight.latitude) / 2,
                (farRight.longitude + nearRight.longitude) / 2,
                (farLeft.latitude + nearLeft.latitude) / 2,
                (farLeft.longitude + nearLeft.longitude) / 2,
                distanceHeight
        )

        var distance = distanceHeight[0]
        if (distanceWidth[0] > distanceHeight[0]) {
            distance = distanceWidth[0]
        }

        val location = mMap.cameraPosition.target;

        Log.e(TAG, "location: (${location.latitude}, ${location.longitude}), distance: $distance")

        //
        // query items
        //

        // geofire
        val geoFire = GeoFire(FirebaseDatabase.getInstance().getReference(Item.TABLE_NAME))
        val geoQuery = geoFire.queryAtLocation(GeoLocation(location.latitude, location.longitude), distance / 1000.0)
        geoQuery.addGeoQueryDataEventListener(object: GeoQueryDataEventListener {
            override fun onGeoQueryReady() {
                Log.d(TAG, "addGeoQueryEventListener:onGeoQueryReady")
            }

            override fun onDataExited(dataSnapshot: DataSnapshot?) {
                Log.d(TAG, "addGeoQueryEventListener:onDataExited $dataSnapshot")
            }

            override fun onDataChanged(dataSnapshot: DataSnapshot?, location: GeoLocation?) {
                Log.d(TAG, "addGeoQueryEventListener:onDataChanged$dataSnapshot")
            }

            override fun onDataEntered(dataSnapshot: DataSnapshot?, location: GeoLocation?) {
                Log.d(TAG, "addGeoQueryEventListener:onDataEntered$dataSnapshot")

                // check if existing
                for (lc in aryLocation) {
                    if (lc.latitude == location?.latitude && lc.longitude == location.longitude) {
                        return
                    }
                }

                addItem(dataSnapshot, location)
            }

            override fun onDataMoved(dataSnapshot: DataSnapshot?, location: GeoLocation?) {
                Log.d(TAG, "addGeoQueryEventListener:onDataMoved$dataSnapshot")
            }

            override fun onGeoQueryError(error: DatabaseError?) {
                Log.w(TAG, "addGeoQueryEventListener:failure", error?.toException())
            }

        })
    }

    private fun addItem(data: DataSnapshot?, location: GeoLocation?) {
        data?.let {
            val item = it.getValue(Item::class.java)
            item!!.id = it.key

            if (item.deletedAt != null) {
                // deleted item, skip it
                return
            }

            // add on map
            location?.let {
                val latlng = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(latlng).title(item.name))
            }
        }
    }
}
