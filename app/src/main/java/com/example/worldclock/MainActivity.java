package com.example.worldclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    String timeZones[];
    RecyclerView mRecyclerView;
    FloatingActionButton addButton;
    boolean isChecked[];
    TypedArray imagesArr;
    private static final String FILE_NAME = "example.txt";
    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        getSupportActionBar().setTitle(R.string.actionbar_title);

        imagesArr = getResources().obtainTypedArray(R.array.images);    //get flag images in typed array

        timeZones = getResources().getStringArray(R.array.timezones);   //get timezones in string array
        mRecyclerView = findViewById(R.id.availableCitiesRecyclerView);

        isChecked = new boolean[timeZones.length];  //boolean array to keep track of checked/unchecked cities
        for (int i=0;i<isChecked.length;i++)
        {
            isChecked[i] = false;
        }
        //isChecked[275] = true;

        //loadFromFile();
        loadFromDB();

        //save state upon onPause()
        if (savedInstanceState!=null){
            //reload instance by getting back boolean array from bundle
            isChecked = savedInstanceState.getBooleanArray("exitArr");
        }

        addButton = findViewById(R.id.addCityButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity2();    //load up next activity
            }
        });

        refreshRecyclerView();  //set adapter to recycler view

        //refresh recycler view upon swipe down
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerView();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public void refreshRecyclerView(){
        MyAdapter myAdapter = new MyAdapter(this, timeZones, imagesArr, isChecked);
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void openActivity2(){
        Intent intent = new Intent(this, MainActivity2.class);
        Bundle bundle = new Bundle();
        bundle.putBooleanArray("checked", isChecked);
        intent.putExtras(bundle);
        startActivityForResult(intent,1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                //Toast.makeText(this,"receiving back",Toast.LENGTH_SHORT).show();
                isChecked = data.getBooleanArrayExtra("checked1");
                refreshRecyclerView();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray("exitArr",isChecked);  //save boolean array by putting in bundle
    }

    public void saveToFile(){

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            for (boolean b:isChecked) {
                String s = Boolean.toString(b);
                s += '\n';
                fos.write(s.getBytes());
            }
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                    Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void loadFromFile(){
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;
            int i = 0;
            while ((text = br.readLine()) != null) {
                boolean b = Boolean.parseBoolean(text);
                Log.d("World Clock", "load: " + b);
                isChecked[i++] = b;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

    public void saveToDB(){
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

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

    public void loadFromDB(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i=0;i<isChecked.length;i++) {
            boolean isFound = existsInDB(i, db);

            if (isFound){
                isChecked[i] = true;
            }
        }

    }

    @Override
    public void onPause() {
        saveToDB();
        super.onPause();
    }

}