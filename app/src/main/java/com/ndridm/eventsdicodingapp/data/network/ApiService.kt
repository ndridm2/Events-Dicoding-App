package com.ndridm.eventsdicodingapp.data.network

import com.ndridm.eventsdicodingapp.data.response.DetailEventResponse
import com.ndridm.eventsdicodingapp.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvent(
        @Query("active")
        active: Int
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvent(
        @Path("id") id: String
    ): Call<DetailEventResponse>

    @GET("events")
    fun getEventActiveLimit(
        @Query("active") active: Int,
        @Query("limit") limit: Int
    ): Call<EventResponse>

    @GET("events")
    fun getEventSearch(
        @Query("actvie") active: Int,
        @Query("q") q: String
    ): Call<EventResponse>

}