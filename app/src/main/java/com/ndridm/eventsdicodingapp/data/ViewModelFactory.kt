package com.ndridm.eventsdicodingapp.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ndridm.eventsdicodingapp.view.detail.DetailViewModel
import com.ndridm.eventsdicodingapp.view.favorite.FavoriteViewModel

class ViewModelFactory private constructor(private val repository: EventRepository) :
    ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(EventRepository.Injection.providerRepository(application)).also {
                    instance = it
                }
            }
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return DetailViewModel() as T
        } else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return FavoriteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }


}