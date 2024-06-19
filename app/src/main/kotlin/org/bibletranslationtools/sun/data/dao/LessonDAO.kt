package org.bibletranslationtools.sun.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.util.Log
import org.bibletranslationtools.sun.data.QMDatabaseHelper
import org.bibletranslationtools.sun.data.model.Lesson

class LessonDAO(context: Context) {
    private val qmDatabaseHelper = QMDatabaseHelper(context)
    private val database = qmDatabaseHelper.writableDatabase

    fun insertLesson(lesson: Lesson): Long {
        val contentValues = ContentValues()

        //put
        contentValues.put("id", lesson.id)

        return try {
            database.insert(QMDatabaseHelper.TABLE_LESSONS, null, contentValues)
        } catch (e: SQLException) {
            Log.e("LessonDAO", "createLesson: $e")
            -1
        }
    }

    fun getLesson(id: Int): Lesson? {
        var lesson: Lesson? = null

        try {
            database.query(
                QMDatabaseHelper.TABLE_LESSONS,
                null,
                "${QMDatabaseHelper.TABLE_LESSONS_ID}=?",
                arrayOf(id.toString()),
                null,
                null,
                null
            ).use { cursor ->
                with(cursor) {
                    if (moveToFirst()) {
                        lesson = Lesson(
                            getString(cursor.getColumnIndexOrThrow(QMDatabaseHelper.TABLE_LESSONS_ID)),
                            listOf()
                        )
                    }
                }
            }
        } catch (e: SQLException) {
            Log.e("LessonDAO", "getLesson: $e")
        }

        return lesson
    }

    fun getLessons(): ArrayList<Lesson> {
        val lessons = arrayListOf<Lesson>()

        try {
            database.query(
                QMDatabaseHelper.TABLE_LESSONS,
                null,
                null,
                null,
                null,
                null,
                null
            ).use { cursor ->
                with(cursor) {
                    if (moveToFirst()) {
                        do {
                            val lesson = Lesson(
                                cursor.getString(cursor.getColumnIndexOrThrow(QMDatabaseHelper.TABLE_LESSONS_ID)),
                                listOf()
                            )
                            lessons.add(lesson)
                        } while (moveToNext())
                    }
                }
            }
        } catch (e: SQLException) {
            Log.e("LessonDAO", "getLessons: $e")
        }
        return lessons
    }

    fun close() {
        qmDatabaseHelper.close()
    }
}