package org.bibletranslationtools.sun.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.util.Log
import org.bibletranslationtools.sun.data.QMDatabaseHelper
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Status

class CardDAO(context: Context) {

    private val qmDatabaseHelper = QMDatabaseHelper(context)
    private val database = qmDatabaseHelper.writableDatabase

    fun insertCard(card: Card): Long {
        val contentValues = ContentValues()

        val learned = if (card.isLearned) 1 else 0

        contentValues.put(QMDatabaseHelper.TABLE_CARDS_ID, card.id)
        contentValues.put(QMDatabaseHelper.TABLE_CARDS_SYMBOL, card.symbol)
        contentValues.put(QMDatabaseHelper.TABLE_CARDS_STATUS, card.status.value)
        contentValues.put(QMDatabaseHelper.TABLE_CARDS_IS_LEARNED, learned)
        contentValues.put(QMDatabaseHelper.TABLE_CARDS_VARIATIONS, card.variations.joinToString(","))
        contentValues.put(QMDatabaseHelper.TABLE_CARDS_PARENT_FK, card.lessonId)

        return try {
            database.insert(
                QMDatabaseHelper.TABLE_CARDS,
                null,
                contentValues
            )
        } catch (e: SQLException) {
            Log.e("CardDAO", "insertCard: $e")
            -1
        }
    }

    fun countLessonCards(lessonId: String): Int {
        return try {
            database.query(
                QMDatabaseHelper.TABLE_CARDS,
                null,
                "${QMDatabaseHelper.TABLE_CARDS_PARENT_FK}=?",
                arrayOf(lessonId),
                null,
                null,
                null
            ).use { cursor ->
                cursor.count
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "countLessonCards: $e")
        }
    }

    fun getLessonCards(lessonId: String): ArrayList<Card> {
        return try {
            database.query(
                QMDatabaseHelper.TABLE_CARDS,
                null,
                "${QMDatabaseHelper.TABLE_CARDS_PARENT_FK}=?",
                arrayOf(lessonId),
                null,
                null,
                null
            ).use { cursor ->
                getCardsFromCursor(cursor)
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "getLessonCards: $e")
            arrayListOf()
        }
    }

    fun getLessonCardByStatus(lessonId: String): ArrayList<Card> {
        return try {
            database.query(
                QMDatabaseHelper.TABLE_CARDS,
                null,
                "${QMDatabaseHelper.TABLE_CARDS_PARENT_FK}=? AND ${QMDatabaseHelper.TABLE_CARDS_STATUS}!=?",
                arrayOf(lessonId, Status.NOT_LEARNED.value.toString()),
                null,
                null,
                null
            ).use { cursor ->
                getCardsFromCursor(cursor)
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "getLessonCardByStatus: $e")
            arrayListOf()
        }
    }

