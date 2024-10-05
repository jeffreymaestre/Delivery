package com.kotlin.appdelivery.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.appdelivery.R

class MainActivity : AppCompatActivity() {
    var imageViewGoToRegiser: ImageView ? = null
    var editTextEmail : EditText ? = null
    var editTextPassword : EditText ? = null
    var buttonLogin : Button  ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Cargar el layout XML

        imageViewGoToRegiser = findViewById(R.id.imageview_go_to_register)
        editTextEmail = findViewById(R.id.edit_text_email)
        editTextPassword = findViewById(R.id.edit_text_password)
        buttonLogin = findViewById(R.id.btn_login)

        imageViewGoToRegiser?.setOnClickListener { goToRegister() }
        buttonLogin?.setOnClickListener { login() }
    }

    private fun login(){
        val email = editTextEmail?.text.toString()
        val password = editTextPassword?.text.toString()

        if (isValidForm(email,password)){
            Toast.makeText(this, "El formulario es valido", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this, "El formulario no es valido", Toast.LENGTH_LONG).show()
        }

        Log.d("MainActivity", "El email es: $email")
        Log.d("MainActivity", "El password es: $password")
    }

    fun String.isEmailValid(): Boolean{
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun isValidForm(email: String, password: String) : Boolean {
        if (email.isNullOrBlank()){
            return false
        }

        if (!email.isEmailValid()){
            return false
        }
        return true
    }

    private fun goToRegister(){
        val i = Intent(this, RegisterActivity::class.java)
        startActivity(i)
    }
}