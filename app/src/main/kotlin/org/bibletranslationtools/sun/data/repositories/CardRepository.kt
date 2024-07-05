package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.CardDao
import org.bibletranslationtools.sun.data.model.Card

class CardRepository(private val cardDao: CardDao) {

    suspend fun insert(card: Card) {
        cardDao.insert(card)
    }

    suspend fun delete(card: Card) {
        cardDao.delete(card)
    }

    suspend fun update(card: Card) {
        cardDao.update(card)
    }

    suspend fun get(id: String): Card? {
        return cardDao.get(id)
    }

    suspend fun getAll(lessonId: Int): List<Card> {
        return cardDao.getAll(lessonId)
    }

    suspend fun getPassed(lessonId: Int, passed: Boolean): List<Card> {
        return cardDao.getPassed(lessonId, passed)
    }

    suspend fun getLearned(lessonId: Int, learned: Boolean): List<Card> {
        return cardDao.getLearned(lessonId, learned)
    }

    suspend fun resetAll(lessonId: Int): Int {
        return cardDao.resetAll(lessonId)
    }
}