package org.bibletranslationtools.sun.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.model.Sentence
import org.bibletranslationtools.sun.data.model.SentenceWithSymbols
import org.bibletranslationtools.sun.data.repositories.SentenceRepository

class TestViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SentenceRepository

    val sentenceDone = MutableLiveData(false)

    init {
        val sentenceDao = AppDatabase.getDatabase(application).getSentenceDao()
        val symbolDao = AppDatabase.getDatabase(application).getSymbolDao()
        repository = SentenceRepository(sentenceDao, symbolDao)
    }

    suspend fun getAllSentences(testId: String): List<SentenceWithSymbols> {
        return repository.getAllWithSymbols(testId)
    }

    suspend fun getPassedSentences(testId: String, passed: Boolean): List<Sentence> {
        return repository.getPassed(testId, passed)
    }

    suspend fun updateSentence(sentence: Sentence) {
        repository.update(sentence)
    }
}