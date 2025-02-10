package com.ndridm.eventsdicodingapp.view.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ndridm.eventsdicodingapp.data.EventRepository

class FavoriteViewModelFactory(private val repository: EventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}