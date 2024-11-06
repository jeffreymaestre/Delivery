package com.kotlin.appdelivery.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.client.address.list.ClienteAddressListActivity
import com.kotlin.appdelivery.activities.client.home.ClientHomeActivity
import com.kotlin.appdelivery.activities.client.orders.detail.ClientOrdersDetailActivity
import com.kotlin.appdelivery.activities.client.products.list.ClientProductsListActivity
import com.kotlin.appdelivery.activities.delivery.home.DeliveryHomeActivity
import com.kotlin.appdelivery.activities.restaurant.home.RestaurantHomeActivity
import com.kotlin.appdelivery.models.Address
import com.kotlin.appdelivery.models.Category
import com.kotlin.appdelivery.models.Order
import com.kotlin.appdelivery.models.Rol
import com.kotlin.appdelivery.utils.SharePref

class OrdersClientAdapter(val context: Activity, val orders: ArrayList<Order>): RecyclerView.Adapter<OrdersClientAdapter.OrdersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_orders, parent, false)
        return  OrdersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int){
        val order = orders[position]

        // Configurar los textos de la dirección y el vecindario
        holder.textViewOrdersId.text = "Orden #${order.id}"
        holder.textViewDate.text = "${order.timestamp}"
        holder.textViewaddress.text = "${order.address?.address}"

        // Configurar el clic del item para cambiar la selección
        holder.itemView.setOnClickListener {
            goToOrderDetail(order)
        }
    }

    private fun goToOrderDetail(order: Order){
        val i = Intent(context, ClientOrdersDetailActivity::class.java)
        i.putExtra("order", order.toJson())
        context.startActivity(i)
    }

    class OrdersViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textViewOrdersId: TextView
        val textViewDate: TextView
        val textViewaddress: TextView

        init {
            textViewOrdersId = view.findViewById(R.id.textview_order_id)
            textViewDate = view.findViewById(R.id.textview_date)
            textViewaddress = view.findViewById(R.id.textview_address)
        }
    }
}