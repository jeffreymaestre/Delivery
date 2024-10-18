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
import com.kotlin.appdelivery.activities.client.shoping_bag.ClientShopingBagActivity
import com.kotlin.appdelivery.activities.delivery.home.DeliveryHomeActivity
import com.kotlin.appdelivery.activities.restaurant.home.RestaurantHomeActivity
import com.kotlin.appdelivery.models.Category
import com.kotlin.appdelivery.models.Product
import com.kotlin.appdelivery.models.Rol
import com.kotlin.appdelivery.utils.SharePref

class ShopingBagAdapter(val context: Activity, val products: ArrayList<Product>): RecyclerView.Adapter<ShopingBagAdapter.ShopingBagViewHolder>() {

    val sharedPref = SharePref(context)

    init {
        (context as ClientShopingBagActivity).setTotal(getTotal())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopingBagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_shoping_bag, parent, false)
        return  ShopingBagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ShopingBagViewHolder, position: Int){
        val product = products[position] //Devuelve cada categoria

        holder.textViewName.text = product.name
        holder.textViewCounter.text = "${product.quantity}"
        holder.textViewPrice.text = "${product.price * product.quantity!!}$"
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)

        holder.imageViewAdd.setOnClickListener { addItem(product, holder) }
        holder.imageViewRemove.setOnClickListener { removeItem(product, holder) }
        holder.imageViewDelete.setOnClickListener { deleteItem(position) }

       // holder.itemView.setOnClickListener{ goToDetail(product) }
    }

    private fun getTotal(): Double{
        var total = 0.0
        for (p in products){
            total += (p.quantity!! * p.price)
        }
        return total
    }

    private fun goToDetail(product: Product){
            val i = Intent(context, ClientDetailActivity::class.java)
            i.putExtra("product", product.toJson())
            context.startActivity(i)
    }

    class ShopingBagViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textViewName: TextView
        val textViewPrice: TextView
        val textViewCounter: TextView
        val imageViewProduct: ImageView
        val imageViewAdd: ImageView
        val imageViewRemove: ImageView
        val imageViewDelete: ImageView

        init {
            textViewName = view.findViewById(R.id.textview_name)
            textViewPrice = view.findViewById(R.id.textview_price)
            textViewCounter = view.findViewById(R.id.textview_counter)
            imageViewProduct = view.findViewById(R.id.imageview_product)
            imageViewAdd =  view.findViewById(R.id.imageview_add)
            imageViewRemove = view.findViewById(R.id.imageview_remove)
            imageViewDelete = view.findViewById(R.id.imageview_delete)
        }
    }

    //METODO PARA COMPARAR SI UN PRODUCTO YA EXISTE EN SHARED PREF Y PODER EDITAR LA CANTIDAD DEL PRODUCTO SELECCIONADO
    private fun getIndexOf(idProduct: String): Int{
        var pos = 0
        for (p in products){
            if (p.id == idProduct){
                return pos
            }
            pos++
        }
        return -1
    }

    private fun addItem(product: Product, holder: ShopingBagViewHolder){
        val index = getIndexOf(product.id!!)
        product.quantity = product.quantity!! + 1
        products[index].quantity = product.quantity

        holder.textViewCounter.text = "${product?.quantity}"
        holder.textViewPrice.text = "${product.quantity!! * product.price}$"

        sharedPref.save("order", products)
        (context as ClientShopingBagActivity).setTotal(getTotal())
    }

    private fun removeItem(product: Product, holder: ShopingBagViewHolder){
        if (product.quantity!! > 1 ){
            val index = getIndexOf(product.id!!)
            product.quantity = product.quantity!! - 1
            products[index].quantity = product.quantity

            holder.textViewCounter.text = "${product?.quantity}"
            holder.textViewPrice.text = "${product.quantity!! * product.price}$"

            sharedPref.save("order", products)
            (context as ClientShopingBagActivity).setTotal(getTotal())
        }
    }

    private fun deleteItem(position: Int){
        products.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, products.size)
        sharedPref.save("order", products)
        (context as ClientShopingBagActivity).setTotal(getTotal())
    }
}