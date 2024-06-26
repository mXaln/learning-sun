package org.bibletranslationtools.sun.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
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

    private val lessonIdLiveData = MutableLiveData<String?>()
    val lessonId get() = lessonIdLiveData

    init {
        val dao = AppDatabase.getDatabase(application).getCardDao()
        repository = CardRepository(dao)
    }

    fun loadCards(): Job {
        return viewModelScope.launch {
            lessonId.value?.let {
                mutableCards.value = repository.getAll(it)
            }
        }
    }

    fun saveCard(card: Card): Job {
        return viewModelScope.launch {
            repository.update(card)
            mutableCards.value = mutableCards.value
        }
    }

    fun setLessonId(lessonId: String?) {
        lessonIdLiveData.value = lessonId
    }

}