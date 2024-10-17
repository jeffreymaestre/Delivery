package com.kotlin.appdelivery.fragments.restaurant

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
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
import java.io.File
import java.util.ArrayList

class RestaurantProductFragment : Fragment() {

    val TAG = "ProductFragment"

    var myView: View? = null
    var editTextName: EditText? = null
    var editTextPrice: EditText? = null
    var editTextDescription: EditText? = null
    var imageViewProduct1: ImageView? = null
    var imageViewProduct2: ImageView? = null
    var imageViewProduct3: ImageView? = null
    var buttonCreate: Button? = null
    var spinnerCategories: Spinner? = null

    var imageFile1: File? = null
    var imageFile2: File? = null
    var imageFile3: File? = null

    var categoriesProvider: CategoriesProvider? = null
    var user: User? = null
    var sharePref: SharePref? = null
    var categories = ArrayList<Category>()
    var idCategory = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView =  inflater.inflate(R.layout.fragment_restaurant_product, container, false)

        editTextName = myView?.findViewById(R.id.edit_text_name)
        editTextPrice = myView?.findViewById(R.id.edit_text_price)
        editTextDescription = myView?.findViewById(R.id.edit_text_description)
        imageViewProduct1 = myView?.findViewById(R.id.imageview_image1)
        imageViewProduct2 = myView?.findViewById(R.id.imageview_image2)
        imageViewProduct3 = myView?.findViewById(R.id.imageview_image3)
        buttonCreate = myView?.findViewById(R.id.btn_create)
        spinnerCategories = myView?.findViewById(R.id.spinner_categories)

        buttonCreate?.setOnClickListener{ createProduct() }
        imageViewProduct1?.setOnClickListener{ selectImage(101) }
        imageViewProduct2?.setOnClickListener{ selectImage(102) }
        imageViewProduct3?.setOnClickListener{ selectImage(103) }

        sharePref = SharePref(requireActivity())

        getUserFromSession()

        categoriesProvider = CategoriesProvider(user?.sessionToken!!)

        getCategories()

        return myView
    }

    private fun isValidForm(name: String, description: String, price: String): Boolean {
        if (name.isNullOrBlank()){
            Toast.makeText(requireContext(), "Ingresa el nombre del producto", Toast.LENGTH_SHORT).show()
            return false
        }
        if (description.isNullOrBlank()){
            Toast.makeText(requireContext(), "Ingresa el precio del producto", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile1 == null){
            Toast.makeText(requireContext(), "Selecciona la imagen 1 del producto", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile2 == null){
            Toast.makeText(requireContext(), "Selecciona la imagen 2 del producto", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageFile3 == null){
            Toast.makeText(requireContext(), "Selecciona la imagen 3 del producto", Toast.LENGTH_SHORT).show()
            return false
        }
        if (idCategory.isNullOrBlank()){
            Toast.makeText(requireContext(), "Selecciona la categoria del producto", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun getCategories(){
        categoriesProvider?.getAll()?.enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>
            ) {
                if (response.body() != null){
                    categories = response.body()!!

                    val arrayAdapter = ArrayAdapter<Category>(requireActivity(), android.R.layout.simple_dropdown_item_1line, categories)
                    spinnerCategories?.adapter = arrayAdapter
                    spinnerCategories?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            adapterView: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            l: Long
                        ) {
                            idCategory = categories[position].id!!
                            Log.d(TAG, "Id category: ${idCategory}")
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }
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

    private fun createProduct(){
        val name = editTextName?.text.toString()
        val description = editTextDescription?.text.toString()
        val priceText = editTextPrice?.text.toString()

        if (isValidForm(name, description, priceText)){
            
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            //Image Uri will not be null for RESULT_OK
            if (requestCode == 101){
                imageFile1 = File(fileUri?.path) // Imagen que se va a guardar en el storage
                imageViewProduct1?.setImageURI(fileUri)
            }
           else if (requestCode == 102){
                imageFile2 = File(fileUri?.path) // Imagen que se va a guardar en el storage
                imageViewProduct2?.setImageURI(fileUri)
            }
            else if (requestCode == 103){
                imageFile3 = File(fileUri?.path) // Imagen que se va a guardar en el storage
                imageViewProduct3?.setImageURI(fileUri)
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImage(requestCode: Int){
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start(requestCode)
    }

}