package com.kotlin.appdelivery.fragments.client

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.adapters.CategoriesAdapter
import com.kotlin.appdelivery.models.Category
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.providers.CategoriesProvider
import com.kotlin.appdelivery.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ClienteCategoriesFragment : Fragment() {

    val TAG = "CategoriesFragment"

    var myView : View? = null
    var recyclerViewCategories: RecyclerView? = null
    var adapter: CategoriesAdapter? = null
    var categoriesProvider: CategoriesProvider? = null
    var user: User? = null
    var sharePref: SharePref? = null
    var categories = ArrayList<Category>()
    var toolbar: Toolbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_cliente_categories, container, false)

        toolbar = myView?.findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        toolbar?.title = "Categorias"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        recyclerViewCategories = myView?.findViewById(R.id.recycleview_categories)
        recyclerViewCategories?.layoutManager = LinearLayoutManager(requireContext()) // ELEMENTOS SE MOSTRARAN DE MANERA VERTICAL "UNO DEBAJO DEL OTRO"

        sharePref = SharePref(requireActivity())

        getUserFromSession()

        categoriesProvider = CategoriesProvider(user?.sessionToken!!)

        getCategories()

        return  myView
    }

    private fun getCategories(){
        categoriesProvider?.getAll()?.enqueue(object : Callback<ArrayList<Category>>{
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>
            ) {
               if (response.body() != null){
                   categories = response.body()!!
                   adapter = CategoriesAdapter(requireActivity(), categories)
                   recyclerViewCategories?.adapter = adapter
               }
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                Log.d(TAG, "Error: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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
}