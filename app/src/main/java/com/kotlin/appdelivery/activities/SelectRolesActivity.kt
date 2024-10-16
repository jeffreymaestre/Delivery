package com.kotlin.appdelivery.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.adapters.RolesAdapter
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.utils.SharePref

class SelectRolesActivity : AppCompatActivity() {
    var recycleViewRoles: RecyclerView? = null
    var user: User? = null
    var adapter: RolesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_roles)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recycleViewRoles = findViewById(R.id.recycleview_roles)
        recycleViewRoles?.layoutManager = LinearLayoutManager(this)

        getUserFromSession()

        adapter = RolesAdapter(this, user?.roles!!)

        recycleViewRoles?.adapter = adapter
    }

    private fun getUserFromSession(){
        val sharedPerf = SharePref(this)
        val gson = Gson()

        if (!sharedPerf.getData("user").isNullOrBlank()){
            // VALIDO SI EL USUARIO EXISTE EN SESION
            user = gson.fromJson(sharedPerf.getData("user"), User::class.java)
        }
    }

}