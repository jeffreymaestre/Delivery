package com.kotlin.appdelivery.activities.client.address.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.kotlin.appdelivery.R
import java.net.URI.create

class ClientAddressMapActivity : AppCompatActivity(), OnMapReadyCallback {

    val TAG = "ClientAddress"
    var googleMap: GoogleMap? = null

    val PERMISSION_ID = 97
    var fusedLocationClient: FusedLocationProviderClient? = null

    var texviewAddress: TextView? = null
    var buttonAcept: Button? = null

    var city = ""
    var country = ""
    var address = ""
    var addresLatLong: LatLng? = null

    private val locationCallback = object: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation = locationResult.lastLocation
            Log.d("LOCALIZACION",  "Callback: ${lastLocation}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_address_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        texviewAddress = findViewById(R.id.textview_address)
        buttonAcept = findViewById(R.id.btn_acept)

        getLastLocation()

        buttonAcept?.setOnClickListener{ goToCreateAddress() }
    }

    private fun goToCreateAddress(){
        val i = Intent()
        i.putExtra("city", city)
        i.putExtra("address", address)
        i.putExtra("country", country)
        i.putExtra("lat", addresLatLong?.latitude)
        i.putExtra("lng", addresLatLong?.longitude)
        setResult(RESULT_OK, i)
        finish() // Volver hacia atras
    }

    private fun onCameraMove(){
        googleMap?.setOnCameraIdleListener {
            try {
                val geocoder = Geocoder(this)
                addresLatLong = googleMap?.cameraPosition?.target
                val addressList = geocoder.getFromLocation(addresLatLong?.latitude!!, addresLatLong?.longitude!!, 1)
                city = addressList!![0].locality //Ciudad
                country = addressList[0].countryName
                address = addressList[0].getAddressLine(0)

                texviewAddress?.text = "$address $city"
            }catch (e: Exception) {
                Log.d(TAG, "Error: ${e.message}")
            }
        }
    }

    private fun getLastLocation() {
        if (checkPermision()) {
            if (isLocationEnabled()) {
                // Verificamos nuevamente los permisos antes de acceder a la ubicación
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Solicitamos los permisos si no están otorgados
                    requestPermision()
                    return
                }

                // Obtener la última ubicación si ya tenemos los permisos
                fusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        googleMap?.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder().target(
                                    LatLng(location.latitude, location.longitude)
                                ).zoom(15f).build()
                            )
                        )
                    }
                }
            } else {
                Toast.makeText(this, "Habilita la localización", Toast.LENGTH_SHORT).show()
                val i = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(i)
            }
        } else {
            requestPermision()
        }
    }


    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

    }

    private fun isLocationEnabled(): Boolean{
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermision(): Boolean{
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        return false
    }

    private fun requestPermision(){
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso fue concedido
                getLastLocation()
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        onCameraMove()
    }
}