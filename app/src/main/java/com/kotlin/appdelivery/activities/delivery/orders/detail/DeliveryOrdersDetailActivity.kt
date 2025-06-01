package com.kotlin.appdelivery.activities.delivery.orders.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
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
import com.kotlin.appdelivery.activities.delivery.orders.map.DeliveryOrdersMapActivity
import com.kotlin.appdelivery.activities.restaurant.home.RestaurantHomeActivity
import com.kotlin.appdelivery.adapters.OrderProductsAdapter
import com.kotlin.appdelivery.adapters.OrdersClientAdapter
import com.kotlin.appdelivery.models.Category
import com.kotlin.appdelivery.models.Order
import com.kotlin.appdelivery.models.ResponseHttp
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.providers.OrdersProvider
import com.kotlin.appdelivery.providers.UsersProviders
import com.kotlin.appdelivery.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryOrdersDetailActivity : AppCompatActivity() {

    val TAG = "DeliverOrdersDetail"
    var order: Order? = null
    val gson = Gson()

    var toolbar: Toolbar? = null

    var texViewClient: TextView? = null
    var texViewAddress: TextView? = null
    var texViewData: TextView? = null
    var texViewTotal: TextView? = null
    var texViewStatus: TextView? = null
    var texViewDeliveryName: TextView? = null
    var recyclerViewProducts: RecyclerView? = null
    var buttonUpdate: Button? = null
    var buttonGoToMap: Button? = null

    var adapter: OrderProductsAdapter? = null

    var usersProvider: UsersProviders? = null
    var ordersProvider: OrdersProvider? = null
    var user: User? = null
    var sharePref: SharePref? = null


    var idDelivery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delivery_orders_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharePref = SharePref(this)

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)

        getUserFromSession()

        usersProvider = UsersProviders(user?.sessionToken!!)
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        toolbar?.title = "Order #${order?.id}"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        texViewClient = findViewById(R.id.textview_client)
        texViewAddress = findViewById(R.id.textview_address)
        texViewData = findViewById(R.id.textview_date)
        texViewTotal = findViewById(R.id.textview_total)
        texViewStatus = findViewById(R.id.textview_status)
        texViewDeliveryName = findViewById(R.id.textview_delivery_name)
        buttonUpdate = findViewById(R.id.btn_update)
        buttonGoToMap = findViewById(R.id.btn_go_to_map)

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

        if (order?.status == "DESPACHADO"){
            buttonUpdate?.visibility = View.VISIBLE
            texViewDeliveryName?.visibility = View.GONE
            buttonGoToMap?.visibility = View.GONE
        }

        buttonUpdate?.setOnClickListener{ updateOrder() }
        buttonGoToMap?.setOnClickListener{ goToMap() }
    }

    private fun updateOrder(){
        ordersProvider?.updateToOnTheWay(order!!)?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null){
                    Toast.makeText(this@DeliveryOrdersDetailActivity, "Entrega iniciada", Toast.LENGTH_SHORT).show()

                    if (response.body()?.isSuccess == true){
                        Toast.makeText(this@DeliveryOrdersDetailActivity, "Entrega iniciada", Toast.LENGTH_SHORT).show()
                        goToMap()
                    }else {
                        Toast.makeText(this@DeliveryOrdersDetailActivity, "No se iniciar la entrega", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DeliveryOrdersDetailActivity, "No hubo respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@DeliveryOrdersDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun goToMap() {
        val i = Intent(this, DeliveryOrdersMapActivity::class.java)
        i.putExtra("order", order?.toJson())
        startActivity(i)
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