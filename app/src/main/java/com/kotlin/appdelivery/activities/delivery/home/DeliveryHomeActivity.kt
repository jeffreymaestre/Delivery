package com.kotlin.appdelivery.activities.delivery.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.MainActivity
import com.kotlin.appdelivery.fragments.client.ClientOrdersFragment
import com.kotlin.appdelivery.fragments.client.ClienteCategoriesFragment
import com.kotlin.appdelivery.fragments.client.ClienteProfileFragment
import com.kotlin.appdelivery.fragments.delivery.DeliveryOrdersFragment
import com.kotlin.appdelivery.fragments.restaurant.RestaurantCategoryFragment
import com.kotlin.appdelivery.fragments.restaurant.RestaurantOrdersFragment
import com.kotlin.appdelivery.fragments.restaurant.RestaurantProductFragment
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.utils.SharePref

class DeliveryHomeActivity : AppCompatActivity() {
    private val TAG = "DeliverytHomeActivity"
    //var butonLogout: Button? = null
    var sharedPerf:  SharePref? = null

    var bottonNavegation: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delivery_home)
        sharedPerf = SharePref(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //butonLogout = findViewById(R.id.btn_logout)
        //butonLogout?.setOnClickListener{ logout() }

        openFragment(DeliveryOrdersFragment())

        bottonNavegation = findViewById(R.id.bottom_navegation)
        bottonNavegation?.setOnItemSelectedListener {
            when(it.itemId){
                R.id.item_orders -> {
                    openFragment(DeliveryOrdersFragment())
                    true
                }
                R.id.item_profile -> {
                    openFragment(ClienteProfileFragment())
                    true
                }
                else -> false
            }
        }
        getUserFromSession()
    }

    private fun openFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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