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

class LessonViewModel(application: Application) : AndroidViewModel(application) {
    val cards: LiveData<List<Card>> get() = mutableCards
    private val mutableCards = MutableLiveData<List<Card>>()
    private val repository: CardRepository

    init {
        val dao = AppDatabase.getDatabase(application).getCardDao()
        repository = CardRepository(dao)
    }

    fun loadCards(lessonId: String) {
        viewModelScope.launch {
            mutableCards.value = repository.getAll(lessonId)
        }
    }

    suspend fun resetCards(lessonId: String): Int {
        return repository.resetAll(lessonId)
    }
}