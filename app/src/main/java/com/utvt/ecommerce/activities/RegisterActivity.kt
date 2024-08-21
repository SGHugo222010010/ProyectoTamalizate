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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.utvt.ecommerce.R
import com.utvt.ecommerce.activities.client.home.ClientHomeActivity
import com.utvt.ecommerce.models.ResponseHttp
import com.utvt.ecommerce.models.User
import com.utvt.ecommerce.providers.UsersProvider
import com.utvt.ecommerce.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    val  TAG = "RegisterActivity"

    var editTextName: EditText? = null
    var editTextLastname: EditText? = null
    var editTextEmail: EditText? = null
    var editTextPhone: EditText? = null
    var editTextPassword: EditText? = null
    var editTextConfirmPassword: EditText? = null
    var buttonRegister: Button? = null

    var usersProvider = UsersProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        editTextName = findViewById(R.id.edittext_name)
        editTextLastname = findViewById(R.id.edittext_lastname)
        editTextEmail = findViewById(R.id.edittext_email)
        editTextPhone = findViewById(R.id.edittext_phone)
        editTextPassword = findViewById(R.id.edittext_password)
        editTextConfirmPassword = findViewById(R.id.edittext_confirm_password)
        buttonRegister = findViewById(R.id.btn_register)
        buttonRegister?.setOnClickListener{register()}

        var imageViewGoToLogin: ImageView? = null

        imageViewGoToLogin = findViewById(R.id.imageview_go_to_login)
        //Aqui hay un if
        imageViewGoToLogin?.setOnClickListener{goToLogin()}
    }

    private fun goToLogin(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun register(){
        val name = editTextName?.text.toString()
        val lastname = editTextLastname?.text.toString()
        val email = editTextEmail?.text.toString()
        val phone = editTextPhone?.text.toString()
        val password = editTextPassword?.text.toString()
        val confirmPassword = editTextConfirmPassword?.text.toString()

        Toast.makeText(this, "El name es: $name", Toast.LENGTH_LONG).show()

        //Validamos que se ha un formulario valido
        if (isValidForm(name = name, lastname = lastname, email = email, phone = phone, password = password, confirmPassword = confirmPassword)){

            val user = User(
                name = name,
                lastname = lastname,
                email = email,
                phone = phone,
                password = password
            )
            usersProvider.register(user)?.enqueue(object: Callback<ResponseHttp>{
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    if (response.body()?.isSuccess == true) {
                        saveUserInSession(response.body()?.data.toString())
                        goToClientHome()
                    }

                    Toast.makeText(this@RegisterActivity, response.body()?.message, Toast.LENGTH_LONG).show()

                    Log.d(TAG, "Response: ${response}")
                    Log.d(TAG, "Body: ${response.body()}")

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Se produjo un error ${t.message}")
                    Toast.makeText(this@RegisterActivity,"Se produjo un error", Toast.LENGTH_LONG).show()
                }

            })
        }

    }

    private fun goToClientHome() {
        val i = Intent(this, SaveImageActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Eliminar el historial de pantallas
        startActivity(i)
    }

    private fun saveUserInSession(data: String) {

        val sharedPref = SharedPref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)
    }

    fun String.isEmailValid(): Boolean{
        return  !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun isValidForm(
        name: String,
        lastname: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean{

        if (name.isBlank()){
            Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_LONG).show()
            return false
        }
        if (lastname.isBlank()){
            Toast.makeText(this, "Debes ingresar el appelido", Toast.LENGTH_LONG).show()
            return false
        }
        if (email.isBlank()){
            Toast.makeText(this, "Debes ingresar el email", Toast.LENGTH_LONG).show()
            return false
        }
        if (phone.isBlank()){
            Toast.makeText(this, "Debes ingresar el phone", Toast.LENGTH_LONG).show()
            return false
        }
        if (password.isBlank()){
            Toast.makeText(this, "Debes ingresar el password", Toast.LENGTH_LONG).show()
            return false
        }
        if (confirmPassword.isBlank()){
            Toast.makeText(this, "Debes ingresar el ConfirmPassword", Toast.LENGTH_LONG).show()
            return false
        }
        //Confirma mediante el metodo isEmailValid creado, que ciertos carcateres tienen que tener para ser un correo
        if (!email.isEmailValid()){
            Toast.makeText(this, "El email no es valido", Toast.LENGTH_LONG).show()
            return false
        }
        //confirmamos que el password sea igual al confirmPassword con el método reservado equals = es igual a, pero Kotlin dice que se remplace con un ! =
        if (password != confirmPassword){
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }
        return  true
    }

}