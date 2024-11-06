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
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.client.products.detail.ClientDetailActivity
import com.kotlin.appdelivery.activities.client.shoping_bag.ClientShopingBagActivity
import com.kotlin.appdelivery.models.Product
import com.kotlin.appdelivery.utils.SharePref

class OrderProductsAdapter(val context: Activity, val products: ArrayList<Product>): RecyclerView.Adapter<OrderProductsAdapter.OrderProductsViewHolder>() {

    val sharedPref = SharePref(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_order_products, parent, false)
        return OrderProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: OrderProductsViewHolder, position: Int){
        val product = products[position] //Devuelve cada categoria

        holder.textViewName.text = product.name

        if (product.quantity != null){
            holder.textViewQuantity.text = "${product.quantity!!}"
        }
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)
    }

    class OrderProductsViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imageViewProduct: ImageView
        val textViewName: TextView
        val textViewQuantity: TextView

        init {
            imageViewProduct = view.findViewById(R.id.imageview_product)
            textViewName = view.findViewById(R.id.textview_name)
            textViewQuantity = view.findViewById(R.id.textview_quantity)
        }
    }
}