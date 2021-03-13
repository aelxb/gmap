package com.example.gmap

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.prefs.Preferences


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var curlocation: Location
    private val permisscode = 101
    private lateinit var mMap: GoogleMap
    private val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    private var countOfMarks = myPreferences.getInt("count", 0)
    private lateinit var marks: Array<LatLng>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
        val buttonAdd = findViewById<Button>(R.id.addBtn)
        buttonAdd.setOnClickListener{
            Add()
        }
    }
    private fun fetchLocation(){
        if(ActivityCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permisscode)
            return
        }
        val task = fusedLocation.lastLocation
        task.addOnSuccessListener { location ->
            if(location!=null){
                curlocation = location
                Toast.makeText(applicationContext, curlocation.latitude.toString()+""+curlocation.longitude.toString(), Toast.LENGTH_LONG).show()
                val supportFragment = (supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment?)!!
                supportFragment.getMapAsync(this)
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val nezhka = LatLng(curlocation.latitude, curlocation.longitude)
        val markerOptions = MarkerOptions().position(nezhka).title("Олды здесь")
        mMap?.addMarker(markerOptions)
        mMap?.animateCamera(CameraUpdateFactory.newLatLng(nezhka))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(nezhka, 5f))
        if(countOfMarks!=0) {
            for (i in 0 until countOfMarks) {
                val mark = LatLng((myPreferences.getString("lat$i", "0.0")).toString().toDouble(), (myPreferences.getString("lon$i", "0.0")).toString().toDouble())
                val markerOptions = MarkerOptions().position(mark)
                mMap?.addMarker(markerOptions)
            }
        }
    }
    private fun Add(){
        val longtitude = findViewById<TextView>(R.id.longitudeText)
        val latitude = findViewById<TextView>(R.id.latitudeText)
        val nezhka = LatLng((longtitude.text.toString()).toDouble(), (latitude.text.toString()).toDouble())
        val markerOptions = MarkerOptions().position(nezhka).title("Олды здесь")
        mMap?.addMarker(markerOptions)
        countOfMarks+=1
        myPreferences.edit().putInt("count", countOfMarks).apply()
        myPreferences.edit().putString("lat$countOfMarks", latitude.text.toString()).apply()
        myPreferences.edit().putString("lon$countOfMarks", longtitude.text.toString()).apply()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            permisscode->if(grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fetchLocation()
            }
        }
    }
}