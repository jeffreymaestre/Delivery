package com.kotlin.appdelivery.providers

import com.kotlin.appdelivery.api.ApiRoutes
import com.kotlin.appdelivery.models.ResponseHttp
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.routes.UsersRoutes
import retrofit2.Call

class UsersProviders {

    private var usersRoutes: UsersRoutes? = null

    init {
        val api = ApiRoutes()
        usersRoutes = api.getUsersRoutes()
    }

    fun register(user: User): Call<ResponseHttp>?{
        return usersRoutes?.register(user)
    }

    fun login(email: String, password: String): Call<ResponseHttp>?{
        return usersRoutes?.login(email, password)
    }
}