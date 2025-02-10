package com.ndridm.eventsdicodingapp.view.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ndridm.eventsdicodingapp.data.ReminderWorker
import com.ndridm.eventsdicodingapp.data.SettingPreferences
import com.ndridm.eventsdicodingapp.data.dataStore
import com.ndridm.eventsdicodingapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val  pref = SettingPreferences.getInstance(requireContext().dataStore)
        settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory(pref))[SettingsViewModel::class.java]
        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner)
        { isDarkModeActive ->
            if (isDarkModeActive){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveThemeSetting(isChecked)
        }

        binding.switchReminder.setOnCheckedChangeListener { _, isCheck ->
            lifecycleScope.launch {
                delay(3000)
                if (isCheck) {
                    requestNotificationPermission()
                    scheduleDailyReminder()
                    val workManager = context?.let { WorkManager.getInstance(it) }
                    val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>().build()
                    workManager?.enqueue(reminderRequest)

                    Toast.makeText(requireContext(), "Reminder Active", Toast.LENGTH_SHORT).show()
                } else {
                    cancelDailyReminder()
                    Toast.makeText(requireContext(), "Reminder Inactive", Toast.LENGTH_SHORT).show()
                }
            }
        }

        checkWorkStatus()

    }

    private fun scheduleDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun cancelDailyReminder() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("daily_reminder")
    }

    private fun checkWorkStatus() {
        WorkManager.getInstance(requireContext()).getWorkInfosForUniqueWorkLiveData("daily_reminder")
            .observe(viewLifecycleOwner) { workInfos ->
                if (workInfos.isNotEmpty()) {
                    val workInfo = workInfos[0]
                    Log.d("SettingsFragment", "Status WorkManager: ${workInfo.state}")
                }
            }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        } else {
            Log.d("NotificationPermission", "Izin tidak diperlukan di Android < 13")
        }
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}