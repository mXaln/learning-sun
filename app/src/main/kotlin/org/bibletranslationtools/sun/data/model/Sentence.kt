package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import java.util.Objects

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val sentence = other as Sentence
        return id == sentence.id &&
                correct == sentence.correct &&
                incorrect == sentence.incorrect &&
                passed == sentence.passed &&
                testId == sentence.testId
    }

    override fun hashCode(): Int {
        return Objects.hash(id, correct, incorrect, passed, testId)
    }
}