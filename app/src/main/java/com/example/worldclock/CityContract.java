package com.example.worldclock;

import android.provider.BaseColumns;

public final class CityContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private CityContract() {}

    /* Inner class that defines the table contents */
    public static class CityEntry implements BaseColumns {
        public static final String TABLE_NAME = "checkedCity";
        public static final String COLUMN_NAME_INDEX = "cityIndex";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + CityEntry.TABLE_NAME + " (" +
                        CityEntry._ID + " INTEGER PRIMARY KEY," +
                        CityEntry.COLUMN_NAME_INDEX + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + CityEntry.TABLE_NAME;

    }
}

