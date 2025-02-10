package com.ndridm.eventsdicodingapp.view.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndridm.eventsdicodingapp.data.EventRepository
import com.ndridm.eventsdicodingapp.data.local.entity.EventEntity
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: EventRepository): ViewModel() {
    val favoriteEvents: LiveData<List<EventEntity>> = repository.favoriteEvents

    fun addFavoriteEvent(event: EventEntity) {
        viewModelScope.launch {
            if (event.id?.let { repository.isFavorite(it) }!!) {
                repository.deleteFavoriteEvent(event)
            } else {
                repository.insertFavoriteEvent(event)
            }
        }
    }
    fun deleteFavoriteEvent(event: EventEntity) = viewModelScope.launch {
        repository.deleteFavoriteEvent(event)
    }
}