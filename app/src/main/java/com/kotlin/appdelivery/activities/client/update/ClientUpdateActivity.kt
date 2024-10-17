package com.kotlin.appdelivery.activities.client.update

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.models.ResponseHttp
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.providers.UsersProviders
import com.kotlin.appdelivery.utils.SharePref
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ClientUpdateActivity : AppCompatActivity() {
    val TAG = "ClientUpdateActivity"

    var circleImageUser: CircleImageView? = null
    var editTextName: EditText? = null
    var editTextLastName: EditText? = null
    var editTextPhone: EditText? = null
    var buttonUpdate: Button? = null

    var sharedPref: SharePref? = null
    var user: User? = null

    private var imageFile: File? = null

    var usersProvider: UsersProviders? = null

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_update)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPref = SharePref(this)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.title = "Editar perfil"
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        circleImageUser = findViewById(R.id.circleimage_user)
        editTextName = findViewById(R.id.edit_text_name)
        editTextLastName = findViewById(R.id.edit_text_last_name)
        editTextPhone = findViewById(R.id.edit_text_phone)
        buttonUpdate = findViewById(R.id.btn_update)

        getUserFromSession()

        usersProvider = UsersProviders(user?.sessionToken)

        editTextName?.setText(user?.name)
        editTextLastName?.setText(user?.lastname)
        editTextPhone?.setText(user?.phone)

        if (!user?.image.isNullOrBlank()){
            Glide.with(this).load(user?.image).into(circleImageUser!!)
        }

        circleImageUser?.setOnClickListener{ selectImage() }
        buttonUpdate?.setOnClickListener{ updateData() }
    }

    private fun updateData(){
        val name = editTextName?.text.toString()
        val lastname = editTextLastName?.text.toString()
        val phone = editTextPhone?.text.toString()

        user?.name = name
        user?.lastname = lastname
        user?.phone = phone

        if (imageFile != null){
            usersProvider?.update(imageFile!!, user!!)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    Log.d(TAG, "RESPONSE: ${response}")
                    Log.d(TAG, "BODY: ${response.body()}")

                    if (response.body()?.isSuccess == true){
                        response.body()?.data?.let {
                            saveUserInSession(it.toString())
                        }

                        // Limpiar la caché de Glide después de que se haya guardado el usuario actualizado
                        Glide.get(this@ClientUpdateActivity).clearMemory()
                        Thread {
                            Glide.get(this@ClientUpdateActivity).clearDiskCache()
                        }.start()
                    }
                    Toast.makeText(this@ClientUpdateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()

                    //saveUserInSession(response.body()?.data.toString())
                    //refreshUserData()
                    /*val intent = intent
                    finish()
                    startActivity(intent)*/

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    Toast.makeText(this@ClientUpdateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        } else {
            usersProvider?.updateWithoutImage(user!!)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    Log.d(TAG, "RESPONSE: ${response}")
                    Log.d(TAG, "BODY: ${response.body()}")

                    if (response.body()?.isSuccess == true){
                        response.body()?.data?.let {
                            saveUserInSession(it.toString())
                        }

                        // Limpiar la caché de Glide después de que se haya guardado el usuario actualizado
                        Glide.get(this@ClientUpdateActivity).clearMemory()
                        Thread {
                            Glide.get(this@ClientUpdateActivity).clearDiskCache()
                        }.start()
                    }

                    Toast.makeText(this@ClientUpdateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()

                    //saveUserInSession(response.body()?.data.toString())
                    //refreshUserData()
                   /* val intent = intent
                    finish()
                    startActivity(intent)*/

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    Toast.makeText(this@ClientUpdateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }



    }

    private fun saveUserInSession(data: String){
        val gson = Gson()
        val updateUser = gson.fromJson(data, User::class.java)
        sharedPref?.save("user", updateUser)
        user = updateUser

        Log.d(TAG, "Nueva URL de imagen guardada en sesión: ${user?.image}")

        Glide.get(this).clearMemory()
        Thread {
            Glide.get(this).clearDiskCache()
        }.start()
        //refreshUserData()
        val savedUser = sharedPref?.getData("user")
        Log.d(TAG, "Datos del usuario en SharedPreferences después de actualizar: $savedUser")

    }

    private fun getUserFromSession(){
        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()){
            // VALIDO SI EL USUARIO EXISTE EN SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private val startImageForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        if (resultCode == Activity.RESULT_OK){
            val fileUri = data?.data
            imageFile = File(fileUri?.path) // Imagen que se va a guardar en el storage
            circleImageUser?.setImageURI(fileUri)
        }
        else if (resultCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this, "Tarea cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImage(){
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startImageForResult.launch(intent)
            }
    }

    private fun refreshUserData() {
        getUserFromSession()

        // Actualiza los campos de la UI con los datos nuevos del usuario
        editTextName?.setText(user?.name)
        editTextLastName?.setText(user?.lastname)
        editTextPhone?.setText(user?.phone)

        // Si el usuario tiene una imagen, vuelve a cargarla
        if (!user?.image.isNullOrBlank()) {
            val imageUrlWithTimestamp = "${user?.image}?timestamp=${System.currentTimeMillis()}"

            Log.d(TAG, "Cargando imagen desde URL: $imageUrlWithTimestamp")
            Glide.with(this)
                .load(imageUrlWithTimestamp)  // Usa el timestamp para evitar que Glide use una imagen cacheada
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(circleImageUser!!)
        }
    }

}