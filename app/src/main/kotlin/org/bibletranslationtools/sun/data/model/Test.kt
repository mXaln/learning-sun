package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "tests", primaryKeys = ["id"])
data class Test(
    @ColumnInfo(name = "id")
    val id: String
) {
    @Ignore
    val sentences: List<Sentence> = listOf()
}