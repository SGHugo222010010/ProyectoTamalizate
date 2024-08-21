package com.utvt.ecommerce.api

import com.utvt.ecommerce.providers.UsersProvider
import com.utvt.ecommerce.routes.UsersRoutes

class ApiRoutes {
    val API_URL = "http://192.168.1.4:3000/api/"  // Usar 10.0.2.2 para localhost en el emulador
    val retrofit = RetrofitClient()

    fun getUsersRoutes(): UsersRoutes {
        return retrofit.getClient(API_URL).create(UsersRoutes::class.java)
    }
    fun getUsersRoutesWithToken(token: String): UsersRoutes {
        return retrofit.getClientWithToken(API_URL, token).create(UsersRoutes::class.java)
    }
}
