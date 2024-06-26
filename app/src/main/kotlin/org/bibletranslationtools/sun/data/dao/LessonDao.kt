package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Lesson

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lesson: Lesson)

    @Delete
    suspend fun delete(lesson: Lesson)

    @Update
    suspend fun update(lesson: Lesson)

    @Query("SELECT * FROM lessons")
    suspend fun getAll(): List<Lesson>

    @Query("SELECT * FROM lessons JOIN cards ON lessons.id = cards.lesson_id")
    suspend fun getAllWithCards(): Map<Lesson, List<Card>>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun get(id: String): Lesson?
}