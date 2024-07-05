package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.bibletranslationtools.sun.data.model.Card

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(card: Card)

    @Delete
    suspend fun delete(card: Card)

    @Update
    suspend fun update(card: Card)

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun get(id: String): Card?

    @Query("SELECT * FROM cards WHERE lesson_id = :lessonId")
    suspend fun getAll(lessonId: Int): List<Card>

    @Query("SELECT * FROM cards WHERE lesson_id = :lessonId AND passed = :passed")
    suspend fun getPassed(lessonId: Int, passed: Boolean): List<Card>

    @Query("SELECT * FROM cards WHERE lesson_id = :lessonId AND learned = :learned")
    suspend fun getLearned(lessonId: Int, learned: Boolean): List<Card>

    @Query("UPDATE cards SET passed = 0, learned = 0 WHERE lesson_id = :lessonId")
    suspend fun resetAll(lessonId: Int): Int

}