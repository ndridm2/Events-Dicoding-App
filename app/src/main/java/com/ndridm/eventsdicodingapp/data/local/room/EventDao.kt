package com.ndridm.eventsdicodingapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ndridm.eventsdicodingapp.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM favorite")
    fun getFavoritesEvent(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM favorite WHERE id = :id LIMIT 1")
    fun getFavoriteEventById(id: Int): LiveData<EventEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(event: EventEntity)

    @Delete
    suspend fun deleteFavorite(event: EventEntity)

    @Query("SELECT EXISTS(SELECT * FROM favorite WHERE id = :id)")
    suspend fun isFavoritesEvent(id: Int): Boolean
}