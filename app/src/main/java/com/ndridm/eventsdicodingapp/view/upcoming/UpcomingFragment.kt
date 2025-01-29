package com.ndridm.eventsdicodingapp.view.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ndridm.eventsdicodingapp.data.response.ListEventsItem
import com.ndridm.eventsdicodingapp.databinding.FragmentUpcomingBinding
import com.ndridm.eventsdicodingapp.view.adapter.EventAdapter
import com.ndridm.eventsdicodingapp.view.detail.DetailActivity

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private val upcomingViewModel: UpcomingViewModel by viewModels()

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

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.layoutManager = layoutManager
        binding.rvEvent.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))

        upcomingViewModel.eventList.observe(viewLifecycleOwner) { eventList ->
            if (eventList.isNotEmpty()) setUpcomingEvent(eventList) else binding.tvNoEventNow.visibility = View.VISIBLE
        }

        upcomingViewModel.isLoading.observe(viewLifecycleOwner) { load ->
            showLoading(load)
        }

    }

    private fun setUpcomingEvent(upcomingEvent: List<ListEventsItem>) {
        val adapter = EventAdapter {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID, it.toString())
            startActivity(intent)
        }
        adapter.submitList(upcomingEvent)
        binding.rvEvent.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}