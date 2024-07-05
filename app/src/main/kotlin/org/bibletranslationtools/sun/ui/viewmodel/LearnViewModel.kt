package org.bibletranslationtools.sun.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.model.Card

class LearnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CardRepository
    private val mutableCards = MutableLiveData<List<Card>>()

    val cards: LiveData<List<Card>> = mutableCards

    init {
        val dao = AppDatabase.getDatabase(application).getCardDao()
        repository = CardRepository(dao)
    }

    fun loadCards(lessonId: Int): Job {
        return viewModelScope.launch {
            mutableCards.value = repository.getAll(lessonId)
        }
    }

    fun saveCard(card: Card): Job {
        return viewModelScope.launch {
            repository.update(card)
            mutableCards.value = mutableCards.value
        }
    }

}