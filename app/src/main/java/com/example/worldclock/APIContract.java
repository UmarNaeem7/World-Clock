package com.example.worldclock;

import android.provider.BaseColumns;

public final class APIContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private APIContract() {}

    /* Inner class that defines the table contents */
    public static class APIEntry implements BaseColumns {
        public static final String TABLE_NAME = "loadedCity";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DATETIME = "datetime";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + APIEntry.TABLE_NAME + " (" +
                        APIEntry._ID + " INTEGER PRIMARY KEY," +
                        APIEntry.COLUMN_NAME_NAME + " TEXT," +
                        APIEntry.COLUMN_NAME_DATETIME + " TEXT)";


        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + APIEntry.TABLE_NAME;

    }
}

