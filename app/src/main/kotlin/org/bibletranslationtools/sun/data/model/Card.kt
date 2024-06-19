package org.bibletranslationtools.sun.data.model

import java.util.Objects

enum class Status(val value: Int) {
    IDLE(0),
    LEARNED(1),
    NOT_LEARNED(2);

    companion object {
        private val map = entries.toTypedArray().associateBy { it.value }

        /** @throws IllegalArgumentException */
        fun of(status: Int) =
            map[status]
                ?: throw IllegalArgumentException("Status $status not supported")
    }
}

data class Card(
    val id: String,
    val symbol: String,
    val variations: List<String>,
    var status: Status = Status.IDLE,
    var isLearned: Boolean = false,
    var lessonId: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val card = other as Card
        return id == card.id && symbol == card.symbol && lessonId == card.lessonId
    }

    override fun hashCode(): Int {
        return Objects.hash(id, lessonId, symbol)
    }
}