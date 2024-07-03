package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.data.model.Sentence
import java.util.Objects

data class LessonModel(
    val lesson: Lesson,
    val cards: List<Card>,
    val sentences: List<Sentence>
) {
    var isAvailable = false
    var isSelected = false

    val cardsLearned get() = cards.count { it.learned }
    val cardsLearnedProgress get() = cardsLearned.toDouble() / cards.size * 100

    val cardsPassed get() = cards.count { it.passed }
    val cardsPassedProgress get() = cardsPassed.toDouble() / cards.size * 100

    val sentencesPassed get() = sentences.count { it.passed }
    val sentencesPassedProgress get() = sentencesPassed.toDouble() / sentences.size * 100

    val totalProgress: Double
        get() {
            // Cards size times 2, because we have learned and passed cards
            val total = (cards.size * 2) + sentences.size
            val completed = cardsLearned + cardsPassed + sentencesPassed
            return (completed.toDouble() / total) * 100
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val lessonModel = other as LessonModel
        return lesson == lessonModel.lesson &&
                totalProgress == lessonModel.totalProgress &&
                isAvailable == lessonModel.isAvailable &&
                isSelected == lessonModel.isSelected
    }

    override fun hashCode(): Int {
        return Objects.hash(lesson, totalProgress, isAvailable, isSelected)
    }
}