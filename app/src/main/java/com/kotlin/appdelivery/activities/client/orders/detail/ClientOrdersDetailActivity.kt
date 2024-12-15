package com.kotlin.appdelivery.activities.client.orders.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.client.orders.map.ClientOrdersMapActivity
import com.kotlin.appdelivery.activities.delivery.orders.map.DeliveryOrdersMapActivity
import com.kotlin.appdelivery.adapters.OrderProductsAdapter
import com.kotlin.appdelivery.adapters.OrdersClientAdapter
import com.kotlin.appdelivery.models.Order

class ClientOrdersDetailActivity : AppCompatActivity() {

    val TAG = "ClientOrderDetail"
    var order: Order? = null
    val gson = Gson()

    var toolbar: Toolbar? = null

    var texViewClient: TextView? = null
    var texViewAddress: TextView? = null
    var texViewData: TextView? = null
    var texViewTotal: TextView? = null
    var texViewStatus: TextView? = null
    var recyclerViewProducts: RecyclerView? = null
    var buttonGoToMap: Button? = null

    var adapter: OrderProductsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_orders_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)
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

        if (order?.status == "EN CAMINO"){
            buttonGoToMap?.visibility = View.VISIBLE
        }

        buttonGoToMap?.setOnClickListener{ goToMap() }
    }

    private fun goToMap() {
        val i = Intent(this, ClientOrdersMapActivity::class.java)
        i.putExtra("order", order?.toJson())
        startActivity(i)
    }

    private fun getTotal(){
        var total = 0.0
        for (p in order?.products!!){
            total = total + (p.price * p.quantity!!)
        }
        texViewTotal?.text = "${total}$"
    }
}