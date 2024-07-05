package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import org.bibletranslationtools.sun.data.model.Test

@Dao
interface TestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(test: Test)

    @Delete
    suspend fun delete(test: Test)

    @Update
    suspend fun update(test: Test)

    @Transaction
    @Query("SELECT * FROM tests WHERE id = :id")
    suspend fun get(id: String): Test?

}