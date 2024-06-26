package org.bibletranslationtools.sun.ui.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.adapter.flashcard.LessonsAdapter
import org.bibletranslationtools.sun.databinding.FragmentHomeBinding
import org.bibletranslationtools.sun.ui.viewmodels.MainViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLessons()
        setupVisibility()
        setupSwipeRefreshLayout()

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
            Toast.makeText(requireActivity(), "Refreshed", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupLessons() {
        viewModel.loadLessons()
        val linearLayoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.setsRv.layoutManager = linearLayoutManager
        val setsAdapter = LessonsAdapter(requireActivity())

        viewModel.lessons.observe(viewLifecycleOwner) {
            setsAdapter.submitList(it)
        }

        binding.setsRv.adapter = setsAdapter
        setsAdapter.notifyDataSetChanged()
    }

    private fun setupVisibility() {
        if (viewModel.lessons.value?.isEmpty() == true) {
            binding.setsCl.visibility = View.GONE
        } else {
            binding.setsCl.visibility = View.VISIBLE
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun refreshData() {
        setupLessons()
        setupVisibility()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }
}
