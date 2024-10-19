package com.kotlin.appdelivery.activities.client.address.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.client.address.map.ClientAddressMapActivity

class ClientAddressCreateActivity : AppCompatActivity() {

    val TAG = "ClientAddressCreate"

    var toolbar: Toolbar? = null
    var editTextRefPoint: EditText? = null
    var editTextAddress: EditText? = null
    var editTextNeighbor: EditText? = null
    var btnCreateAddress: Button? = null

    var addresLat = 0.0
    var addressLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_address_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar = findViewById(R.id.toolbar)
        editTextRefPoint = findViewById(R.id.edit_text_ref_point)
        editTextAddress = findViewById(R.id.edit_text_address)
        editTextNeighbor = findViewById(R.id.edit_text_neighborhood)
        btnCreateAddress = findViewById(R.id.btn_create_address)

        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        toolbar?.title = "Nueva direccion"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextRefPoint?.setOnClickListener{ goToAddresMap() }
    }

    private fun createAddress(){
        val address = editTextAddress?.text.toString()
        val neighbor = editTextNeighbor?.text.toString()

        if (isValidForm(address, neighbor)){
            //Lanzar la peticion
        }

    }

    private fun isValidForm(address: String, neighbor: String): Boolean {
        if (address.isNullOrBlank()){
            Toast.makeText(this, "Ingresa tu direccion", Toast.LENGTH_SHORT).show()
            return false
        }
        if (neighbor.isNullOrBlank()){
            Toast.makeText(this, "Ingresa tu barrio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (addresLat == 0.0){
            Toast.makeText(this, "Selecciona el punto de referencia", Toast.LENGTH_SHORT).show()
            return false
        }
        if (addressLng == 0.0){
            Toast.makeText(this, "Selecciona el punto de referencia", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    var resultLaucher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if(result.resultCode == Activity.RESULT_OK){
            val data = result.data
            val city = data?.getStringExtra("city")
            val address = data?.getStringExtra("address")
            val country = data?.getStringExtra("country")
            addresLat = data?.getDoubleExtra("lat", 0.0)!!
            addressLng = data?.getDoubleExtra("lng", 0.0)!!

            editTextRefPoint?.setText("$address $city")
            Log.d(TAG, "City: $city")
            Log.d(TAG, "Address: $address")
            Log.d(TAG, "Country: $country")
            Log.d(TAG, "Lat: $addresLat")
            Log.d(TAG, "Lng: $addressLng")
        }
    }

    private fun goToAddresMap(){
        val i = Intent(this, ClientAddressMapActivity::class.java)
        //startActivity(i)
        resultLaucher.launch(i)
    }
}