package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.data.model.LessonWithCards

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lesson: Lesson)

    @Delete
    suspend fun delete(lesson: Lesson)

    @Update
    suspend fun update(lesson: Lesson)

    @Transaction
    @Query("SELECT * FROM lessons")
    suspend fun getAll(): List<Lesson>

    @Transaction
    @Query("SELECT * FROM lessons")
    suspend fun getAllWithCards(): List<LessonWithCards>

    @Transaction
    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun get(id: String): Lesson?
}