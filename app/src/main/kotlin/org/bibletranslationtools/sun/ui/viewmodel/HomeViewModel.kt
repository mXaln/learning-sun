package org.bibletranslationtools.sun.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.data.model.LessonSuite
import org.bibletranslationtools.sun.data.model.Sentence
import org.bibletranslationtools.sun.data.model.Setting
import org.bibletranslationtools.sun.data.model.Symbol
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.utils.AssetsProvider

class HomeViewModel(private val application: Application) : AndroidViewModel(application) {
    private val cardRepository: CardRepository
    private val lessonRepository: LessonRepository
    private val settingsRepository: SettingsRepository
    private val sentenceRepository: SentenceRepository

    init {
        val cardDao = AppDatabase.getDatabase(application).getCardDao()
        cardRepository = CardRepository(cardDao)
        val lessonDao = AppDatabase.getDatabase(application).getLessonDao()
        lessonRepository = LessonRepository(lessonDao)
        val sentenceDao = AppDatabase.getDatabase(application).getSentenceDao()
        val symbolDao = AppDatabase.getDatabase(application).getSymbolDao()
        sentenceRepository = SentenceRepository(sentenceDao, symbolDao)
        val settingsDao = AppDatabase.getDatabase(application).getSettingDao()
        settingsRepository = SettingsRepository(settingsDao)
    }

    fun importLessons(): Job {
        return viewModelScope.launch {
            val mapper = ObjectMapper().registerKotlinModule()
            val reference = object : TypeReference<LessonSuite>() {}
            val json = AssetsProvider.readText(application, "lessons.json")

            val dbVersion = getVersion() ?: 0

            json?.let {
                val lessonSuite = mapper.readValue(it, reference)

                if (lessonSuite.version > dbVersion) {
                    for (lesson in lessonSuite.lessons) {
                        insertLesson(lesson)

                        for (card in lesson.cards) {
                            card.lessonId = lesson.id
                            insertCard(card)
                        }

                        for (sentence in lesson.sentences) {
                            sentence.lessonId = lesson.id
                            insertSentence(sentence)
                            for (symbol in sentence.symbols) {
                                symbol.sentenceId = sentence.id
                                insertSymbol(symbol)
                            }
                        }
                    }

                    insertSetting(
                        Setting("version", lessonSuite.version.toString())
                    )
                }
            }
        }
    }

    private suspend fun insertLesson(lesson: Lesson) {
        lessonRepository.insert(lesson)
    }

    private suspend fun insertCard(card: Card) {
        cardRepository.insert(card)
    }

    private fun insertSentence(sentence: Sentence) {
        viewModelScope.launch {
            sentenceRepository.insert(sentence)
        }
    }

    private fun insertSymbol(symbol: Symbol) {
        viewModelScope.launch {
            sentenceRepository.insert(symbol)
        }
    }

    private suspend fun getVersion(): Int? {
        return settingsRepository.get("version")?.value?.toInt()
    }

    private suspend fun insertSetting(setting: Setting) {
        settingsRepository.insert(setting)
    }
}