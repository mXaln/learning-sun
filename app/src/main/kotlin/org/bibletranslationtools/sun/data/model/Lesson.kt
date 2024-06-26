package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "lessons", primaryKeys = ["id"])
data class Lesson @JvmOverloads constructor(
    @ColumnInfo(name = "id")
    val id: String,
    @Ignore
    val cards: List<Card> = listOf()
)