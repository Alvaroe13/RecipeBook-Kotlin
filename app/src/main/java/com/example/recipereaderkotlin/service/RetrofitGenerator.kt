package com.example.recipereaderkotlin.service

import com.example.recipereaderkotlin.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * this class will generation connection to server through retrofit
 */
class RetrofitGenerator {

    companion object{

        private val retrofitInstance by lazy {
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val connectionClient = OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(connectionClient)
                .build()
        }


        val apiConnection by lazy {
            retrofitInstance.create(Api::class.java)
        }

    }


}