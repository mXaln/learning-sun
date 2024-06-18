package org.bibletranslationtools.sun.ui.fragments.library

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.adapter.flashcard.SetCopyAdapter
import org.bibletranslationtools.sun.data.dao.FlashCardDAO
import org.bibletranslationtools.sun.data.model.FlashCard
import org.bibletranslationtools.sun.databinding.FragmentStudySetsBinding
import org.bibletranslationtools.sun.ui.activities.create.CreateSetActivity

class StudySetsFragment : Fragment() {
    private lateinit var binding: FragmentStudySetsBinding
    private val flashCards = arrayListOf<FlashCard>()
    private val flashCardDAO by lazy { FlashCardDAO(requireActivity()) }
    private val setsAdapter by lazy { SetCopyAdapter(requireActivity(), flashCards) }
    private val idUser: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudySetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        binding.createSetBtn.setOnClickListener {
            startActivity(
                Intent(
                    activity, CreateSetActivity::class.java
                )
            )
        }
        flashCards.clear()
        flashCards.addAll(flashCardDAO.getAllFlashCards())
        updateVisibility()
        setupRecyclerView()
    }

    private fun updateVisibility() {
        if (flashCards.isEmpty()) {
            binding.setsCl.visibility = View.VISIBLE
            binding.setsRv.visibility = View.GONE
        } else {
            binding.setsCl.visibility = View.GONE
            binding.setsRv.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.setsRv.layoutManager = linearLayoutManager
        binding.setsRv.adapter = setsAdapter
        setsAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshData() {
        flashCards.clear()
        flashCards.addAll(flashCardDAO.getAllFlashCards())
        setsAdapter.notifyDataSetChanged()
        updateVisibility()
    }
}