    fun updateCardStatus(id: String, status: Status): Int {
        val contentValues = ContentValues()

        contentValues.put(QMDatabaseHelper.TABLE_CARDS_STATUS, status.value)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_CARDS,
                contentValues,
                "${QMDatabaseHelper.TABLE_CARDS_ID}=?",
                arrayOf(id)
            )
        } catch (e: SQLException) {
            Log.e("CardDAO", "updateCardStatus: $e")
            0
        }
    }

    fun countCardsWithStatus(lessonId: String, status: Status): Int {
        return try {
            database.query(
                QMDatabaseHelper.TABLE_CARDS,
                null,
                "${QMDatabaseHelper.TABLE_CARDS_PARENT_FK}=? AND ${QMDatabaseHelper.TABLE_CARDS_STATUS}=?",
                arrayOf(lessonId, status.value.toString()),
                null,
                null,
                null
            ).use { cursor ->
                getCardsFromCursor(cursor).size
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "countCardsWithStatus: $e")
            0
        }
    }

    fun resetLessonCardsStatus(lessonId: String): Int {
        val contentValues = ContentValues()

        contentValues.put(QMDatabaseHelper.TABLE_CARDS_STATUS, Status.NOT_LEARNED.value)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_CARDS,
                contentValues,
                "${QMDatabaseHelper.TABLE_CARDS_PARENT_FK}=?",
                arrayOf(lessonId)
            )
        } catch (e: SQLException) {
            Log.e("CardDAO", "resetLessonCardsStatus: $e")
        }
    }

    fun updateCardIsLearned(id: String, isLearned: Int): Int {
        val contentValues = ContentValues()

        contentValues.put(QMDatabaseHelper.TABLE_CARDS_IS_LEARNED, isLearned)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_CARDS,
                contentValues,
                "${QMDatabaseHelper.TABLE_CARDS_ID}=?",
                arrayOf(id)
            )
        } catch (e: SQLException) {
            Log.e("CardDAO", "updateCardIsLearned: $e")
        }
    }

    fun getIsLearnedCards(lessonId: String, isLearned: Boolean): ArrayList<Card> {
        val learned = if (isLearned) 1 else 0

        return try {
            database.query(
                QMDatabaseHelper.TABLE_CARDS,
                null,
                "${QMDatabaseHelper.TABLE_CARDS_PARENT_FK}=? AND ${QMDatabaseHelper.TABLE_CARDS_IS_LEARNED}=?",
                arrayOf(lessonId, learned.toString()),
                null,
                null,
                null
            ).use { cursor ->
                getCardsFromCursor(cursor)
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "getIsLearnedCards: $e")
            arrayListOf()
        }
    }

    fun checkCardExist(id: String): Boolean {
        return try {
            database.query(
                QMDatabaseHelper.TABLE_CARDS,
                null,
                "${QMDatabaseHelper.TABLE_CARDS_ID}=?",
                arrayOf(id),
                null,
                null,
                null
            ).use { cursor ->
                cursor.moveToFirst()
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "checkCardExist: $e")
            false
        }
    }

    fun updateCard(card: Card): Int {
        val contentValues = ContentValues()

        val isLearned = if (card.isLearned) 1 else 0

        contentValues.put(QMDatabaseHelper.TABLE_CARDS_SYMBOL, card.symbol)
        contentValues.put(QMDatabaseHelper.TABLE_CARDS_STATUS, card.status.value)
        contentValues.put(QMDatabaseHelper.TABLE_CARDS_IS_LEARNED, isLearned)
        contentValues.put(QMDatabaseHelper.TABLE_CARDS_VARIATIONS, card.variations.joinToString(","))
        contentValues.put(QMDatabaseHelper.TABLE_CARDS_PARENT_FK, card.lessonId)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_CARDS,
                contentValues,
                "${QMDatabaseHelper.TABLE_CARDS_ID}=?",
                arrayOf(card.id)
            )
        } catch (e: SQLException) {
            Log.e("CardDAO", "updateCard: $e")
            0
        }
    }

    fun resetCard(lessonId: String): Int {
        val contentValues = ContentValues()

        contentValues.put(QMDatabaseHelper.TABLE_CARDS_IS_LEARNED, 0)
        contentValues.put(QMDatabaseHelper.TABLE_CARDS_STATUS, Status.IDLE.value)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_CARDS,
                contentValues,
                "${QMDatabaseHelper.TABLE_CARDS_PARENT_FK}=?",
                arrayOf(lessonId)
            )
        } catch (e: SQLException) {
            Log.e("CardDAO", "resetCard: $e")
            0
        }
    }

    private fun getCardsFromCursor(cursor: Cursor): ArrayList<Card> {
        val cards = arrayListOf<Card>()

        if (database.isOpen) {
            if (cursor.moveToFirst()) {
                do {
                    val card = Card(
                        id = cursor.getString(0),
                        lessonId = cursor.getString(1),
                        symbol = cursor.getString(2),
                        status = Status.of(cursor.getInt(3)),
                        isLearned = cursor.getInt(4) == 1,
                        variations = cursor.getString(5).split(",")
                    )
                    cards.add(card)
                } while (cursor.moveToNext())
            }
        } else {
            Log.d("CardDAO", "Database is closed.")
        }
        return cards
    }

    fun close() {
        qmDatabaseHelper.close()
    }
}