package com.ndridm.eventsdicodingapp.view.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ndridm.eventsdicodingapp.R
import com.ndridm.eventsdicodingapp.data.SettingPreferences
import com.ndridm.eventsdicodingapp.data.ViewModelFactory
import com.ndridm.eventsdicodingapp.data.dataStore
import com.ndridm.eventsdicodingapp.data.local.entity.EventEntity
import com.ndridm.eventsdicodingapp.data.remote.response.Event
import com.ndridm.eventsdicodingapp.databinding.ActivityDetailBinding
import com.ndridm.eventsdicodingapp.view.favorite.FavoriteViewModel
import com.ndridm.eventsdicodingapp.view.settings.SettingsViewModel
import com.ndridm.eventsdicodingapp.view.settings.SettingsViewModelFactory
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    private var currentEvent: Event? = null
    private var isFavorite= false

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        val pref = SettingPreferences.getInstance(dataStore)
        SettingsViewModelFactory(pref)
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        supportActionBar?.title = "Detail Event"

        val eventId = intent.getStringExtra(EXTRA_ID)
        if (eventId != null) detailViewModel.getDetailEvent(eventId)

        detailViewModel.eventDetail.observe(this) {
            showEventDetail(it)
        }
        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        detailViewModel.errorMessage.observe(this) {
            showError(it)
        }



        binding.btnFavorite.setOnClickListener {
            currentEvent?.let { event ->
                val eventEntity = EventEntity(
                    id = event.id,
                    name = event.name,
                    mediaCover = event.mediaCover,
                    beginTime = event.beginTime,
                )

                lifecycleScope.launch {
                    if (isFavorite) {
                        favoriteViewModel.deleteFavoriteEvent(eventEntity)
                    } else {
                        favoriteViewModel.addFavoriteEvent(eventEntity)
                    }
                }
            }
        }


        favoriteViewModel.favoriteEvents.observe(this) { favoriteEvents ->
            currentEvent.let { event ->
                isFavorite = favoriteEvents.any { it.id == event?.id }
                setIconfavorite()
            }
        }

        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun showEventDetail(event: Event?) {

        currentEvent = event

        val register = event?.registrants
        val quota = event?.quota
        val remainingQuota = register?.let { quota?.minus(it) }

        val beginEvent = event?.beginTime
        val eventDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val inFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val outFormatter = DateTimeFormatter.ofPattern("EEEE dd MMM yyyy")
            val localDateTime = LocalDateTime.parse(beginEvent, inFormatter)
            localDateTime.format(outFormatter)
        } else {
            // SimpleDateFormat utk versi Android lama
            try {
                val sdfInput = java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                val sdfOutput = java.text.SimpleDateFormat("EEEE dd MMM yyyy")
                val date = beginEvent?.let { sdfInput.parse(it) }
                sdfOutput.format(date ?: "")
            } catch (e: Exception) {
                "Tanggal tidak tersedia"
            }
        }

        val imgCover = binding.ivMediaCover
        if (event != null) {
            Glide.with(imgCover).load(event.mediaCover).into(imgCover)
            with(binding) {
                tvTitle.text = event.name
                tvOwner.text = getString(R.string.owner_event, event.ownerName)
                tvSummary.text = event.summary
                tvQuotaNominal.text = getString(R.string.quota_peserta, event.quota.toString())
                tvRegistrantsNominal.text = getString(R.string.sisa_quota, remainingQuota.toString())
                tvBeginTime.text = getString(R.string.acara_terbuka, "\n$eventDate")
                tvDescription.text = HtmlCompat.fromHtml(
                    event.description.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                btnRegister.setOnClickListener{
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    startActivity(intent)
                }
            }
        }

        checkFavorite(event)
    }

    private fun checkFavorite(event: Event?) {
        event?.let { e ->
            favoriteViewModel.favoriteEvents.observe(this) { favoriteEvents ->
                isFavorite = favoriteEvents.any { it.id == e.id }
                setIconfavorite()
            }
        }
    }
    private fun setIconfavorite() {
        binding.btnFavorite.setImageResource(
            if (isFavorite) R.drawable.baseline_favorite else R.drawable.baseline_favorite_border
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}