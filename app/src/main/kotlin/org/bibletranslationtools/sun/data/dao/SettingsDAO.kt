package org.bibletranslationtools.sun.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.util.Log
import org.bibletranslationtools.sun.data.QMDatabaseHelper
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Status
import kotlin.reflect.jvm.internal.impl.resolve.constants.EnumValue

class SettingsDAO(context: Context) {

    private val qmDatabaseHelper = QMDatabaseHelper(context)
    private val database = qmDatabaseHelper.writableDatabase

    fun insert(name: String, value: String): Long {
        val contentValues = ContentValues()

        contentValues.put(QMDatabaseHelper.TABLE_SETTINGS_NAME, name)
        contentValues.put(QMDatabaseHelper.TABLE_SETTINGS_VALUE, value)

        return try {
            database.insert(
                QMDatabaseHelper.TABLE_SETTINGS,
                null,
                contentValues
            )
        } catch (e: SQLException) {
            Log.e("SettingsDAO", "insert: $e")
            -1
        }
    }

    fun get(name: String): String? {
        return try {
            database.query(
                QMDatabaseHelper.TABLE_SETTINGS,
                null,
                "${QMDatabaseHelper.TABLE_SETTINGS_NAME}=?",
                arrayOf(name),
                null,
                null,
                null
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(QMDatabaseHelper.TABLE_SETTINGS_VALUE))
                } else {
                    null
                }
            }
        } catch (e: SQLException) {
            Log.e("SettingsDAO", "get: $e")
            null
        }
    }

    fun update(name: String, value: String): Int {
        val contentValues = ContentValues()

        contentValues.put(QMDatabaseHelper.TABLE_SETTINGS_VALUE, value)

        return try {
            database.update(
                QMDatabaseHelper.TABLE_SETTINGS,
                contentValues,
                "${QMDatabaseHelper.TABLE_SETTINGS_NAME}=?",
                arrayOf(name)
            )
        } catch (e: SQLException) {
            Log.e("SettingsDAO", "update: $e")
            0
        }
    }

    fun close() {
        qmDatabaseHelper.close()
    }
}