package com.kotlin.appdelivery.activities.restaurant.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.MainActivity
import com.kotlin.appdelivery.fragments.client.ClientOrdersFragment
import com.kotlin.appdelivery.fragments.client.ClienteCategoriesFragment
import com.kotlin.appdelivery.fragments.client.ClienteProfileFragment
import com.kotlin.appdelivery.fragments.restaurant.RestaurantCategoryFragment
import com.kotlin.appdelivery.fragments.restaurant.RestaurantOrdersFragment
import com.kotlin.appdelivery.fragments.restaurant.RestaurantProductFragment
import com.kotlin.appdelivery.models.ResponseHttp
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.providers.OrdersProvider
import com.kotlin.appdelivery.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class RestaurantHomeActivity : AppCompatActivity() {
    private val TAG = "RestaurantHomeActivity"
    //var butonLogout: Button? = null
    var sharedPerf:  SharePref? = null

    var bottonNavegation: BottomNavigationView? = null

    private lateinit var ordersProvider: OrdersProvider
    var user: User? = null
    var totalDaySum: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_restaurant_home)
        sharedPerf = SharePref(this)
        totalDaySum = findViewById(R.id.total_day_restaurant)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //butonLogout = findViewById(R.id.btn_logout)
        //butonLogout?.setOnClickListener{ logout() }

        openFragment(RestaurantOrdersFragment())

        bottonNavegation = findViewById(R.id.bottom_navegation)
        bottonNavegation?.setOnItemSelectedListener {
            when(it.itemId){
                R.id.item_home -> {
                    openFragment(RestaurantOrdersFragment())
                    true
                }
                R.id.item_category -> {
                    openFragment(RestaurantCategoryFragment())
                    true
                }
                R.id.item_product -> {
                    openFragment(RestaurantProductFragment())
                    true
                }
                R.id.item_profile -> {
                    openFragment(ClienteProfileFragment())
                    true
                }
                else -> false
            }
        }
        getUserFromSession()

        totalDaySum?.setOnClickListener {
            if (::ordersProvider.isInitialized) {
                getTotalDay()
            } else {
                Toast.makeText(this, "No se pudo obtener el total del día. Usuario no válido.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTotalDay() {
        val call = ordersProvider.getTotalDay()
        call?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseHttp ->
                        Log.d("Retrofit Response", "Respuesta del servidor: ${responseHttp.data}")
                        val data = responseHttp.data

                        // Verifica que `data` no sea null y que sea un objeto JSON válido
                        if (data != null && data.isJsonObject) {
                            val jsonObject = data.asJsonObject

                            val orderDate = jsonObject.get("order_date")?.asString ?: "Sin fecha"
                            val total = jsonObject.get("total")?.asString ?: "0"
                            showTotalDayDialog(total)
                            //Toast.makeText(this@RestaurantHomeActivity, "Fecha: $orderDate\nTotal: $total", Toast.LENGTH_LONG).show()
                        } else {
                            showTotalDayDialog("0")
                            //Toast.makeText(this@RestaurantHomeActivity, "No hay datos disponibles", Toast.LENGTH_SHORT).show()

                        }
                    } ?: run {
                        showTotalDayDialog("0")
                        //Toast.makeText(this@RestaurantHomeActivity, "Respuesta vacía", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showTotalDayDialog("0")
                //Toast.makeText(this@RestaurantHomeActivity, "Error en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                showTotalDayDialog("Error: ${t.message}")
            //Toast.makeText(this@RestaurantHomeActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun openFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun logout(){
        sharedPerf?.remove("user")
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun getUserFromSession(){
        val gson = Gson()
        val userData = sharedPerf?.getData("user")

        if (!userData.isNullOrBlank()) {
            this.user = gson.fromJson(userData, User::class.java)
            Log.d(TAG, "Usuario encontrado: ${user?.sessionToken}")

            // Inicializar ordersProvider aquí porque user ya no es null
            if (!user?.sessionToken.isNullOrBlank()) {
                ordersProvider = OrdersProvider(user!!.sessionToken!!)
                Log.d(TAG, "OrdersProvider inicializado con éxito")
            } else {
                Log.e(TAG, "Error: Token de sesión es nulo o vacío, no se puede inicializar ordersProvider")
            }
        } else {
            Log.e(TAG, "Error: No se encontró usuario en sesión")
        }

    }

    private fun showTotalDayDialog(total: String) {
        val numberFormat = NumberFormat.getInstance(Locale("es", "CO")) // Formato de Colombia
        val formattedTotal = numberFormat.format(total.toDouble()) // Formatea el número

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Total del Día")
        builder.setMessage("El total del día es: $ $formattedTotal")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


}