package org.bibletranslationtools.sun.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.util.Log
import org.bibletranslationtools.sun.data.QMDatabaseHelper
import org.bibletranslationtools.sun.data.model.FlashCard

class FlashCardDAO(context: Context) {
    private val qmDatabaseHelper = QMDatabaseHelper(context)
    private val database = qmDatabaseHelper.writableDatabase

    fun insertFlashCard(flashcard: FlashCard): Long {
        val contentValues = ContentValues()

        //put
        contentValues.put("id", flashcard.id)
        contentValues.put("name", flashcard.name)
        contentValues.put("description", flashcard.description)

        return try {
            database.insert(QMDatabaseHelper.TABLE_LESSONS, null, contentValues)
        } catch (e: SQLException) {
            Log.e("FlashCardDAO", "insertFlashCard: $e")
            -1
        }
    }

    fun deleteFlashcardAndCards(flashcardId: String): Boolean {
        return try {
            database.beginTransaction()

            database.delete(
                QMDatabaseHelper.TABLE_CARDS,
                "flashcard_id = ?",
                arrayOf(flashcardId)
            )
            database.delete(QMDatabaseHelper.TABLE_LESSONS, "id = ?", arrayOf(flashcardId))

            database.setTransactionSuccessful()
            true
        } catch (e: SQLException) {
            Log.e("FlashCardDAO", "deleteFlashcardAndCards: $e")
            false
        }
    }

    fun getFlashCardById(id: String): FlashCard? {
        val query = "SELECT * FROM ${QMDatabaseHelper.TABLE_LESSONS} WHERE id='$id'"
        var flashCard: FlashCard? = null

        try {
            database.rawQuery(query, null).use { cursor ->
                with(cursor) {
                    if (moveToFirst()) {
                        flashCard = FlashCard(
                            getString(cursor.getColumnIndexOrThrow("id")),
                            getString(cursor.getColumnIndexOrThrow("name")),
                            getString(cursor.getColumnIndexOrThrow("description"))
                        )
                    }
                }
            }
        } catch (e: SQLException) {
            Log.e("FlashCardDAO", "getFlashCardById: $e")
        }

        return flashCard
    }

    fun getAllFlashCards(): ArrayList<FlashCard> {
        val flashCards = arrayListOf<FlashCard>()
        val query = "SELECT * FROM ${QMDatabaseHelper.TABLE_LESSONS}"

        try {
            database.rawQuery(query, null).use { cursor ->
                with(cursor) {
                    if (moveToFirst()) {
                        do {
                            val flashCard = FlashCard(
                                cursor.getString(cursor.getColumnIndexOrThrow("id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("description"))
                            )
                            flashCards.add(flashCard)
                        } while (moveToNext())
                    }
                }
            }
        } catch (e: SQLException) {
            Log.e("FlashCardDAO", "getAllFlashCardByUserId: $e")
        }
        return flashCards
    }

    fun updateFlashCard(flashcard: FlashCard): Int {
        val contentValues = ContentValues()

        contentValues.put("name", flashcard.name)
        contentValues.put("description", flashcard.description)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_LESSONS,
                contentValues,
                "id = ?",
                arrayOf(flashcard.id)
            )
        } catch (e: SQLException) {
            Log.e("FlashCardDAO", "updateFlashCard: $e")
        }
    }
}