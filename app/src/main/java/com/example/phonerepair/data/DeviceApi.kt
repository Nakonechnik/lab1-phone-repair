package com.example.phonerepair.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.Path

interface DeviceApi {
    @GET("objects")
    suspend fun devices(): List<CatalogDeviceDto>

    @GET("objects/{id}")
    suspend fun device(@Path("id") id: String): CatalogDeviceDto
}

@Serializable
data class CatalogDeviceDto(
    val id: String,
    val name: String,
    val data: JsonElement? = null
)
