package com.example.worldclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.worldclock.CityContract.*;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CityEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(CityEntry.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean existsInDB(int index, SQLiteDatabase db){
        Cursor cursor = null;
        String sql ="SELECT * FROM "+ CityContract.CityEntry.TABLE_NAME +" WHERE cityIndex="+index;
        cursor= db.rawQuery(sql,null);

        //Log.d("WorldClock", "existsInDB: " + cursor.getCount());
        if(cursor.getCount()>0){
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

    public void addtoDB(int index, SQLiteDatabase db){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CityContract.CityEntry.COLUMN_NAME_INDEX, index);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(CityContract.CityEntry.TABLE_NAME, null, values);
    }

    public void deleteFromDB(int index, SQLiteDatabase db){
        // Define 'where' part of query.
        String selection = CityContract.CityEntry.COLUMN_NAME_INDEX + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(index) };
        // Issue SQL statement.
        int deletedRows = db.delete(CityContract.CityEntry.TABLE_NAME, selection, selectionArgs);
        Log.d("World Clock", "deleteFromDB: " + deletedRows);
    }

    public void saveToDB(boolean[] isChecked){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i=0;i<isChecked.length;i++) {
            boolean isFound = existsInDB(i, db);


            if (isChecked[i]){
                if (!isFound){  //if city is checked but it doesn't exist in DB, then add it
                    addtoDB(i, db);
                }
            }
            else{
                if (isFound){   //if city is unchecked and it also exists in DB, then delete it
                    deleteFromDB(i, db);
                }
            }
        }
        db.close();
    }

    public boolean[] loadFromDB(boolean[] isChecked){
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i=0;i<isChecked.length;i++) {
            boolean isFound = existsInDB(i, db);

            if (isFound){
                isChecked[i] = true;
            }
        }
        return isChecked;
    }
}
