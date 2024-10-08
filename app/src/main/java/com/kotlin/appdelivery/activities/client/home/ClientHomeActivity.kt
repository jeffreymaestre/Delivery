package com.kotlin.appdelivery.activities.client.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.MainActivity
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.utils.SharePref

class ClientHomeActivity : AppCompatActivity() {
    private val TAG = "ClientHomeActivity"
    var butonLogout: Button? = null
    var sharedPerf:  SharePref? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_home)
        sharedPerf = SharePref(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        butonLogout = findViewById(R.id.btn_logout)
        butonLogout?.setOnClickListener{ logout() }
        getUserFromSession()
    }

    private fun logout(){
        sharedPerf?.remove("user")
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun getUserFromSession(){
        val gson = Gson()

        if (!sharedPerf?.getData("user").isNullOrBlank()){
            // VALIDO SI EL USUARIO EXISTE EN SESION
            val user = gson.fromJson(sharedPerf?.getData("user"), User::class.java)
            Log.d(TAG, "Usuario: ${user}")
        }
    }
}