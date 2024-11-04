package com.kotlin.appdelivery.providers

import com.kotlin.appdelivery.api.ApiRoutes
import com.kotlin.appdelivery.models.Address
import com.kotlin.appdelivery.models.Category
import com.kotlin.appdelivery.models.Order
import com.kotlin.appdelivery.models.ResponseHttp
import com.kotlin.appdelivery.models.User
import com.kotlin.appdelivery.routes.AddressRoutes
import com.kotlin.appdelivery.routes.CategoriesRoutes
import com.kotlin.appdelivery.routes.OrdersRoutes
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File
import java.util.ArrayList

class OrdersProvider(val token: String) {
    private var ordersRoutes: OrdersRoutes? = null

    init {
        val api = ApiRoutes()
        ordersRoutes = api.getOrdersRoutes(token)
    }

    fun getOrdersByStatus(status: String): Call<ArrayList<Order>>? {
        return  ordersRoutes?.getOrdersByStatus(status, token)
    }

    fun getOrdersByClientAndStatus(id_client: String,status: String): Call<ArrayList<Order>>? {
        return  ordersRoutes?.getOrdersByClientAndStatus(id_client,status, token)
    }

    fun create(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.create(order, token)
    }
}