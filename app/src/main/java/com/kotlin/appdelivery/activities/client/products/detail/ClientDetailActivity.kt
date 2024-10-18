package com.kotlin.appdelivery.activities.client.products.detail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.models.Product
import com.kotlin.appdelivery.utils.SharePref
import java.util.ArrayList

class ClientDetailActivity : AppCompatActivity() {
    val TAG = "ClientDetailActivity"

    var product: Product? = null
    val gson = Gson()

    var imageSlider: ImageSlider? = null
    var textViewName: TextView? = null
    var textViewDescription: TextView? = null
    var textViewPrice: TextView? = null
    var textViewCounter: TextView? = null
    var imageViewAdd: ImageView? = null
    var imageViewRemove: ImageView? = null
    var buttonAdd: Button? = null

    var counter = 1
    var productPrice = 0.0

    var sharePref : SharePref? = null
    var selectedProducts = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        product = gson.fromJson(intent.getStringExtra("product"), Product::class.java)
        sharePref = SharePref(this)

        imageSlider = findViewById(R.id.imageslider)
        textViewName = findViewById(R.id.textview_name)
        textViewDescription = findViewById(R.id.textview_description)
        textViewPrice = findViewById(R.id.textview_price)
        textViewCounter = findViewById(R.id.textview_counter)
        imageViewAdd = findViewById(R.id.imageview_add)
        imageViewRemove = findViewById(R.id.imageview_remove)
        buttonAdd = findViewById(R.id.btn_add_product)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(product?.image1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(product?.image2, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(product?.image3, ScaleTypes.CENTER_CROP))

        imageSlider?.setImageList(imageList)
        textViewName?.text = product?.name
        textViewDescription?.text = product?.description
        textViewPrice?.text = "${product?.price}$"

        imageViewAdd?.setOnClickListener { addItem() }
        imageViewRemove?.setOnClickListener{ removeItem() }
        buttonAdd?.setOnClickListener{ addToBag() }

        getProductsFromSharedPref()
    }

    private fun addToBag(){
        val index = getIndexOf(product?.id!!) //indice del producto si existe en shared pref
        if (index == -1){
            if (product?.quantity == 0){
                product?.quantity = 1
            }
            selectedProducts.add(product!!)
        }
        else {
            selectedProducts[index].quantity = counter
        }
        //selectedProducts.add(product!!)
        sharePref?.save("order", selectedProducts)
        Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
    }

    private fun getProductsFromSharedPref(){
        if (!sharePref?.getData("order").isNullOrBlank()){
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharePref?.getData("order"), type)
            val index = getIndexOf(product?.id!!)

            if (index != -1){
                product?.quantity = selectedProducts[index].quantity
                textViewCounter?.text = "${product?.quantity}"

                productPrice = product?.price!! * product?.quantity!!
                textViewPrice?.text = "${productPrice}$"
                buttonAdd?.setText("Editar producto")
                buttonAdd?.backgroundTintList = ColorStateList.valueOf(Color.RED)
            }

            for (p in selectedProducts){
                Log.d(TAG, "Shared pref: $p")
            }
        }
    }

    //METODO PARA COMPARAR SI UN PRODUCTO YA EXISTE EN SHARED PREF Y PODER EDITAR LA CANTIDAD DEL PRODUCTO SELECCIONADO
    private fun getIndexOf(idProduct: String): Int{
        var pos = 0
        for (p in selectedProducts){
            if (p.id == idProduct){
                return pos
            }
            pos++
        }
        return -1
    }

    private fun addItem(){
        counter++
        productPrice = product?.price!! * counter
        product?.quantity = counter
        textViewCounter?.text = "${product?.quantity}"
        textViewPrice?.text = "${productPrice}$"
    }

    private fun removeItem(){
        if (counter > 1){
            counter--
            productPrice = product?.price!! * counter
            product?.quantity = counter
            textViewCounter?.text = "${product?.quantity}"
            textViewPrice?.text = "${productPrice}$"
        }
    }
}