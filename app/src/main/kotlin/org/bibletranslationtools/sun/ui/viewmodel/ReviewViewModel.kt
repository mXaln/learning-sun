package org.bibletranslationtools.sun.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.model.Card

class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CardRepository

    val questionDone = MutableLiveData(false)

    init {
        val cardDao = AppDatabase.getDatabase(application).getCardDao()
        repository = CardRepository(cardDao)
    }

    suspend fun getAllCards(lessonId: Int): List<Card> {
        return repository.getAll(lessonId)
    }

    suspend fun getPassedCards(lessonId: Int, passed: Boolean): List<Card> {
        return repository.getPassed(lessonId, passed)
    }

    suspend fun updateCard(card: Card) {
        repository.update(card)
    }
}