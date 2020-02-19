package com.aescolar.marvelsuperheroes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aescolar.apimodule.ApiClient
import com.aescolar.apimodule.AuthInterceptor
import com.aescolar.apimodule.InterceptorProvider
import com.aescolar.apimodule.TimeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testApi()
    }

    private fun testApi(){
        val publicKey = "f580130be5d8dcb319a3b264cb4fdf7e"
        val privateKey = "04eedb3dbaef54b446e9ae44b5f5d8c0d0b3bff5"

        val authInterceptor = AuthInterceptor(publicKey, privateKey, TimeProvider())

        val apiClient = ApiClient("https://gateway.marvel.com/v1/public/", InterceptorProvider(listOf(authInterceptor)))

        GlobalScope.launch {
            val characters = withContext(Dispatchers.IO) {
                apiClient.fetchCharacters(1)
            }

            Log.d("MainActivity", "[testApi] characters: $characters")
        }

    }
}
