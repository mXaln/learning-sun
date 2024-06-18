package org.bibletranslationtools.sun.data.model

import java.util.Objects
import java.util.UUID

enum class Status(val value: Int) {
    INITIAL(0),
    RIGHT(1),
    LEFT(2);

    companion object {
        private val map = entries.toTypedArray().associateBy { it.value }

        /** @throws IllegalArgumentException */
        fun of(status: Int) =
            map[status]
                ?: throw IllegalArgumentException("Status $status not supported")
    }
}

data class Card(
    val id: String = UUID.randomUUID().toString(),
    val flashcardId: String? = null,
    var front: String? = null,
    var back: String? = null,
    var status: Status = Status.INITIAL,
    var isLearned: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val card = other as Card
        return (id == card.id) &&
                (front == card.front) &&
                (back == card.back)
    }

    override fun hashCode(): Int {
        return Objects.hash(id, front, back)
    }
}