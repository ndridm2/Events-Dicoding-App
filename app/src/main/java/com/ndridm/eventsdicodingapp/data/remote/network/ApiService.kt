package com.ndridm.eventsdicodingapp.data.remote.network

import com.ndridm.eventsdicodingapp.data.remote.response.DetailEventResponse
import com.ndridm.eventsdicodingapp.data.remote.response.EventResponse
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
        @Query("active") active: Int,
        @Query("q") q: String
    ): Call<EventResponse>

    @GET("events")
    fun getEventNotification(
        @Query("active") active: Int,
        @Query("limit") limit: Int
    ): Call<EventResponse>

}