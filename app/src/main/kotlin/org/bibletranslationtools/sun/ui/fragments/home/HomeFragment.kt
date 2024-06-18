package org.bibletranslationtools.sun.ui.fragments.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.adapter.flashcard.SetsAdapter
import org.bibletranslationtools.sun.data.dao.FlashCardDAO
import org.bibletranslationtools.sun.data.model.FlashCard
import org.bibletranslationtools.sun.databinding.FragmentHomeBinding
import org.bibletranslationtools.sun.ui.activities.search.ViewSearchActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val flashCards = arrayListOf<FlashCard>()
    private val flashCardDAO by lazy { FlashCardDAO(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFlashCards()
        setupVisibility()
        setupSwipeRefreshLayout()
        setupSearchBar()

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
            Toast.makeText(requireActivity(), "Refreshed", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupFlashCards() {
        flashCards.clear()
        flashCards.addAll(flashCardDAO.getAllFlashCards())
        val linearLayoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.setsRv.layoutManager = linearLayoutManager
        val setsAdapter = SetsAdapter(requireActivity(), flashCards, false)
        binding.setsRv.adapter = setsAdapter
        setsAdapter.notifyDataSetChanged()
    }

    private fun setupVisibility() {
        if (flashCards.isEmpty()) {
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

    private fun setupSearchBar() {
        binding.searchBar.setOnClickListener {
            val intent = Intent(requireActivity(), ViewSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun refreshData() {
        setupFlashCards()
        setupVisibility()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }
}
