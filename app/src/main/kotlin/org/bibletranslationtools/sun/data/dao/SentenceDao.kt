package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import org.bibletranslationtools.sun.data.model.Sentence
import org.bibletranslationtools.sun.data.model.SentenceWithSymbols

@Dao
interface SentenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sentence: Sentence)

    @Delete
    suspend fun delete(sentence: Sentence)

    @Update
    suspend fun update(sentence: Sentence)

    @Transaction
    @Query("SELECT * FROM sentences WHERE test_id = :testId")
    suspend fun getAll(testId: String): List<Sentence>

    @Transaction
    @Query("SELECT * FROM sentences WHERE test_id = :testId")
    suspend fun getAllWithSymbols(testId: String): List<SentenceWithSymbols>

    @Transaction
    @Query("SELECT * FROM sentences WHERE test_id = :testId AND passed = :passed")
    suspend fun getPassed(testId: String, passed: Boolean): List<Sentence>
}