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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.bibletranslationtools.sun.adapter.lesson.LessonListAdapter
import org.bibletranslationtools.sun.databinding.FragmentHomeBinding
import org.bibletranslationtools.sun.ui.viewmodels.MainViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val viewModel: MainViewModel by viewModels()
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lessonsAdapter = LessonListAdapter(requireActivity())
        binding.lessonsList.layoutManager = LinearLayoutManager(
            requireActivity(),
            RecyclerView.VERTICAL,
            false
        )
        binding.lessonsList.adapter = lessonsAdapter

        viewModel.lessons.observe(viewLifecycleOwner) {
            lessonsAdapter.submitList(it)
            lessonsAdapter.notifyDataSetChanged()
        }

        viewModel.importStudyData().invokeOnCompletion {
            refreshData()
        }

        setupSwipeRefreshLayout()

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
            Toast.makeText(requireActivity(), "Refreshed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLessons() {
        viewModel.loadLessons()
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun refreshData() {
        setupLessons()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }
}
