package org.bibletranslationtools.sun.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.data.model.Sentence
import org.bibletranslationtools.sun.data.model.SentenceWithSymbols
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository

class SentenceTestViewModel(application: Application) : AndroidViewModel(application) {
    private val lessonRepository: LessonRepository
    private val repository: SentenceRepository

    val lessonId = MutableStateFlow(1)
    val sentenceDone = MutableStateFlow(false)

    private val mutableSentences = MutableStateFlow<List<SentenceWithSymbols>>(listOf())
    val sentences: StateFlow<List<SentenceWithSymbols>> = mutableSentences

    init {
        val lessonDao = AppDatabase.getDatabase(application).getLessonDao()
        lessonRepository = LessonRepository(lessonDao)

        val sentenceDao = AppDatabase.getDatabase(application).getSentenceDao()
        val symbolDao = AppDatabase.getDatabase(application).getSymbolDao()
        repository = SentenceRepository(sentenceDao, symbolDao)
    }

    fun loadSentences() {
        viewModelScope.launch {
            mutableSentences.value = repository.getAllWithSymbols(lessonId.value)
        }
    }

    suspend fun updateSentence(sentence: Sentence) {
        repository.update(sentence)
    }

    suspend fun getAllLessons(): List<Lesson> {
        return lessonRepository.getAll()
    }
}