package com.example.worldclock;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity2 extends AppCompatActivity{
    List<String> timeZones;
    RecyclerView mRecyclerView;
    boolean[] isChecked;
    MyAdapter2 myAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        String[] temp = getResources().getStringArray(R.array.timezones);
        timeZones = new ArrayList<>(Arrays.asList(temp));
        mRecyclerView = findViewById(R.id.recylerView);

        Bundle bundle = this.getIntent().getExtras();
        isChecked = bundle.getBooleanArray("checked");

        myAdapter2 = new MyAdapter2(this, timeZones, isChecked);
        mRecyclerView.setAdapter(myAdapter2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"onBackPressed",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putBooleanArray("checked1", isChecked);
        intent.putExtras(b);
        //intent.putExtra("editTextValue", "value_here")
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }


}