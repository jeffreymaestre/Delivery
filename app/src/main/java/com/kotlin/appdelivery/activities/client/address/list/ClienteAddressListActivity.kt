package com.kotlin.appdelivery.activities.client.address.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.client.address.create.ClientAddressCreateActivity
import com.kotlin.appdelivery.activities.client.payments.form.ClientPaymentsFormActivity
import com.kotlin.appdelivery.adapters.AddressAdapter
import com.kotlin.appdelivery.adapters.ShopingBagAdapter
import com.kotlin.appdelivery.models.Address
import com.kotlin.appdelivery.models.Order
import com.kotlin.appdelivery.models.Product
import com.kotlin.appdelivery.models.ResponseHttp
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.providers.AddressProvider
import com.kotlin.appdelivery.providers.OrdersProvider
import com.kotlin.appdelivery.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ClienteAddressListActivity : AppCompatActivity() {

    var fabCreateAddress: FloatingActionButton? = null
    var toolbar: Toolbar? = null

    var recyclerView: RecyclerView? = null
    var buttonNext: Button? = null
    var adapter:  AddressAdapter? = null
    var addressProvider: AddressProvider? = null
    var ordersProvider: OrdersProvider? = null
    var sharePref: SharePref? = null
    var user: User? = null

    var address = ArrayList<Address>()
    val gson = Gson()

    var selectedProducts = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cliente_address_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharePref = SharePref(this)
        getProductsFromSharedPref()

        fabCreateAddress = findViewById(R.id.fab_address_create)
        toolbar = findViewById(R.id.toolbar)
        buttonNext = findViewById(R.id.btn_next)
        recyclerView = findViewById(R.id.recyclerview_address)

        recyclerView?.layoutManager = LinearLayoutManager(this)

        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        toolbar?.title = "Mis direcciones"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getUserFromSession()
        addressProvider = AddressProvider(user?.sessionToken!!)
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        fabCreateAddress?.setOnClickListener{ goToAddressCreate() }

        getAddress()

        buttonNext?.setOnClickListener{ getAddressFromSession() }
    }

    private fun createOrder(idAddress: String){
        val order = Order(
            products = selectedProducts,
            idClient = user?.id!!,
            idAddress =idAddress
        )
        ordersProvider?.create(order)?.enqueue(object : Callback<ResponseHttp>{
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null){
                    Toast.makeText(this@ClienteAddressListActivity, "${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@ClienteAddressListActivity, "Ocurrio un errror en la peticion", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@ClienteAddressListActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getProductsFromSharedPref() {
        val orderData = sharePref?.getData("order")
        if (!orderData.isNullOrBlank()) {
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(orderData, type)
        }
    }

    fun resetValue(position: Int){
        val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position) // obtenemos una direccion
        val view = viewHolder?.itemView
        val imageViewCheck = view?.findViewById<ImageView>(R.id.imageview_check)
        imageViewCheck?.visibility = View.GONE
    }

    private fun getAddressFromSession(){
        if (!sharePref?.getData("address").isNullOrBlank()){
            val a = gson.fromJson(sharePref?.getData("address"), Address::class.java)
            createOrder(a.id!!)
        //goToPaymentsForms()
        }else{
            Toast.makeText(this, "Selecciona una direccion", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToPaymentsForms(){
        val i = Intent(this, ClientPaymentsFormActivity::class.java)
        startActivity(i)
    }

    private fun getAddress(){
        addressProvider?.getAddress(user?.id!!)?.enqueue(object : Callback<ArrayList<Address>>{
            override fun onResponse(
                call: Call<ArrayList<Address>>,
                response: Response<ArrayList<Address>>
            ) {
                if (response.body() != null){
                    address = response.body()!!
                    adapter = AddressAdapter(this@ClienteAddressListActivity, address)
                    recyclerView?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Address>>, t: Throwable) {
                Toast.makeText(this@ClienteAddressListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getUserFromSession(){
        val gson = Gson()

        if (!sharePref?.getData("user").isNullOrBlank()){
            // VALIDO SI EL USUARIO EXISTE EN SESION
            user = gson.fromJson(sharePref?.getData("user"), User::class.java)
        }
    }

    private fun goToAddressCreate(){
        val i = Intent(this, ClientAddressCreateActivity::class.java)
        startActivity(i)
    }
}