package fi.oulu.hshi.reminderapp


import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    val zoom = 15f
    private lateinit var mMap: GoogleMap
    lateinit var selectedLocation: LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)




    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val currentCity = LatLng(65.01355297927051, 25.464019811372978)


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentCity, zoom))
        //mMap.addMarker(MarkerOptions().position(currentCity).title("Se"))

        setLongClick(mMap)
    }



    private fun setLongClick(googleMap: GoogleMap){
        googleMap.setOnMapLongClickListener {
            selectedLocation = LatLng(it.latitude, it.longitude)


            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, zoom))
            mMap.addMarker(MarkerOptions().position(selectedLocation).title("Se"))
            //applicationContext.getSharedPreferences(getString(R.string.sharedPreference), Context.MODE_PRIVATE).edit().putString("latitude", selectedLocation.latitude.toString()).apply()
            //applicationContext.getSharedPreferences(getString(R.string.sharedPreference), Context.MODE_PRIVATE).edit().putString("longitude", selectedLocation.longitude.toString()).apply()

            val intent = Intent()
            intent.putExtra("latitude", it.latitude)
            intent.putExtra("longitude", it.longitude)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }


    }
}