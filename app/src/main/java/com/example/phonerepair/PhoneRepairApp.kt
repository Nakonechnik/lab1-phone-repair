package com.example.phonerepair

import android.app.Application
import android.util.Log
import com.example.phonerepair.data.DeviceApi
import com.example.phonerepair.data.RepairRepository
import com.example.phonerepair.data.fakeServerError
import com.example.phonerepair.data.slowNetwork
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://api.restful-api.dev/"

private const val SLOW_NETWORK = false

class PhoneRepairApp : Application() {
    private val json = Json { ignoreUnknownKeys = true }

    private val client = OkHttpClient.Builder()
        .callTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor { message -> Log.d("PhoneRepairHttp", message) }.apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .apply { if (SLOW_NETWORK) addInterceptor(slowNetwork) }
        .addInterceptor(fakeServerError)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(DeviceApi::class.java)

    val repository = RepairRepository(api)
}
