package com.ndridm.eventsdicodingapp.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ndridm.eventsdicodingapp.data.remote.network.ApiConfig
import com.ndridm.eventsdicodingapp.data.remote.response.EventResponse
import com.ndridm.eventsdicodingapp.data.remote.response.ListEventsItem
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

    private val _eventCarousel = MutableLiveData<List<ListEventsItem>?>()
    val eventCarousel: MutableLiveData<List<ListEventsItem>?> = _eventCarousel

    private val _eventFinishedList = MutableLiveData<List<ListEventsItem>>()
    val eventFinishedList: LiveData<List<ListEventsItem>> = _eventFinishedList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

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
                    _errorMessage.value = "Gagal memuat data: ${response.message()}"
                    Log.e(TAG, "onResponseFail: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _errorMessage.value = "Terjadi kesalahan: ${t.message}"
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
                    _errorMessage.value = "Gagal memuat data: ${response.message()}"
                    Log.e(TAG, "onResponseFail: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingFinished.value = true
                _errorMessage.value = "Terjadi kesalahan: ${t.message}"
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })

    }

    private fun findItemCarousel() {
        _isLoadingUpcoming.value = true
        val client = ApiConfig.getApiService().getEventActiveLimit(1, 5)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoadingUpcoming.value = false
                if (response.isSuccessful) {
                    val eventList = response.body()?.listEvents ?: emptyList()

                    if (eventList.isNotEmpty()) {
                        _eventCarousel.value = eventList
                    } else {
                        // Jika kosong, coba ambil data dari getEventActiveLimit(0, 5)
                        fetchBackupEvents()
                    }
                } else {
                    _errorMessage.value = "Gagal memuat data: ${response.message()}"
                    Log.e(TAG, "onResponseFail: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingUpcoming.value = true
                _errorMessage.value = "Terjadi kesalahan: ${t.message}"
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun fetchBackupEvents() {
        _isLoadingUpcoming.value = true
        val backupClient = ApiConfig.getApiService().getEventActiveLimit(0, 3)

        backupClient.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoadingUpcoming.value = false
                if (response.isSuccessful) {
                    val backupEventList = response.body()?.listEvents ?: emptyList()
                    if (backupEventList.isNotEmpty()) {
                        _eventCarousel.value = backupEventList
                    } else {
                        showPlaceholderImage()
                    }
                } else {
                    showPlaceholderImage()
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingUpcoming.value = false
                _errorMessage.value = "Terjadi kesalahan: ${t.message}"
                Log.e(TAG, "Backup Fetch Failed: ${t.message}")
                showPlaceholderImage()
            }
        })
    }

    private fun showPlaceholderImage() {
        val randomPlaceholder = listOf(
            "android.resource://com.ndridm.eventsdicodingapp/drawable/placeholder_1",
            "android.resource://com.ndridm.eventsdicodingapp/drawable/placeholder_2",
            "android.resource://com.ndridm.eventsdicodingapp/drawable/placeholder_3"
        ).random()

        _eventCarousel.value = listOf(
            ListEventsItem(
                id = 0,
                name = "Tidak Ada Event",
                beginTime = "-",
                mediaCover = randomPlaceholder
            )
        )
    }

}