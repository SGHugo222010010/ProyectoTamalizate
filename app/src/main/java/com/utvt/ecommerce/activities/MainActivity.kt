package com.utvt.ecommerce.activities

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.utvt.ecommerce.R
import com.utvt.ecommerce.activities.client.home.ClientHomeActivity
import com.utvt.ecommerce.activities.delivery.home.DeliveryHomeActivity
import com.utvt.ecommerce.activities.restaurant.home.RestaurantHomeActivity
import com.utvt.ecommerce.models.ResponseHttp
import com.utvt.ecommerce.models.User
import com.utvt.ecommerce.providers.UsersProvider
import com.utvt.ecommerce.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    var imageViewGoToRegister: ImageView? = null
    var editTextEmail: EditText? = null
    var editTextPassword: EditText? = null
    var buttonLogin: Button? = null
    var usersProvider = UsersProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewGoToRegister = findViewById(R.id.imageview_go_to_register)
        editTextEmail = findViewById(R.id.edittext_email)
        editTextPassword = findViewById(R.id.edittext_password)
        buttonLogin = findViewById(R.id.btn_login)

        imageViewGoToRegister?.setOnClickListener { goToRegister() }
        buttonLogin?.setOnClickListener { login() }

        getUserFromSession()
    }

    private fun login() {
        val email = editTextEmail?.text.toString() // NULL POINTER EXCEPTION
        val password = editTextPassword?.text.toString()

        if (isValidForm(email, password)) {

            usersProvider.login(email, password)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    Log.d("MainActivity", "Response: ${response.body()}")

                    if (response.body()?.isSuccess == true) {
                        Toast.makeText(this@MainActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                        saveUserInSession(response.body()?.data.toString())
                    }
                    else {
                        Toast.makeText(this@MainActivity, "Los datos no son correctos", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(p0: Call<ResponseHttp>, t: Throwable) {
                    Log.d("MainActivity", "Hubo un error ${t.message}")
                    Toast.makeText(this@MainActivity, "Hubo un error ${t.message}", Toast.LENGTH_LONG).show()
                }
            })

        }
        else {
            Toast.makeText(this, "No es valido", Toast.LENGTH_LONG).show()
        }

//        Log.d("MainActivity", "El password es: $password")
    }

    private fun goToClientHome() {
        val i = Intent(this, ClientHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK // Eliminar el historial de pantallas
        startActivity(i)
    }

    private fun goToRestaurantHome() {
        val i = Intent(this, RestaurantHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK // Eliminar el historial de pantallas
        startActivity(i)
    }

    private fun goToDeliveryHome() {
        val i = Intent(this, DeliveryHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK // Eliminar el historial de pantallas
        startActivity(i)
    }

    private fun goToSelectRol() {
        val i = Intent(this, SelectRolesActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK // Eliminar el historial de pantallas
        startActivity(i)
    }

    private fun saveUserInSession(data: String) {

        val sharedPref = SharedPref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)

        if (user.roles?.size!! > 1) { // TIENE MAS DE UN ROL
            goToSelectRol()
        }
        else { // SOLO UN ROL (CLIENTE)
            goToClientHome()
        }

    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun getUserFromSession() {

        val sharedPref = SharedPref(this)
        val gson = Gson()

        if (!sharedPref.getData("user").isNullOrBlank()) {

            // SI EL USARIO EXISTE EN SESION
            val user = gson.fromJson(sharedPref.getData("user"), User::class.java)

            if (!sharedPref.getData("rol").isNullOrBlank()) {

                // SI EL USUARIO SELECCIONO EL ROL
                val rol = sharedPref.getData("rol")?.replace("\"", "")
                Log.d("MainActivity", "ROL $rol")


                if (rol == "RESTAURANTE") {
                    goToRestaurantHome()
                }
                else if (rol == "CLIENTE") {
                    goToClientHome()
                }
                else if (rol == "REPARTIDOR"){
                    goToDeliveryHome()
                }
            }
            else {
                Log.d("MainActivity", "ROL NO EXISTE")
                goToClientHome()
            }

        }

    }

    private fun isValidForm(email: String, password: String): Boolean {

        if (email.isBlank()) {
            return false
        }

        if (password.isBlank()) {
            return false
        }

        if (!email.isEmailValid()) {
            return false
        }

        return true
    }

    private fun goToRegister() {
        val i = Intent(this, RegisterActivity::class.java)
        startActivity(i)
    }


}