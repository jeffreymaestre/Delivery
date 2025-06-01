package com.kotlin.appdelivery.api

import com.kotlin.appdelivery.routes.AddressRoutes
import com.kotlin.appdelivery.routes.CategoriesRoutes
import com.kotlin.appdelivery.routes.OrdersRoutes
import com.kotlin.appdelivery.routes.ProductsRoutes
import com.kotlin.appdelivery.routes.UsersRoutes

class ApiRoutes {
    //val API_URL = "http://192.168.56.1:3000/api/"
    val API_URL = "http://86.48.24.183:3000/api/" //SERVER LINUX
    //val API_URL = "https://app-delivery-1b69a60ed081.herokuapp.com/api/"
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

    fun getProductssRoutes(token: String): ProductsRoutes{
        return retrofit.getClientWithToken(API_URL, token).create(ProductsRoutes::class.java)
    }

    fun getAddressRoutes(token: String): AddressRoutes{
        return retrofit.getClientWithToken(API_URL, token).create(AddressRoutes::class.java)
    }

    fun getOrdersRoutes(token: String): OrdersRoutes{
        return retrofit.getClientWithToken(API_URL, token).create(OrdersRoutes::class.java)
    }
}