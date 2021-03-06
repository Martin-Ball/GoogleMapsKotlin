package com.martin.googlemapskotlin

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color.red
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map:GoogleMap

    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createFragment()

    }

    private fun createFragment() {
        val mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //createMarker()
        createPolylines()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableLocation()
    }

    private fun createMarker() {
        val coordinates = LatLng(-32.946235, -60.679548)
        val marker = MarkerOptions().position(coordinates).title("Casa")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            4000,
            null
        )
    }

    private fun createPolylines(){

        //https://geojson.io/#map=2/20.0/0.0

      val polylineOptions = PolylineOptions()
          .add(LatLng(40.419173113350965,-3.705976009368897))
          .add(LatLng( 40.4150807746539, -3.706072568893432))
          .add(LatLng( 40.41517062907432, -3.7012016773223873))
          .add(LatLng( 40.41713105928677, -3.7037122249603267))
          .add(LatLng( 40.41926296230622,  -3.701287508010864))
          .add(LatLng( 40.419173113350965, -3.7048280239105225))
          .width(15f)
          .color(ContextCompat.getColor(this, R.color.kotlin))

      val polyline = map.addPolyline(polylineOptions)

        polyline.startCap = RoundCap()
        polyline.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.dog))

      val pattern = listOf(
          Dot(), Gap(10f), Dash(50f), Gap(10f)
      )
        polyline.pattern = pattern

      polyline.isClickable = true

      map.setOnPolylineClickListener { polyline -> changeColor(polyline) }
    }

    fun changeColor(polyline: Polyline){
        val color:Int = (0..3).random()
        when(color){
            0 -> polyline.color = ContextCompat.getColor(this, R.color.red)
            1 -> polyline.color = ContextCompat.getColor(this, R.color.blue)
            2 -> polyline.color = ContextCompat.getColor(this, R.color.yellow)
            3 -> polyline.color = ContextCompat.getColor(this, R.color.green)
        }
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation(){
        if(!::map.isInitialized) return
        if(isLocationPermissionGranted()){
            map.isMyLocationEnabled = true
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Acepta los permisos en ajustes", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Acepta los permisos en ajustes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!::map.isInitialized) return
        if(!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localizacion ve a ajustes", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false //false lo lleva a la localizacion, true deja implementar algo
    }

    override fun onMyLocationClick(p0: Location) { //se llama cada vez que se pulse en el circulo azul donde esta la localizacion
        Toast.makeText(this, "Estas en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }
}