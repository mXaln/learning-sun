package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import java.util.Objects

@Entity(tableName = "lessons", primaryKeys = ["id"])
data class Lesson(
    @ColumnInfo(name = "id")
    val id: String
) {
    @Ignore
    val cards: List<Card> = listOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val lesson = other as Lesson
        return lesson.id == id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}