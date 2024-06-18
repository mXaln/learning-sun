package org.bibletranslationtools.sun.ui.activities.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import org.bibletranslationtools.sun.adapter.flashcard.SetAllAdapter
import org.bibletranslationtools.sun.data.dao.FlashCardDAO
import org.bibletranslationtools.sun.data.model.FlashCard
import org.bibletranslationtools.sun.databinding.ActivityViewSearchBinding
import java.util.Locale

class ViewSearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivityViewSearchBinding.inflate(layoutInflater) }
    private val flashCards = arrayListOf<FlashCard>()
    private val flashCardDAO by lazy { FlashCardDAO(this) }
    private val setAllAdapter by lazy { SetAllAdapter(this, flashCards) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setupBackButton()
        setupSets()
        setupSearchView()
    }

    private fun setupBackButton() {
        binding.backIv.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSets() {
        flashCards.clear()
        flashCards.addAll(flashCardDAO.getAllFlashCards())

        binding.setsRv.layoutManager = LinearLayoutManager(this)
        binding.setsRv.adapter = setAllAdapter
        binding.setsCl.visibility =
            if (flashCards.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                handleSearchQuery(newText)
                return true
            }
        })
    }

    private fun handleSearchQuery(newText: String) {
        val filteredFlashCards = ArrayList<FlashCard>()
        for (flashCard in flashCards) {
            if (flashCard.name?.lowercase(Locale.getDefault())
                    ?.contains(newText.lowercase(Locale.getDefault())) == true
            ) {
                filteredFlashCards.add(flashCard)
            }
        }
        updateAdapters(filteredFlashCards)
        updateVisibility(newText, filteredFlashCards)
    }

    private fun updateAdapters(flashCards: ArrayList<FlashCard>) {
        binding.setsRv.adapter = setAllAdapter
    }

    private fun updateVisibility(newText: String, flashCards: ArrayList<FlashCard>) {
        val isSearchEmpty = newText.isEmpty()
        val isFlashCardsEmpty = flashCards.isEmpty()

        binding.setsCl.visibility =
            if (isSearchEmpty || isFlashCardsEmpty) View.GONE else View.VISIBLE
        binding.enterTopicTv.visibility =
            if (isSearchEmpty) View.VISIBLE else View.GONE
        binding.noResultTv.visibility =
            if (isSearchEmpty || !isFlashCardsEmpty) View.GONE else View.VISIBLE
    }
}