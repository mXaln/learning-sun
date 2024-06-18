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

    fun close() {
        qmDatabaseHelper.close()
    }

    fun insertCard(card: Card): Long {
        val contentValues = ContentValues()

        contentValues.put("id", card.id)
        contentValues.put("front", card.front)
        contentValues.put("back", card.back)
        contentValues.put("status", card.status.value)
        contentValues.put("is_learned", card.isLearned)
        contentValues.put("flashcard_id", card.flashcardId)

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

    fun countCardByFlashCardId(flashcardId: String): Int {
        val query = "SELECT * FROM ${QMDatabaseHelper.TABLE_CARDS} WHERE flashcard_id='$flashcardId'"

        return try {
            database.rawQuery(query, null).use { cursor ->
                cursor.count
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "countCardByFlashCardId: $e")
        }
    }

    fun getCardsByFlashCardId(flashcardId: String): ArrayList<Card> {
        val query =
            "SELECT * FROM ${QMDatabaseHelper.TABLE_CARDS} WHERE flashcard_id='$flashcardId'"

        return try {
            database.rawQuery(query, null).use { cursor ->
                getCardsFromCursor(cursor)
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "getCardsByFlashCardId: $e")
            arrayListOf()
        }
    }

    fun getAllCardByStatus(flashcardId: String): ArrayList<Card> {
        val query =
            "SELECT * FROM ${QMDatabaseHelper.TABLE_CARDS} WHERE flashcard_id='$flashcardId' AND status != 1"

        return try {
            database.rawQuery(query, null).use { cursor ->
                getCardsFromCursor(cursor)
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "getAllCardByStatus: $e")
            arrayListOf()
        }
    }

    //delete card by id
    fun deleteCardById(id: String): Int {
        return try {
            database.delete(QMDatabaseHelper.TABLE_CARDS, "id = ?", arrayOf(id))
        } catch (e: SQLException) {
            Log.e("CardDAO", "deleteCardById: $e")
            0
        }
    }

    //update card status by id
    fun updateCardStatusById(id: String, status: Status): Int {
        val contentValues = ContentValues()

        contentValues.put("status", status.value)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_CARDS,
                contentValues,
                "id = ?",
                arrayOf(id)
            )
        } catch (e: SQLException) {
            Log.e("CardDAO", "updateCardStatusById: $e")
            0
        }
    }

    fun countCardsWithStatus(flashcardId: String, status: Int): Int {
        val query = "SELECT * FROM ${QMDatabaseHelper.TABLE_CARDS} WHERE flashcard_id='$flashcardId' AND status=$status"

        return try {
            database.rawQuery(query, null).use { cursor ->
                getCardsFromCursor(cursor).size
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "getCardByStatus: $e")
            0
        }
    }

    fun resetStatusCardByFlashCardId(flashcardId: String): Int {
        val contentValues = ContentValues()

        contentValues.put("status", 2)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_CARDS,
                contentValues,
                "flashcard_id = ?",
                arrayOf(flashcardId)
            )
        } catch (e: SQLException) {
            Log.e("CardDAO", "resetStatusCardByFlashCardId: $e")
        }
    }

    fun updateIsLearnedCardById(id: String, isLearned: Int): Int {
        val contentValues = ContentValues()

        contentValues.put("is_learned", isLearned)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_CARDS,
                contentValues,
                "id = ?",
                arrayOf(id)
            )
        } catch (e: SQLException) {
            Log.e("CardDAO", "updateIsLearnedCardById: $e")
        }
    }

    fun getCardByIsLearned(flashcardId: String, isLearned: Int): ArrayList<Card> {
        val query = "SELECT * FROM ${QMDatabaseHelper.TABLE_CARDS} WHERE flashcard_id='$flashcardId' AND is_learned=$isLearned"

        return try {
            database.rawQuery(query, null).use { cursor ->
                getCardsFromCursor(cursor)
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "getCardByIsLearned: $e")
            arrayListOf()
        }
    }

    fun checkCardExist(cardId: String): Boolean {
        val query = "SELECT * FROM ${QMDatabaseHelper.TABLE_CARDS} WHERE id='$cardId'"

        return try {
            database.rawQuery(query, null).use { cursor ->
                cursor.moveToFirst()
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "checkCardExist: $e")
            false
        }
    }

    fun updateCardById(card: Card): Int {
        val contentValues = ContentValues()

        contentValues.put("front", card.front)
        contentValues.put("back", card.back)
        contentValues.put("flashcard_id", card.flashcardId)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_CARDS,
                contentValues,
                "id = ?",
                arrayOf(card.id)
            )
        } catch (e: SQLException) {
            Log.e("CardDAO", "updateCardById: $e")
            0
        }
    }


    fun getAllCardByFlashCardId(flashcardId: String): ArrayList<Card> {
        val query = "SELECT * FROM ${QMDatabaseHelper.TABLE_CARDS} WHERE flashcard_id='$flashcardId'"

        return try {
            database.rawQuery(query, null).use { cursor ->
                getCardsFromCursor(cursor)
            }
        } catch (e: SQLException) {
            Log.e("CardDAO", "getAllCardByFlashCardId: $e")
            arrayListOf()
        }
    }

    fun resetIsLearnedAndStatusCardByFlashCardId(flashcardId: String): Int {
        val contentValues = ContentValues()

        contentValues.put("is_learned", 0)
        contentValues.put("status", 0)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_CARDS,
                contentValues,
                "flashcard_id = ?",
                arrayOf(flashcardId)
            )
        } catch (e: SQLException) {
            Log.e("CardDAO", "resetIsLearnedAndStatusCardByFlashCardId: $e")
            0
        }
    }

    private fun getCardsFromCursor(cursor: Cursor): ArrayList<Card> {
        val cards = arrayListOf<Card>()

        if (database.isOpen) { // Check if the database is open
            if (cursor.moveToFirst()) {
                do {
                    val card = Card(
                        id = cursor.getString(0),
                        front = cursor.getString(1),
                        back = cursor.getString(2),
                        flashcardId = cursor.getString(3),
                        status = Status.of(cursor.getInt(4)),
                        isLearned = cursor.getInt(5) == 1
                    )
                    cards.add(card)
                } while (cursor.moveToNext())
            }
        } else {
            Log.d("CardDAO", "Database is closed.")
        }
        return cards
    }
}