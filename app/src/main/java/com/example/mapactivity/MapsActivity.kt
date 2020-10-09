package com.example.mapactivity

import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private  val TAG = MapsActivity::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1



    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        isPermissionGranted()
    }

    private fun isPermissionGranted():Boolean{
        return ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        return true


    }


    override fun onOptionsItemSelected(item: MenuItem):Boolean = when(item.itemId)  {
        R.id.Normal_map ->{
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }

        R.id.hybrid ->{
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true

        }
        R.id.satellite ->{
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
             true
        }
        R.id.terrain ->{
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
             true
        }


        else ->
            super.onOptionsItemSelected(item)
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
    override  fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val latitude = 8.6753
        val longitude = 9.0820
        val zoomLevel = 15f
        val homeLatLng = LatLng(latitude,longitude)
        val overlaySize = 100f
        val androidOverlay = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.android))
            .position(homeLatLng, overlaySize)
        map.addGroundOverlay(androidOverlay)





        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng,zoomLevel))
        map.addMarker(MarkerOptions().position(homeLatLng))

        setMapLOngClick(map)
        setPoiCLick(map)
        setMapStyle(map)

        enableMyLocation()
    }


    private fun setMapLOngClick(map: GoogleMap){
        map.setOnMapLongClickListener {  latLng ->

           val snippet = String.format(
                Locale.getDefault(),
               "Lat:%1%$.5f, Long:%2$.5f",
                latLng.latitude,
               latLng.longitude


                )
            map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(getString(R.string.dropped_pin))
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
          }

    }

private fun enableMyLocation(){
    if (isPermissionGranted()){
        map.isMyLocationEnabled= true
    }
    else{
        ActivityCompat.requestPermissions(this, arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_LOCATION_PERMISSION)
    }
}

private  fun  setPoiCLick(map: GoogleMap){
    map.setOnPoiClickListener { poi->
        val  poiMarker = map.addMarker(
            MarkerOptions()

                .position(poi.latLng)
                .title(poi.name)
        )
        poiMarker.showInfoWindow()
    }
}
private fun setMapStyle(map:GoogleMap){

    try {
        val success = map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style)
        )
        if(!success){
            Log.e(TAG,"Style parsing failed")
         }

    }catch (e:Resources.NotFoundException){
        Log.e(TAG,"Can't find style. Error ",e)
    }




}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        if (requestCode == REQUEST_LOCATION_PERMISSION){
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)){
                enableMyLocation()
            }
        }
    }
}
