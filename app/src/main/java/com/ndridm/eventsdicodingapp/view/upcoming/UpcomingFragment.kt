package com.ndridm.eventsdicodingapp.view.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ndridm.eventsdicodingapp.data.SettingPreferences
import com.ndridm.eventsdicodingapp.data.dataStore
import com.ndridm.eventsdicodingapp.data.remote.response.ListEventsItem
import com.ndridm.eventsdicodingapp.databinding.FragmentUpcomingBinding
import com.ndridm.eventsdicodingapp.view.adapter.EventAdapter
import com.ndridm.eventsdicodingapp.view.detail.DetailActivity
import com.ndridm.eventsdicodingapp.view.settings.SettingsViewModel
import com.ndridm.eventsdicodingapp.view.settings.SettingsViewModelFactory

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private val upcomingViewModel: UpcomingViewModel by viewModels()

    private val settingViewModel: SettingsViewModel by viewModels {
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        SettingsViewModelFactory(pref)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val layoutManager = LinearLayoutManager(requireContext())
            rvEvent.layoutManager = layoutManager
            rvEvent.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
        }

        upcomingViewModel.apply {
            eventList.observe(viewLifecycleOwner) { eventList ->
                binding.apply {
                    tvNoEventNow.visibility = if (eventList.isEmpty()) View.VISIBLE else View.GONE
                    if (eventList.isNotEmpty()) setUpcomingEvent(eventList)
                }
            }

            isLoading.observe(viewLifecycleOwner, ::showLoading)
            errorMessage.observe(viewLifecycleOwner, ::showError)
        }

        settingViewModel.getThemeSettings()
            .observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

    }

    private fun setUpcomingEvent(upcomingEvent: List<ListEventsItem>) {
        val adapter = EventAdapter {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID, it.toString())
            startActivity(intent)
        }
        binding.rvEvent.adapter = adapter.apply {
            submitList(upcomingEvent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showError(errorMessage: String) {
        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}