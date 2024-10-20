package com.kotlin.appdelivery.activities.client.shoping_bag

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.client.address.create.ClientAddressCreateActivity
import com.kotlin.appdelivery.activities.client.address.list.ClienteAddressListActivity
import com.kotlin.appdelivery.adapters.ShopingBagAdapter
import com.kotlin.appdelivery.models.Product
import com.kotlin.appdelivery.utils.SharePref
import java.util.ArrayList

class ClientShopingBagActivity : AppCompatActivity() {
    var recyclerViewShopingBag: RecyclerView? = null
    var textViewTotal: TextView? = null
    var buttonNext: Button? = null
    var toolbar: Toolbar? = null

    var adapter: ShopingBagAdapter? = null
    var sharePref: SharePref? = null
    var gson = Gson()
    var selectedProducts = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_shoping_bag)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharePref = SharePref(this)

        recyclerViewShopingBag = findViewById(R.id.recycleview_shoping_bag)
        textViewTotal = findViewById(R.id.textview_total)
        buttonNext = findViewById(R.id.btn_next)
        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        toolbar?.title = "Tu orden"

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerViewShopingBag?.layoutManager = LinearLayoutManager(this)

        getProductsFromSharedPref()
        buttonNext?.setOnClickListener{ goToAddressList() }

    }

    private fun goToAddressList(){
        val i = Intent(this, ClienteAddressListActivity::class.java)
        startActivity(i)
    }

    fun setTotal(total: Double) {
        textViewTotal?.text = "${total}$"
    }

    /*private fun getProductsFromSharedPref() {
        if (!sharePref?.getData("order").isNullOrBlank()) {
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharePref?.getData("order"), type)

            adapter = ShopingBagAdapter(this, selectedProducts)
            recyclerViewShopingBag?.adapter = adapter

        }
    }*/

    private fun getProductsFromSharedPref() {
        val orderData = sharePref?.getData("order")
        if (!orderData.isNullOrBlank()) {
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(orderData, type)

            // Asegurarse de que la lista no esté vacía antes de asignarla al adaptador
            if (selectedProducts.isNotEmpty()) {
                adapter = ShopingBagAdapter(this, selectedProducts)
                recyclerViewShopingBag?.adapter = adapter
            } else {
                // Manejar caso cuando no hay productos en el carrito
                setTotal(0.0)
            }
        } else {
            // Si no hay datos, manejar el caso
            setTotal(0.0)
        }
    }

}