package com.ndridm.eventsdicodingapp.view.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ndridm.eventsdicodingapp.data.EventRepository
import com.ndridm.eventsdicodingapp.data.SettingPreferences
import com.ndridm.eventsdicodingapp.data.dataStore
import com.ndridm.eventsdicodingapp.databinding.FragmentFavoriteBinding
import com.ndridm.eventsdicodingapp.view.adapter.FavoriteAdapter
import com.ndridm.eventsdicodingapp.view.detail.DetailActivity
import com.ndridm.eventsdicodingapp.view.settings.SettingsViewModel
import com.ndridm.eventsdicodingapp.view.settings.SettingsViewModelFactory

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoriteViewModel
    private val favoriteAdapter by lazy {
        FavoriteAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_ID, event.id.toString())
            }
            startActivity(intent)
        }
    }

    private val settingViewModel: SettingsViewModel by viewModels {
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        SettingsViewModelFactory(pref)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = EventRepository.Injection.providerRepository(requireActivity().application)
        val factory = FavoriteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
        observeThemeSettings()

    }

    private fun setupRecyclerView() {
        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteAdapter
        }

        favoriteAdapter.setOnItemClickCallback { event ->
            val intent = Intent(activity, DetailActivity::class.java).apply {
                putExtra(EVENT_ID_KEY, event.id)
            }
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            binding.apply {
                tvNoEventFav.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
                rvEvent.visibility = if (events.isEmpty()) View.GONE else View.VISIBLE
            }
            favoriteAdapter.submitList(events)
        }
    }

    private fun observeThemeSettings() {
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EVENT_ID_KEY = "event_id";
    }

}
