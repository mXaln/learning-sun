package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.TestDao
import org.bibletranslationtools.sun.data.model.Test

class TestRepository(private val testDao: TestDao) {
    suspend fun insert(test: Test) {
        testDao.insert(test)
    }

    suspend fun delete(test: Test) {
        testDao.delete(test)
    }

    suspend fun update(test: Test) {
        testDao.update(test)
    }

    suspend fun get(id: String): Test? {
        return testDao.get(id)
    }
}