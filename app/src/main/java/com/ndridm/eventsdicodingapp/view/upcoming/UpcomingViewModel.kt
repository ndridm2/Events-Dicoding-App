package com.ndridm.eventsdicodingapp.view.upcoming

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ndridm.eventsdicodingapp.data.network.ApiConfig
import com.ndridm.eventsdicodingapp.data.response.EventResponse
import com.ndridm.eventsdicodingapp.data.response.ListEventsItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingViewModel : ViewModel() {
    private val _eventList = MutableLiveData<List<ListEventsItem>>()
    val eventList: LiveData<List<ListEventsItem>> = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "UpcomingViewModel"
    }

    init {
        findUpcomingEvent()
    }

    private fun findUpcomingEvent() {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getEvent(1)
        client.enqueue(object : Callback<EventResponse>{
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventList.value = response.body()?.listEvents
                }else {
                    Log.e(TAG, "onResponseFail: ${response.message()}" )
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "onFailure: ${t.message}" )
            }

        })
    }
}