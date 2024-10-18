package com.kotlin.appdelivery.activities.client.address.create

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.client.address.map.ClientAddressMapActivity

class ClientAddressCreateActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null
    var editTextRefPoint: EditText? = null

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

        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        toolbar?.title = "Nueva direccion"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextRefPoint?.setOnClickListener{ goToAddresMap() }
    }

    private fun goToAddresMap(){
        val i = Intent(this, ClientAddressMapActivity::class.java)
        startActivity(i)
    }
}