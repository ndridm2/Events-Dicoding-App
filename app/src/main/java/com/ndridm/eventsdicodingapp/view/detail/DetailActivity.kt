package com.ndridm.eventsdicodingapp.view.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.ndridm.eventsdicodingapp.data.response.Event
import com.ndridm.eventsdicodingapp.databinding.ActivityDetailBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    companion object {
        const val TAG = "DetailEventActivity"
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
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun showEventDetail(event: Event?) {
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
                val sdfInput = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
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
                tvOwner.text = "Diselenggarakan oleh : ${event.ownerName}"
                tvSummary.text = event.summary
                tvQuotaNominal.text = "Quota peserta : ${event.quota.toString()}"
                tvRegistrantsNominal.text = "Sisa Quota: ${remainingQuota.toString()}"
                tvBeginTime.text = "Acara Terbuka Hingga:\n${eventDate}"
                tvDescription.text = HtmlCompat.fromHtml(
                    event.description.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                btnRegister.setOnClickListener{
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    startActivity(intent)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}