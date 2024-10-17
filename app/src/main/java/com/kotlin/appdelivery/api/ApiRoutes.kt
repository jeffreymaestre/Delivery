package com.kotlin.appdelivery.api

import com.kotlin.appdelivery.routes.CategoriesRoutes
import com.kotlin.appdelivery.routes.UsersRoutes

class ApiRoutes {
    val API_URL = "http://192.168.1.20:3000/api/"
    val retrofit = RetrofitClient()

    fun getUsersRoutes(): UsersRoutes{
        return retrofit.getClient(API_URL).create(UsersRoutes::class.java)
    }

    fun getUsersRoutesWithToken(token: String): UsersRoutes{
        return retrofit.getClientWithToken(API_URL, token).create(UsersRoutes::class.java)
    }

    fun getCategoriesRoutes(token: String): CategoriesRoutes{
        return retrofit.getClientWithToken(API_URL, token).create(CategoriesRoutes::class.java)
    }
}