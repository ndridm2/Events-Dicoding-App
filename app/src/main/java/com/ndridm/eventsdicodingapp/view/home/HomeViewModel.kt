package com.ndridm.eventsdicodingapp.view.home

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

class HomeViewModel : ViewModel() {

    private val _searchResult = MutableLiveData<List<ListEventsItem>>()
    val searchResult: LiveData<List<ListEventsItem>> = _searchResult

    private val _isLoadingUpcoming = MutableLiveData<Boolean>()
    val isLoadingUpcoming: LiveData<Boolean> = _isLoadingUpcoming

    private val _isLoadingFinished = MutableLiveData<Boolean>()
    val isLoadingFinished: LiveData<Boolean> = _isLoadingFinished

    private val _eventCarousel = MutableLiveData<List<ListEventsItem>>()
    val eventCarousel: LiveData<List<ListEventsItem>> = _eventCarousel

    private val _eventFinishedList = MutableLiveData<List<ListEventsItem>>()
    val eventFinishedList: LiveData<List<ListEventsItem>> = _eventFinishedList

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        findItemCarousel()
        findItemFinished()
    }

    fun getSearchResult(search: String) {
        val client = ApiConfig.getApiService().getEventSearch(-1, search)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        _searchResult.value = response.body()?.listEvents
                    }
                }else {
                    Log.e(TAG, "onResponseFail: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    private fun findItemFinished() {
        _isLoadingFinished.value = true
        val client = ApiConfig.getApiService().getEventActiveLimit(0,5)
        client.enqueue(object : Callback<EventResponse>{
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    _isLoadingFinished.value = false
                    if (response.body() != null) {
                        _eventFinishedList.value = response.body()?.listEvents
                    }
                } else {
                    Log.e(TAG, "onResponseFail: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingFinished.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })

    }

    private fun findItemCarousel() {
        _isLoadingUpcoming.value = true
        val client = ApiConfig.getApiService().getEventActiveLimit(1, 5)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    _isLoadingUpcoming.value = false
                    if (response.body() != null) _eventCarousel.value = response.body()?.listEvents
                } else {
                    Log.e(TAG, "onResponseFail: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingUpcoming.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

}