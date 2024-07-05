package org.bibletranslationtools.sun.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.data.model.LessonSuite
import org.bibletranslationtools.sun.data.model.Sentence
import org.bibletranslationtools.sun.data.model.Setting
import org.bibletranslationtools.sun.data.model.Symbol
import org.bibletranslationtools.sun.data.model.Test
import org.bibletranslationtools.sun.data.model.TestSuite
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.TestRepository
import org.bibletranslationtools.sun.ui.mapper.LessonMapper
import org.bibletranslationtools.sun.ui.model.LessonModel
import org.bibletranslationtools.sun.utils.AssetsProvider

class MainViewModel(private val application: Application) : AndroidViewModel(application) {
    private val cardRepository: CardRepository
    private val lessonRepository: LessonRepository
    private val settingsRepository: SettingsRepository
    private val testRepository: TestRepository
    private val sentenceRepository: SentenceRepository

    val lessons: LiveData<List<LessonModel>> get() = mutableLessons
    private val mutableLessons = MutableLiveData<List<LessonModel>>()

    private val ioScope = CoroutineScope(Dispatchers.IO)

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

    fun loadLessons(): Job {
        return viewModelScope.launch {
            val lessons = lessonRepository.getAllWithData().map(LessonMapper::map)
            lessons.forEachIndexed { index, lesson ->
                lesson.isAvailable = lessonAvailable(lessons, index)
            }
            mutableLessons.value = lessons
        }
    }

    fun importStudyData(): Job {
        return ioScope.launch {
            importLessons()
            importTests()
        }
    }

    private suspend fun importLessons() {
        val mapper = ObjectMapper().registerKotlinModule()
        val reference = object : TypeReference<LessonSuite>() {}
        val json = AssetsProvider.readText(application, "lessons.json")

        val dbVersion = getLessonsVersion() ?: 0

        json?.let {
            val lessonSuite = mapper.readValue(it, reference)

            if (lessonSuite.version > dbVersion) {
                for (lesson in lessonSuite.lessons) {
                    insertLesson(lesson)
                    for (card in lesson.cards) {
                        card.lessonId = lesson.id
                        insertCard(card)
                    }
                }

                insertSetting(
                    Setting("lessonsVersion", lessonSuite.version.toString())
                )
            }
        }
    }

    private suspend fun importTests() {
        val mapper = ObjectMapper().registerKotlinModule()
        val reference = object : TypeReference<TestSuite>() {}
        val json = AssetsProvider.readText(application, "tests.json")

        val dbVersion = getTestsVersion() ?: 0

        json?.let {
            val testSuite = mapper.readValue(it, reference)

            if (testSuite.version > dbVersion) {
                for (test in testSuite.tests) {
                    insertTest(test)
                    for (sentence in test.sentences) {
                        sentence.testId = test.id
                        insertSentence(sentence)
                        for (symbol in sentence.symbols) {
                            symbol.sentenceId = sentence.id
                            insertSymbol(symbol)
                        }
                    }
                }

                insertSetting(Setting("testsVersion", testSuite.version.toString()))
            }
        }
    }

    private fun insertCard(card: Card) {
        viewModelScope.launch {
            val cardExists = cardRepository.get(card.id) != null
            if (!cardExists) {
                cardRepository.insert(card)
            }
        }
    }

    private fun insertLesson(lesson: Lesson) {
        viewModelScope.launch {
            val lessonExists = lessonRepository.get(lesson.id) != null
            if (!lessonExists) {
                lessonRepository.insert(lesson)
            }
        }
    }

    private fun insertTest(test: Test) {
        viewModelScope.launch {
            val testExists = testRepository.get(test.id) != null
            if (!testExists) {
                testRepository.insert(test)
            }
        }
    }

    private fun insertSentence(sentence: Sentence) {
        viewModelScope.launch {
            val sentenceExists = sentenceRepository.get(sentence.id) != null
            if (!sentenceExists) {
                sentenceRepository.insert(sentence)
            }
        }
    }

    private fun insertSymbol(symbol: Symbol) {
        viewModelScope.launch {
            sentenceRepository.insert(symbol)
        }
    }

    private suspend fun getLessonsVersion(): Int? {
        return settingsRepository.get("lessonsVersion")?.value?.toInt()
    }

    private suspend fun getTestsVersion(): Int? {
        return settingsRepository.get("testsVersion")?.value?.toInt()
    }

    private suspend fun insertSetting(setting: Setting) {
        settingsRepository.insert(setting)
    }

    private fun lessonAvailable(lessons: List<LessonModel>, position: Int): Boolean {
        if (position == 0) return true
        val prevLesson = lessons[position - 1]
        return prevLesson.totalProgress == 100.0
    }
}