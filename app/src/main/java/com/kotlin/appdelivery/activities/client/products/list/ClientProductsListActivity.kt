package com.kotlin.appdelivery.activities.client.products.list

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.adapters.ProductsAdapter
import com.kotlin.appdelivery.models.Product
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.providers.ProductsProvider
import com.kotlin.appdelivery.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ClientProductsListActivity : AppCompatActivity() {

    val TAG = "ClientProducts"
    var recyclerViewProducts: RecyclerView? = null
    var adapter: ProductsAdapter? = null
    var user: User? = null
    var sharePref: SharePref? = null
    var productsProvider: ProductsProvider? = null
    var products: ArrayList<Product> = ArrayList()

    var idCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_products_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharePref = SharePref(this)

        idCategory = intent.getStringExtra("idCategory")

        getUserFromSession()
        productsProvider = ProductsProvider(user?.sessionToken!!)

        recyclerViewProducts = findViewById(R.id.recycleview_products)
        recyclerViewProducts?.layoutManager = GridLayoutManager(this, 2)

        getProducts()

    }

    private fun getUserFromSession(){
        val gson = Gson()

        if (!sharePref?.getData("user").isNullOrBlank()){
            // VALIDO SI EL USUARIO EXISTE EN SESION
            user = gson.fromJson(sharePref?.getData("user"), User::class.java)
        }
    }

    private fun getProducts(){
        productsProvider?.findByCategory(idCategory!!)?.enqueue(object : Callback<ArrayList<Product>>{
            override fun onResponse(
                call: Call<ArrayList<Product>>,
                response: Response<ArrayList<Product>>
            ) {
                if (response.body() != null){
                    products = response.body()!!
                    adapter = ProductsAdapter(this@ClientProductsListActivity, products)
                    recyclerViewProducts?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
                Toast.makeText(this@ClientProductsListActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Error: ${t.message}")
            }

        });
    }
}