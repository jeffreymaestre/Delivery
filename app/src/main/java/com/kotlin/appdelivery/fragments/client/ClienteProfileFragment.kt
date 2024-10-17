package com.kotlin.appdelivery.fragments.client

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.kotlin.appdelivery.R
import com.kotlin.appdelivery.activities.MainActivity
import com.kotlin.appdelivery.activities.SelectRolesActivity
import com.kotlin.appdelivery.activities.client.update.ClientUpdateActivity
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.utils.SharePref
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.log

class ClienteProfileFragment : Fragment() {
    var myView: View? = null
    var buttonSelectRol: Button? = null
    var buttonUpdateProfile: Button? = null
    var circleImageUser: CircleImageView? = null
    var textViewName : TextView? = null
    var textViewEmail : TextView? = null
    var textViewPhone : TextView? = null
    var imageViewLogout: ImageView? = null

    var sharedPerf: SharePref? = null
    var user: User? = null

       override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_cliente_profile, container, false)

           sharedPerf = SharePref(requireActivity())

        buttonSelectRol = myView?.findViewById(R.id.btn_select_rol)
           buttonUpdateProfile = myView?.findViewById(R.id.btn_update_profile)
           textViewName = myView?.findViewById(R.id.textView_name)
           textViewEmail = myView?.findViewById(R.id.textView_email)
           textViewPhone = myView?.findViewById(R.id.textView_phone)
           circleImageUser = myView?.findViewById(R.id.circleimage_user)
           imageViewLogout = myView?.findViewById(R.id.imageview_logout)

           buttonSelectRol?.setOnClickListener{ goToSelectRol()}
           imageViewLogout?.setOnClickListener{ logout() }
           buttonUpdateProfile?.setOnClickListener{ goToUpdate() }

           loadUserData()

        return myView
    }

    override fun onResume() {
        super.onResume()
        // Recargar los datos del usuario cada vez que el fragmento se reanuda
        loadUserData()
    }

    private fun loadUserData(){
        getUserFromSession()

        textViewName?.text = "${user?.name} ${user?.lastname}"
        textViewEmail?.text = user?.email
        textViewPhone?.text = user?.phone

        if (!user?.image.isNullOrBlank()){
            Glide.with(requireContext()).load(user?.image).into(circleImageUser!!)
        }
    }


    private fun logout(){
        sharedPerf?.remove("user")
        val i = Intent(requireContext(), MainActivity::class.java)
        startActivity(i)
    }

    private fun getUserFromSession(){
        val gson = Gson()

        if (!sharedPerf?.getData("user").isNullOrBlank()){
            // VALIDO SI EL USUARIO EXISTE EN SESION
            user = gson.fromJson(sharedPerf?.getData("user"), User::class.java)
        }
    }

    private fun goToUpdate(){
        val i = Intent(requireContext(), ClientUpdateActivity::class.java)
        startActivity(i)
    }

    private fun goToSelectRol(){
        val i = Intent(requireContext(), SelectRolesActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK // eliminar historial de pantallas
        startActivity(i)
    }

}