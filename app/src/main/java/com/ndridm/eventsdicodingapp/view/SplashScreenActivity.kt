package com.ndridm.eventsdicodingapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.ndridm.eventsdicodingapp.data.SettingPreferences
import com.ndridm.eventsdicodingapp.data.dataStore
import com.ndridm.eventsdicodingapp.databinding.ActivitySplashScreenBinding
import com.ndridm.eventsdicodingapp.view.settings.SettingsViewModel
import com.ndridm.eventsdicodingapp.view.settings.SettingsViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private val settingsViewModel: SettingsViewModel by viewModels {
        val pref = SettingPreferences.getInstance(dataStore)
        SettingsViewModelFactory(pref)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        initView()
    }

    private fun initView() {
        binding.root.post {
            binding.root.transitionToEnd {
                lifecycleScope.launch {
                    delay(500)
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}