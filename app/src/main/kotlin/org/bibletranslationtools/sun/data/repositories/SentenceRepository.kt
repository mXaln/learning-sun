package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.SentenceDao
import org.bibletranslationtools.sun.data.dao.SymbolDao
import org.bibletranslationtools.sun.data.model.Sentence
import org.bibletranslationtools.sun.data.model.SentenceWithSymbols
import org.bibletranslationtools.sun.data.model.Symbol

class SentenceRepository(
    private val sentenceDao: SentenceDao,
    private val symbolDao: SymbolDao
) {
    suspend fun insert(sentence: Sentence) {
        sentenceDao.insert(sentence)
    }

    suspend fun delete(sentence: Sentence) {
        sentenceDao.delete(sentence)
    }

    suspend fun update(sentence: Sentence) {
        sentenceDao.update(sentence)
    }

    suspend fun insert(symbol: Symbol) {
        symbolDao.insert(symbol)
    }

    suspend fun getAll(testId: String): List<Sentence> {
        return sentenceDao.getAll(testId)
    }

    suspend fun getAllWithSymbols(testId: String): List<SentenceWithSymbols> {
        return sentenceDao.getAllWithSymbols(testId)
    }

    suspend fun getPassed(testId: String, passed: Boolean): List<Sentence> {
        return sentenceDao.getPassed(testId, passed)
    }

}