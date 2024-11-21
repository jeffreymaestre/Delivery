package com.kotlin.appdelivery.activities.restaurant.orders.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.adapters.OrderProductsAdapter
import com.kotlin.appdelivery.adapters.OrdersClientAdapter
import com.kotlin.appdelivery.models.Category
import com.kotlin.appdelivery.models.Order
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.providers.UsersProviders
import com.kotlin.appdelivery.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantOrdersDetailActivity : AppCompatActivity() {

    val TAG = "RestaurantOrdersDetail"
    var order: Order? = null
    val gson = Gson()

    var toolbar: Toolbar? = null

    var texViewClient: TextView? = null
    var texViewAddress: TextView? = null
    var texViewData: TextView? = null
    var texViewTotal: TextView? = null
    var texViewStatus: TextView? = null
    var recyclerViewProducts: RecyclerView? = null

    var adapter: OrderProductsAdapter? = null

    var usersProvider: UsersProviders? = null
    var user: User? = null
    var sharePref: SharePref? = null

    var spinnerDeliveryMen: Spinner? = null
    var idDelivery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_restaurant_orders_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharePref = SharePref(this)

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)

        getUserFromSession()

        usersProvider = UsersProviders(user?.sessionToken!!)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Order #${order?.id}"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        texViewClient = findViewById(R.id.textview_client)
        texViewAddress = findViewById(R.id.textview_address)
        texViewData = findViewById(R.id.textview_date)
        texViewTotal = findViewById(R.id.textview_total)
        texViewStatus = findViewById(R.id.textview_status)
        spinnerDeliveryMen = findViewById(R.id.spinner_delivery_men)

        recyclerViewProducts = findViewById(R.id.recyclerview_products)
        recyclerViewProducts?.layoutManager = LinearLayoutManager(this)

        adapter = OrderProductsAdapter(this, order?.products!!)
        recyclerViewProducts?.adapter = adapter

        texViewClient?.text = "${order?.client?.name} ${order?.client?.lastname}"
        texViewAddress?.text = order?.address?.address
        texViewData?.text = "${order?.timestamp}"
        texViewStatus?.text = order?.status

        Log.d(TAG, "Orden: ${order.toString()}")

        getTotal()
        getDeliveryMen()
    }

    private fun getDeliveryMen(){
        usersProvider?.getDeliveryMen()?.enqueue(object: Callback<ArrayList<User>>{
            override fun onResponse(
                call: Call<ArrayList<User>>,
                response: Response<ArrayList<User>>
            ) {
                if (response.body() != null){
                    val deliverymen = response.body()

                    val arrayAdapter = ArrayAdapter<User>(this@RestaurantOrdersDetailActivity, android.R.layout.simple_dropdown_item_1line, deliverymen!!)
                    spinnerDeliveryMen?.adapter = arrayAdapter
                    spinnerDeliveryMen?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            adapterView: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            l: Long
                        ) {
                            idDelivery = deliverymen[position].id!!
                            Log.d(TAG, "Id delivery: ${idDelivery}")
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
                Toast.makeText(this@RestaurantOrdersDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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

    private fun getTotal(){
        var total = 0.0
        for (p in order?.products!!){
            total = total + (p.price * p.quantity!!)
        }
        texViewTotal?.text = "${total}$"
    }
}