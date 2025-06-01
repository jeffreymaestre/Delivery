package com.kotlin.appdelivery.activities.client.shoping_bag

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import com.kotlin.appdelivery.activities.client.products.list.ClientProductsListActivity
import com.kotlin.appdelivery.adapters.ShopingBagAdapter
import com.kotlin.appdelivery.models.Address
import com.kotlin.appdelivery.models.Order
import com.kotlin.appdelivery.models.Product
import com.kotlin.appdelivery.utils.SharePref
import java.util.ArrayList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.kotlin.appdelivery.models.ResponseHttp
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.providers.OrdersProvider

class ClientShopingBagActivity : AppCompatActivity() {
    var recyclerViewShopingBag: RecyclerView? = null
    var textViewTotal: TextView? = null
    var buttonNext: Button? = null
    var toolbar: Toolbar? = null

    var adapter: ShopingBagAdapter? = null
    var sharePref: SharePref? = null
    var gson = Gson()
    var selectedProducts = ArrayList<Product>()
    var user: User? = null
    var ordersProvider: OrdersProvider? = null

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
        getUserFromSession()
        ordersProvider = OrdersProvider(user?.sessionToken!!)
        buttonNext?.setOnClickListener{ getAddressFromSharedPref() }

    }

    private fun getUserFromSession(){
        val gson = Gson()

        if (!sharePref?.getData("user").isNullOrBlank()){
            // VALIDO SI EL USUARIO EXISTE EN SESION
            user = gson.fromJson(sharePref?.getData("user"), User::class.java)
        }
    }

private fun clearShopingBag(){
    sharePref?.remove("order")
    selectedProducts.clear()
    adapter?.notifyDataSetChanged()
    setTotal(0.0)
    getProductsFromSharedPref()
}


    private fun createOrder(idAddress: String) {
        if (selectedProducts.isEmpty()) {
            // Mostrar un mensaje al usuario si la bolsa está vacía
            Toast.makeText(this@ClientShopingBagActivity, "Tu bolsa está vacía. Añade productos antes de confirmar.", Toast.LENGTH_SHORT).show()
            //goToProductList()
            //buttonNext?.visibility = View.GONE
            return // No se continúa con la creación de la orden
        }
        val order = Order(
            products = selectedProducts,
            idClient = user?.id!!,
            idAddress = idAddress,
            status = "PAGADO"
        )
        ordersProvider?.create(order)?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null) {
                    Toast.makeText(this@ClientShopingBagActivity, "${response.body()?.message}", Toast.LENGTH_SHORT).show()
                    clearShopingBag()
                // Aquí podrías redirigir a la pantalla de pago o alguna otra
                } else {
                    Toast.makeText(this@ClientShopingBagActivity, "Ocurrió un error en la petición", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@ClientShopingBagActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getAddressFromSharedPref() {
        if (!sharePref?.getData("address").isNullOrBlank()) {
            val address = gson.fromJson(sharePref?.getData("address"), Address::class.java)
            createOrder(address.id!!)
        } else {
            // Crear la orden con la dirección por defecto
            //Toast.makeText(this, "Usando direccion por defecto", Toast.LENGTH_SHORT).show()
            createOrder("8")
            //Toast.makeText(this, "No tienes dirección seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToAddressList(){
        val i = Intent(this, ClienteAddressListActivity::class.java)
        startActivity(i)
    }

    private fun goToProductList(){
        val i = Intent(this@ClientShopingBagActivity, ClientProductsListActivity::class.java)
        startActivity(i)
        finish()
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