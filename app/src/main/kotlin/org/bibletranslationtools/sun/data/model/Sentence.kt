package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "sentences", primaryKeys = ["id"])
data class Sentence (
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "correct")
    val correct: String,
    @ColumnInfo(name = "incorrect")
    val incorrect: String,
    @ColumnInfo(name = "passed")
    var passed: Boolean = false,
    @ColumnInfo(name = "test_id")
    var testId: String? = null
) {
    @Ignore
    val symbols: List<Symbol> = listOf()
}