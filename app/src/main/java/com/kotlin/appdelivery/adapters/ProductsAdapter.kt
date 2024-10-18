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
import com.kotlin.appdelivery.activities.client.home.ClientHomeActivity
import com.kotlin.appdelivery.activities.client.products.detail.ClientDetailActivity
import com.kotlin.appdelivery.activities.delivery.home.DeliveryHomeActivity
import com.kotlin.appdelivery.activities.restaurant.home.RestaurantHomeActivity
import com.kotlin.appdelivery.models.Category
import com.kotlin.appdelivery.models.Product
import com.kotlin.appdelivery.models.Rol
import com.kotlin.appdelivery.utils.SharePref

class ProductsAdapter(val context: Activity, val products: ArrayList<Product>): RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    val sharedPref = SharePref(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_product, parent, false)
        return  ProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int){
        val product = products[position] //Devuelve cada categoria

        holder.textViewName.text = product.name
        holder.textViewPrice.text = "${product.price}$"
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)

        holder.itemView.setOnClickListener{ goToDetail(product) }
    }

    private fun goToDetail(product: Product){
            val i = Intent(context, ClientDetailActivity::class.java)
            i.putExtra("product", product.toJson())
            context.startActivity(i)
    }

    class ProductsViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textViewName: TextView
        val textViewPrice: TextView
        val imageViewProduct: ImageView

        init {
            textViewName = view.findViewById(R.id.textview_name)
            textViewPrice = view.findViewById(R.id.textview_price)
            imageViewProduct = view.findViewById(R.id.imageview_product)
        }
    }
}