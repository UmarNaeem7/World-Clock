package com.example.worldclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    String timeZones[];
    RecyclerView mRecyclerView;
    FloatingActionButton addButton;
    boolean isChecked[];
    TypedArray imagesArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("World clock", "working");

        imagesArr = getResources().obtainTypedArray(R.array.images);

        timeZones = getResources().getStringArray(R.array.timezones);
        mRecyclerView = findViewById(R.id.availableCitiesRecyclerView);

        isChecked = new boolean[timeZones.length];
        for (int i=0;i<isChecked.length;i++)
        {
            isChecked[i] = false;
        }
        isChecked[275] = true;

        if (savedInstanceState!=null){
            isChecked = savedInstanceState.getBooleanArray("exitArr");
        }

        addButton = findViewById(R.id.addCityButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity2();

            }
        });

        refreshRecyclerView();


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
                Toast.makeText(this,"receiving back",Toast.LENGTH_SHORT).show();
                isChecked = data.getBooleanArrayExtra("checked1");

                refreshRecyclerView();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray("exitArr",isChecked);
    }
}