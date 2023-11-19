package com.daominh.quickmem.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.daominh.quickmem.data.QMDatabaseHelper;
import com.daominh.quickmem.data.model.Card;

import java.util.ArrayList;

public class CardDAO {
    QMDatabaseHelper qmDatabaseHelper;
    SQLiteDatabase sqLiteDatabase;

    public CardDAO(Context context) {
        qmDatabaseHelper = new QMDatabaseHelper(context);
    }

    //insert card
    public long insertCard(Card card) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();

        long result = 0;

        ContentValues contentValues = new ContentValues();

        //put
        contentValues.put("id", card.getId());
        contentValues.put("front", card.getFront());
        contentValues.put("back", card.getBack());
        contentValues.put("flashcard_id", card.getFlashcard_id());
        contentValues.put("created_at", card.getCreated_at());
        contentValues.put("updated_at", card.getUpdated_at());

        //insert
        try {
            result = sqLiteDatabase.insert(QMDatabaseHelper.TABLE_CARDS, null, contentValues);
        } catch (SQLException e) {
            Log.e("CardDAO", "insertCard: " + e);
        } finally {
            sqLiteDatabase.close();
        }
        return result;
    }

    //count card by flashcard_id
    public int countCardByFlashCardId(String flashcard_id) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();

        String query = "SELECT * FROM " + QMDatabaseHelper.TABLE_CARDS + " WHERE flashcard_id = '" + flashcard_id + "'";

        int count = 0;

        try {
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            count = cursor.getCount();
        } catch (SQLException e) {
            Log.e("CardDAO", "countCardByFlashCardId: " + e);
        } finally {
            sqLiteDatabase.close();
        }
        return count;
    }

    //get cards by flashcard_id
    public ArrayList<Card> getCardsByFlashCardId(String flashcard_id) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();

        ArrayList<Card> cards = new ArrayList<>();

        String query = "SELECT * FROM " + QMDatabaseHelper.TABLE_CARDS + " WHERE flashcard_id = '" + flashcard_id + "'";

        try {
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Card card = new Card();
                    card.setId(cursor.getString(0));
                    card.setFront(cursor.getString(1));
                    card.setBack(cursor.getString(2));
                    card.setFlashcard_id(cursor.getString(3));
                    card.setCreated_at(cursor.getString(4));
                    card.setUpdated_at(cursor.getString(5));

                    cards.add(card);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e("CardDAO", "getCardsByFlashCardId: " + e);
        } finally {
            sqLiteDatabase.close();
        }
        return cards;
    }

    //delete card by id
    public long deleteCardById(String id) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();

        long result = 0;

        try {
            result = sqLiteDatabase.delete(QMDatabaseHelper.TABLE_CARDS, "id = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e("CardDAO", "deleteCardById: " + e);
        } finally {
            sqLiteDatabase.close();
        }
        return result;
    }
}
