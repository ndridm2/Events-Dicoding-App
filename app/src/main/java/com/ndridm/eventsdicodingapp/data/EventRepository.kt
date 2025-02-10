package com.ndridm.eventsdicodingapp.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.loopj.android.http.BuildConfig
import com.ndridm.eventsdicodingapp.data.local.entity.EventEntity
import com.ndridm.eventsdicodingapp.data.local.room.EventDao
import com.ndridm.eventsdicodingapp.data.local.room.EventDatabase

class EventRepository(private val eventDao: EventDao) {
    val favoriteEvents: LiveData<List<EventEntity>> = eventDao.getFavoritesEvent()

    suspend fun insertFavoriteEvent(event: EventEntity) {
        try {
            eventDao.insertFavorite(event)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.d("EventRepository", "Error insert data event: ${e.message}")
            }
            throw e
        }
    }

    suspend fun deleteFavoriteEvent(event: EventEntity) {
        try {
            eventDao.deleteFavorite(event)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.d("EventRepository", "Error insert data event: ${e.message}")
            }
            throw e
        }
    }

    suspend fun isFavorite(eventId: Int): Boolean {
        return eventDao.isFavoritesEvent(eventId)
    }

    object  Injection {
        fun providerRepository(application: Application): EventRepository {
            val database = EventDatabase.getDatabase(application)
            val eventDao = database.eventDao()
            return EventRepository(eventDao)
        }
    }

}