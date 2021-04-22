package com.example.worldclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity2 extends AppCompatActivity{
    List<String> timeZones;
    RecyclerView mRecyclerView;
    boolean[] isChecked;
    MyAdapter2 myAdapter2;
    SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] temp = getResources().getStringArray(R.array.timezones);
        timeZones = new ArrayList<>(Arrays.asList(temp));   //load time zones in list to make filtering easier
        mRecyclerView = findViewById(R.id.recylerView);

        Bundle bundle = this.getIntent().getExtras();   //get boolean array that was passed by previous activity
        isChecked = bundle.getBooleanArray("checked");

        myAdapter2 = new MyAdapter2(this, timeZones, isChecked);
        mRecyclerView.setAdapter(myAdapter2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //search implementation
        mSearchView = findViewById(R.id.searchBar);
        mSearchView.setActivated(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("World Clock", "onQueryTextChange: ");
                myAdapter2.getFilter().filter(s);
                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putBooleanArray("checked1", isChecked);   //send boolean array back to previous activity
        intent.putExtras(b);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}