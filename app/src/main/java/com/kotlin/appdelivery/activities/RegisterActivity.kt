package com.kotlin.appdelivery.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.client.home.ClientHomeActivity
import com.kotlin.appdelivery.models.ResponseHttp
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.providers.UsersProviders
import com.kotlin.appdelivery.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    val TAG = "RegisterActivity"

    var imageViewGoToLogin : ImageView ? = null
    var editTextName : EditText? = null
    var editTextLastName : EditText? = null
    var editTextEmail : EditText? = null
    var editTextPhone : EditText? = null
    var editTextPassword : EditText? = null
    var editTextConfirmPassword : EditText? = null
    var buttonRegister : Button? = null

    var usersProviders = UsersProviders()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageViewGoToLogin = findViewById(R.id.imageview_go_to_login)
        editTextName = findViewById(R.id.edit_text_name)
        editTextLastName = findViewById(R.id.edit_text_last_name)
        editTextEmail = findViewById(R.id.edit_text_email)
        editTextPhone = findViewById(R.id.edit_text_phone)
        editTextPassword = findViewById(R.id.edit_text_password)
        editTextConfirmPassword = findViewById(R.id.edit_text_confirm_password)
        buttonRegister = findViewById(R.id.btn_register)

        imageViewGoToLogin?.setOnClickListener { goToLogin() }
        buttonRegister?.setOnClickListener { register() }
    }

    private fun register(){
        val name = editTextName?.text.toString()
        val lastname = editTextLastName?.text.toString()
        val email = editTextEmail?.text.toString()
        val phone = editTextPhone?.text.toString()
        val password = editTextPassword?.text.toString()
        val confirm_password = editTextConfirmPassword?.text.toString()

        if (isValidForm(name, lastname, email, phone, password, confirm_password)){
            val user = User(
                name = name,
                lastname = lastname,
                email = email,
                phone = phone,
                pasword = password
            )
            usersProviders.register(user)?.enqueue(object: Callback<ResponseHttp>{
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    if (response.body()?.isSuccess == true){
                        saveUserInSession(response.body()?.data.toString())
                        goToClientHome()
                    }

                    Toast.makeText(this@RegisterActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Response: ${response}")
                    Log.d(TAG, "Body: ${response.body()}")
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Se produjo un error ${t.message}")
                    Toast.makeText(this@RegisterActivity, "Se produjo un error ${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }
    }

    private fun goToClientHome(){
        val i = Intent(this, ClientHomeActivity::class.java)
        startActivity(i)
    }

    private fun saveUserInSession(data: String){
        val sharedPref = SharePref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)
    }

    fun String.isEmailValid(): Boolean{
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun isValidForm(name: String,
                            lastname: String,
                            email: String,
                            phone: String,
                            password: String,
                            confirm_password: String
                            ) : Boolean {
        if (name.isNullOrBlank()){
            Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_SHORT).show()
            return false
        }
        if (lastname.isNullOrBlank()){
            Toast.makeText(this, "Debes ingresar el apellido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isNullOrBlank()){
            Toast.makeText(this, "Debes ingresar el email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (phone.isNullOrBlank()){
            Toast.makeText(this, "Debes ingresar el telefono", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isNullOrBlank()){
            Toast.makeText(this, "Debes ingresar el contraseña", Toast.LENGTH_SHORT).show()
            return false
        }

        if (confirm_password.isNullOrBlank()){
            Toast.makeText(this, "Debes ingresar el confirmar contraseña", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!email.isEmailValid()){
            Toast.makeText(this, "El email no es valido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirm_password){
            Toast.makeText(this, "La contraseña no coincide", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun goToLogin () {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
}