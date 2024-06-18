package org.bibletranslationtools.sun.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class QMDatabaseHelper(context: Context?) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_LESSONS)
        db.execSQL(CREATE_TABLE_CARDS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //drop table if exists
        db.execSQL(COMMAND_DROP_TABLE + TABLE_LESSONS)
        db.execSQL(COMMAND_DROP_TABLE + TABLE_CARDS)

        //create table again
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    companion object {
        //create database name and version
        private const val DATABASE_NAME = "sun.db"
        private const val DATABASE_VERSION = 1

        //create table name
        const val TABLE_LESSONS: String = "lessons"
        const val TABLE_CARDS: String = "cards"

        //command
        const val COMMAND_CREATE_TABLE: String = "CREATE TABLE "
        const val COMMAND_DROP_TABLE: String = "DROP TABLE IF EXISTS "

        //create sql query

        const val CREATE_TABLE_LESSONS: String = COMMAND_CREATE_TABLE + TABLE_LESSONS + " (" +
                "id TEXT PRIMARY KEY , " +
                "name TEXT NOT NULL, " +
                "description TEXT);"

        const val CREATE_TABLE_CARDS: String = COMMAND_CREATE_TABLE + TABLE_CARDS + " (" +
                "id TEXT PRIMARY KEY , " +
                "front TEXT NOT NULL, " +
                "back TEXT NOT NULL, " +
                "flashcard_id TEXT NOT NULL, " +
                "status INTEGER NOT NULL," +  //0: not yet, 1 done, 2 studying
                "is_learned INTEGER NOT NULL, " +
                ");"

    }
}
