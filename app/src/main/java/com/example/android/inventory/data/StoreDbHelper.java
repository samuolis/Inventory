package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory.data.StoreContract.StoreEntry;

/**
 * Created by Lukas on 2017-07-19.
 */

public class StoreDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = StoreDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "store.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;


    public StoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + StoreEntry.TABLE_NAME + " ("
                + StoreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StoreEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + StoreEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0,"
                + StoreEntry.COLUMN_ITEM_COUNT + " INTEGER NOT NULL DEFAULT 0,"
                + StoreEntry.COLUMN_SUP_NAME + " TEXT NOT NULL,"
                + StoreEntry.COLUMN_SUP_EMAIL + " TEXT NOT NULL,"
                + StoreEntry.COLUMN_ITEM_IMAGE + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
