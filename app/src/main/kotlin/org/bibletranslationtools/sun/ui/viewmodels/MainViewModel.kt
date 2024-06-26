package org.bibletranslationtools.sun.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.data.model.Setting

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val cardRepository: CardRepository
    private val lessonRepository: LessonRepository
    private val settingsRepository: SettingsRepository

    val lessons: LiveData<List<Lesson>> get() = mutableLessons
    private val mutableLessons = MutableLiveData<List<Lesson>>()

    init {
        val cardDao = AppDatabase.getDatabase(application).getCardDao()
        cardRepository = CardRepository(cardDao)
        val lessonDao = AppDatabase.getDatabase(application).getLessonDao()
        lessonRepository = LessonRepository(lessonDao)
        val settingsDao = AppDatabase.getDatabase(application).getSettingDao()
        settingsRepository = SettingsRepository(settingsDao)
    }

    fun insertCard(card: Card) {
        viewModelScope.launch {
            cardRepository.insert(card)
        }
    }

    fun loadLessons() {
        viewModelScope.launch {
            mutableLessons.value = lessonRepository.getAllWithCards()
        }
    }

    fun insertLesson(lesson: Lesson) {
        viewModelScope.launch {
            lessonRepository.insert(lesson)
        }
    }

    suspend fun getDatabaseVersion(): Int? {
        return settingsRepository.get("version")?.value?.toInt()
    }

    suspend fun insertSetting(setting: Setting) {
        settingsRepository.insert(setting)
    }
}