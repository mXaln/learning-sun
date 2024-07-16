package org.bibletranslationtools.sun.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.data.model.Sentence
import org.bibletranslationtools.sun.data.model.SentenceWithSymbols
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository

class SentenceTestViewModel(application: Application) : AndroidViewModel(application) {
    private val lessonRepository: LessonRepository
    private val sentenceRepository: SentenceRepository
    private val cardsRepository: CardRepository

    val lessonId = MutableStateFlow(1)
    val sentenceDone = MutableStateFlow(false)
    val isGlobal = MutableStateFlow(false)

    private val mutableSentences = MutableStateFlow<List<SentenceWithSymbols>>(listOf())
    val sentences: StateFlow<List<SentenceWithSymbols>> = mutableSentences

    private val mutableCards = MutableStateFlow<List<Card>>(listOf())
    val cards: StateFlow<List<Card>> = mutableCards

    init {
        val lessonDao = AppDatabase.getDatabase(application).getLessonDao()
        lessonRepository = LessonRepository(lessonDao)

        val sentenceDao = AppDatabase.getDatabase(application).getSentenceDao()
        val symbolDao = AppDatabase.getDatabase(application).getSymbolDao()
        sentenceRepository = SentenceRepository(sentenceDao, symbolDao)

        val cardDao = AppDatabase.getDatabase(application).getCardDao()
        cardsRepository = CardRepository(cardDao)
    }

    fun loadSentences() {
        viewModelScope.launch {
            mutableSentences.value = sentenceRepository.getAllWithSymbols(lessonId.value)
        }
    }

    fun loadAllPassedSentences() {
        viewModelScope.launch {
            mutableSentences.value = sentenceRepository.getAllPassedWithSymbols()
        }
    }

    suspend fun updateSentence(sentence: Sentence) {
        sentenceRepository.update(sentence)
    }

    suspend fun getAllLessons(): List<Lesson> {
        return lessonRepository.getAll()
    }

    suspend fun getAllCards(): List<Card> {
        return cardsRepository.getAllByLesson(lessonId.value)
    }
}