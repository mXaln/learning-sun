package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.Objects

@Entity(tableName = "cards", primaryKeys = ["id"])
data class Card(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "symbol")
    val symbol: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "learned")
    var learned: Boolean = false,
    @ColumnInfo(name = "passed")
    var passed: Boolean = false,
    @ColumnInfo(name = "lesson_id")
    var lessonId: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val card = other as Card
        return id == card.id &&
                symbol == card.symbol &&
                lessonId == card.lessonId
    }

    override fun hashCode(): Int {
        return Objects.hash(id, symbol, lessonId)
    }
}