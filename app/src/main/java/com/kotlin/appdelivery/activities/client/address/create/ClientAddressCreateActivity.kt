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
import com.google.gson.Gson
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.client.address.list.ClienteAddressListActivity
import com.kotlin.appdelivery.activities.client.address.map.ClientAddressMapActivity
import com.kotlin.appdelivery.models.Address
import com.kotlin.appdelivery.models.ResponseHttp
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.providers.AddressProvider
import com.kotlin.appdelivery.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientAddressCreateActivity : AppCompatActivity() {

    val TAG = "ClientAddressCreate"

    var toolbar: Toolbar? = null
    var editTextRefPoint: EditText? = null
    var editTextAddress: EditText? = null
    var editTextNeighbor: EditText? = null
    var btnCreateAddress: Button? = null

    var addresLat = 0.0
    var addressLng = 0.0

    var addressProvider: AddressProvider? = null
    var sharePref: SharePref? = null
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_address_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharePref = SharePref(this)

        getUserFromSession()
        addressProvider = AddressProvider(user?.sessionToken!!)

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
        btnCreateAddress?.setOnClickListener{ createAddress()}
    }

    private fun getUserFromSession(){
        val gson = Gson()

        if (!sharePref?.getData("user").isNullOrBlank()){
            // VALIDO SI EL USUARIO EXISTE EN SESION
            user = gson.fromJson(sharePref?.getData("user"), User::class.java)
        }
    }

    private fun createAddress(){
        val address = editTextAddress?.text.toString()
        val neighbor = editTextNeighbor?.text.toString()

        if (isValidForm(address, neighbor)){
            //Lanzar la peticion
            val addressModel = Address(
                address = address,
                neighborhood = neighbor,
                idUser = user?.id!!,
                lat = addresLat,
                lng = addressLng
            )
            addressProvider?.create(addressModel)?.enqueue(object : Callback<ResponseHttp>{
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    if (response.body() != null){
                        Toast.makeText(this@ClientAddressCreateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                        goToAddressList()
                    } else{
                        Toast.makeText(this@ClientAddressCreateActivity, "Ocurrio un error en la peticion", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(this@ClientAddressCreateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }

    }

    private fun goToAddressList(){
        val i = Intent(this, ClienteAddressListActivity::class.java)
        startActivity(i)
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