package org.bibletranslationtools.sun.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.ui.activity.PART_ONE
import org.bibletranslationtools.sun.ui.activity.PART_TWO

class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CardRepository

    private val mutableCards = MutableStateFlow<List<Card>>(listOf())
    val cards: StateFlow<List<Card>> = mutableCards

    val questionDone = MutableStateFlow(false)
    val lessonId = MutableStateFlow(1)
    val part = MutableStateFlow(PART_ONE)
    val isGlobal = MutableStateFlow(false)

    init {
        val cardDao = AppDatabase.getDatabase(application).getCardDao()
        repository = CardRepository(cardDao)
    }

    fun loadLessonCards() {
        viewModelScope.launch {
            mutableCards.value = repository.getAllByLesson(lessonId.value)
                .filter {
                    when (part.value) {
                        PART_ONE, PART_TWO -> it.part == part.value
                        else -> true
                    }
                }
        }
    }

    fun loadAllPassedCards() {
        viewModelScope.launch {
            mutableCards.value = repository.getAllPassed()
        }
    }

    suspend fun updateCard(card: Card) {
        repository.update(card)
    }
}