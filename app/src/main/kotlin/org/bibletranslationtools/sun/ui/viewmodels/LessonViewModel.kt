package org.bibletranslationtools.sun.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.repositories.SentenceRepository

class LessonViewModel(application: Application) : AndroidViewModel(application) {
    val cards: LiveData<List<Card>> get() = mutableCards
    private val mutableCards = MutableLiveData<List<Card>>()
    private val cardRepository: CardRepository
    private val sentenceRepository: SentenceRepository

    init {
        val dao = AppDatabase.getDatabase(application).getCardDao()
        cardRepository = CardRepository(dao)
        val sentenceDao = AppDatabase.getDatabase(application).getSentenceDao()
        val symbolDao = AppDatabase.getDatabase(application).getSymbolDao()
        sentenceRepository = SentenceRepository(sentenceDao, symbolDao)
    }

    fun loadCards(lessonId: String) {
        viewModelScope.launch {
            mutableCards.value = cardRepository.getAll(lessonId)
        }
    }

    suspend fun resetLesson(lessonId: String): Int {
        return cardRepository.resetAll(lessonId) +
                sentenceRepository.resetAll(lessonId)
    }
}