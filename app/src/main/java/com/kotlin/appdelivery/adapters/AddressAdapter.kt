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
import com.kotlin.appdelivery.activities.client.products.list.ClientProductsListActivity
import com.kotlin.appdelivery.activities.delivery.home.DeliveryHomeActivity
import com.kotlin.appdelivery.activities.restaurant.home.RestaurantHomeActivity
import com.kotlin.appdelivery.models.Address
import com.kotlin.appdelivery.models.Category
import com.kotlin.appdelivery.models.Rol
import com.kotlin.appdelivery.utils.SharePref

class AddressAdapter(val context: Activity, val address: ArrayList<Address>): RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    val sharedPref = SharePref(context)
    val gson = Gson()
    var prev = 0
    var positionAddresSession = 0
    private var selectedPosition = 1 // Posición seleccionada por defecto (ninguna)


    init {
        // Inicializa con la dirección guardada en SharedPreferences (si existe)
        restoreSelectedAddress()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_address, parent, false)
        return  AddressViewHolder(view)
    }

    override fun getItemCount(): Int {
        return address.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int){
        val ad = address[position]

        if(!sharedPref.getData("address").isNullOrBlank()){
            val adr = gson.fromJson(sharedPref.getData("address"), Address::class.java)
            if (adr.id == ad.id){
                positionAddresSession = position
                holder.imageViewCheck.visibility = View.VISIBLE
            }
        }

        // Configurar los textos de la dirección y el vecindario
        holder.textViewAddress.text = ad.address
        holder.textViewNeighbor.text = ad.neighborhood

        // Mostrar o esconder el check basado en la posición seleccionada
        holder.imageViewCheck.visibility = if (selectedPosition == position) View.VISIBLE else View.GONE

        // Configurar el clic del item para cambiar la selección
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            // Notificar que las posiciones han cambiado (descheckear el anterior y checkear el nuevo)
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            // Guardar la dirección seleccionada en SharedPreferences
            saveAddress(ad.toJson())
        }
    }

    // Función para restaurar la selección desde SharedPreferences
    fun restoreSelectedAddress() {
        val savedAddress = sharedPref.getData("address")
        if (!savedAddress.isNullOrBlank()) {
            val adr = gson.fromJson(savedAddress, Address::class.java)
            selectedPosition = address.indexOfFirst { it.id == adr.id }
        } else {
            selectedPosition = -1 // Ninguna dirección seleccionada si no hay nada guardado
        }
    }

    private fun saveAddress(data: String){
        val ad = gson.fromJson(data, Address::class.java)
        sharedPref.save("address", ad)
    }

    class AddressViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textViewAddress: TextView
        val textViewNeighbor: TextView
        val imageViewCheck: ImageView

        init {
            textViewAddress = view.findViewById(R.id.textview_address)
            textViewNeighbor = view.findViewById(R.id.textview_neighborhood)
            imageViewCheck = view.findViewById(R.id.imageview_check)
        }
    }
}