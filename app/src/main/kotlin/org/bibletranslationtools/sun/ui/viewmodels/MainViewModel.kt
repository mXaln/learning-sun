package org.bibletranslationtools.sun.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.data.model.LessonWithCards
import org.bibletranslationtools.sun.data.model.Sentence
import org.bibletranslationtools.sun.data.model.Setting
import org.bibletranslationtools.sun.data.model.Symbol
import org.bibletranslationtools.sun.data.model.Test
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.TestRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val cardRepository: CardRepository
    private val lessonRepository: LessonRepository
    private val settingsRepository: SettingsRepository
    private val testRepository: TestRepository
    private val sentenceRepository: SentenceRepository

    val lessons: LiveData<List<LessonWithCards>> get() = mutableLessons
    private val mutableLessons = MutableLiveData<List<LessonWithCards>>()

    init {
        val cardDao = AppDatabase.getDatabase(application).getCardDao()
        cardRepository = CardRepository(cardDao)
        val lessonDao = AppDatabase.getDatabase(application).getLessonDao()
        lessonRepository = LessonRepository(lessonDao)
        val settingsDao = AppDatabase.getDatabase(application).getSettingDao()
        settingsRepository = SettingsRepository(settingsDao)
        val testDao = AppDatabase.getDatabase(application).getTestDao()
        testRepository = TestRepository(testDao)
        val sentenceDao = AppDatabase.getDatabase(application).getSentenceDao()
        val symbolDao = AppDatabase.getDatabase(application).getSymbolDao()
        sentenceRepository = SentenceRepository(sentenceDao, symbolDao)
    }

    fun insertCard(card: Card) {
        viewModelScope.launch {
            cardRepository.insert(card)
        }
    }

    fun loadLessons(): Job {
        return viewModelScope.launch {
            mutableLessons.value = lessonRepository.getAllWithCards()
        }
    }

    fun insertLesson(lesson: Lesson) {
        viewModelScope.launch {
            lessonRepository.insert(lesson)
        }
    }

    fun insertTest(test: Test) {
        viewModelScope.launch {
            testRepository.insert(test)
        }
    }

    fun insertSentence(sentence: Sentence) {
        viewModelScope.launch {
            sentenceRepository.insert(sentence)
        }
    }

    fun insertSymbol(symbol: Symbol) {
        viewModelScope.launch {
            sentenceRepository.insert(symbol)
        }
    }

    suspend fun getLessonsVersion(): Int? {
        return settingsRepository.get("lessonsVersion")?.value?.toInt()
    }

    suspend fun getTestsVersion(): Int? {
        return settingsRepository.get("testsVersion")?.value?.toInt()
    }

    suspend fun insertSetting(setting: Setting) {
        settingsRepository.insert(setting)
    }
}