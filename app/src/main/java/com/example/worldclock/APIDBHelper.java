package com.example.worldclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.worldclock.CityContract.*;

import androidx.annotation.Nullable;

import java.util.List;

public class APIDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "APIReader.db";

    public APIDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(APIContract.APIEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(APIContract.APIEntry.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addtoDB(String name, String datetime){
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(APIContract.APIEntry.COLUMN_NAME_NAME, name);
        values.put(APIContract.APIEntry.COLUMN_NAME_DATETIME, datetime);

        Log.d("DB", "addtoDB: city" + name);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(APIContract.APIEntry.TABLE_NAME, null, values);
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + APIContract.APIEntry.TABLE_NAME);
    }

    public void loadFromDB(List<String> names, List<String> datetimes){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from " + APIContract.APIEntry.TABLE_NAME,null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(APIContract.APIEntry.COLUMN_NAME_NAME));
                names.add(name);
                String datetime = cursor.getString(cursor.getColumnIndex(APIContract.APIEntry.COLUMN_NAME_DATETIME));
                datetimes.add(datetime);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }
}
