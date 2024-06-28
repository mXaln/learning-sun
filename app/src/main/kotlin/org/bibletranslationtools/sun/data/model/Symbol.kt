package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "symbols")
data class Symbol(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "sort")
    val sort: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "sentence_id")
    var sentenceId: String? = null
) {
    @Ignore
    var selected = false
    @Ignore
    var correct: Boolean? = null
}
