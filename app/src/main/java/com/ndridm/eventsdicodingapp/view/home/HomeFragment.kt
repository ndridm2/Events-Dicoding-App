package com.ndridm.eventsdicodingapp.view.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ndridm.eventsdicodingapp.data.SettingPreferences
import com.ndridm.eventsdicodingapp.data.dataStore
import com.ndridm.eventsdicodingapp.data.remote.response.ListEventsItem
import com.ndridm.eventsdicodingapp.databinding.FragmentHomeBinding
import com.ndridm.eventsdicodingapp.view.adapter.CarouselAdapter
import com.ndridm.eventsdicodingapp.view.adapter.FinishedListItemAdapter
import com.ndridm.eventsdicodingapp.view.adapter.SearchAdapter
import com.ndridm.eventsdicodingapp.view.detail.DetailActivity
import com.ndridm.eventsdicodingapp.view.settings.SettingsViewModel
import com.ndridm.eventsdicodingapp.view.settings.SettingsViewModelFactory

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

    private val settingViewModel: SettingsViewModel by viewModels {
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        SettingsViewModelFactory(pref)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentHomeBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val searchVisibility = binding.searchResultList.visibility

            if (searchVisibility == View.VISIBLE) {
                binding.searchBar.clearText()
                toggleViews(isHome = true)
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
        homeViewModel.apply {
            searchResult.observe(viewLifecycleOwner) { result -> handleSearchResult(result) }
            isLoadingUpcoming.observe(viewLifecycleOwner) { isLoadUpcoming -> isLoadingUpcoming(isLoadUpcoming) }
            isLoadingFinished.observe(viewLifecycleOwner) { isLoadFinished -> isLoadingFinished(isLoadFinished) }
            eventFinishedList.observe(viewLifecycleOwner) { loadFinishedList -> setFinishListItem(loadFinishedList) }
            errorMessage.observe(viewLifecycleOwner) { showError(it) }
            eventCarousel.observe(viewLifecycleOwner) {loadCarousel ->
                if (loadCarousel != null) {
                    setCarousel(loadCarousel)
                }
            }

        }

        setupRecycleView()
        setupSearch()

        settingViewModel.getThemeSettings()
            .observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
    }

    private fun setupRecycleView() {

        val searchAdapter = SearchAdapter {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID, it.toString())
            startActivity(intent)
        }

        binding.apply {

            searchResultList.adapter = searchAdapter

            val layoutManagerSearchResult = LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false)
            searchResultList.layoutManager = layoutManagerSearchResult
            searchResultList.addItemDecoration(DividerItemDecoration(requireContext(),
                layoutManagerSearchResult.orientation)
            )

            val layoutManagerListItem = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvFinishedList.layoutManager = layoutManagerListItem
            rvFinishedList.addItemDecoration(DividerItemDecoration(requireContext(),
                layoutManagerListItem.orientation)
            )

            val layoutManagerCarousel = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            rvCarouselEvent.layoutManager = layoutManagerCarousel
            rvCarouselEvent.addItemDecoration(DividerItemDecoration(requireContext(),
                layoutManagerCarousel.orientation)
            )

        }

    }

    private fun setupSearch() {
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { v, _, _ ->
                val query = v.text.toString()
                homeViewModel.getSearchResult(query)
                searchBar.setText(query)
                searchView.hide()
                false
            }
        }
    }

    private fun handleSearchResult(searchResult: List<ListEventsItem>) {
        val searchAdapter = binding.searchResultList.adapter as SearchAdapter
        if (searchResult.isEmpty()) {
            toggleViews(isHome = false)
            binding.tvNoResult.visibility = View.VISIBLE
            searchAdapter.submitList(searchResult)
        } else {
            toggleViews(isHome = false)
            binding.tvNoResult.visibility = View.GONE
            searchAdapter.submitList(searchResult)
        }
    }

    private fun setCarousel(carouselEvent: List<ListEventsItem>) {
        val adapter = CarouselAdapter {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID, it.toString())
            startActivity(intent)
        }
        adapter.submitList(carouselEvent)
        binding.rvCarouselEvent.adapter = adapter
    }

    private fun setFinishListItem(listItem: List<ListEventsItem>) {
        val adapter = FinishedListItemAdapter {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID, it.toString())
            startActivity(intent)
        }
        adapter.submitList(listItem)
        binding.rvFinishedList.adapter = adapter
    }

    private fun isLoadingUpcoming(isLoading: Boolean) {
        binding.progressBarUpcoming.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isLoadingFinished(isLoading: Boolean) {
        binding.progressBarFinished.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show()
    }


    private fun toggleViews(isHome: Boolean) {
        val homeVisibility = if (isHome) View.VISIBLE else View.GONE
        val searchVisibility = if (isHome) View.GONE else View.VISIBLE

        with(binding) {
            tvHeadingUpcoming.visibility = homeVisibility
            rvCarouselEvent.visibility = homeVisibility
            tvHeadingFinished.visibility = homeVisibility
            rvFinishedList.visibility = homeVisibility
            searchResultList.visibility = searchVisibility
            tvNoResult.visibility = if (isHome) View.GONE else tvNoResult.visibility
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}