package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.LessonDao
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.data.model.LessonWithCards

class LessonRepository(private val lessonDao: LessonDao) {
    suspend fun insert(lesson: Lesson) {
        lessonDao.insert(lesson)
    }

    suspend fun delete(lesson: Lesson) {
        lessonDao.delete(lesson)
    }

    suspend fun update(lesson: Lesson) {
        lessonDao.update(lesson)
    }

    suspend fun getAll(): List<Lesson> {
        return lessonDao.getAll()
    }

    suspend fun getAllWithCards(): List<LessonWithCards> {
        return lessonDao.getAllWithCards()
    }

    suspend fun get(id: String): Lesson? {
        return lessonDao.get(id)
    }
}