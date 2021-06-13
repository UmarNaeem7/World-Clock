package com.example.worldclock;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String[] timeZones;
    RecyclerView mRecyclerView;
    FloatingActionButton addButton;
    boolean[] isChecked;
    TypedArray imagesArr;
    private SwipeRefreshLayout pullToRefresh;
    private static final String FILE_NAME = "example.txt";
    DBHelper dbHelper = new DBHelper(this);
    boolean selectionEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.actionbar_title);

        imagesArr = getResources().obtainTypedArray(R.array.images);    //get flag images in typed array

        timeZones = getResources().getStringArray(R.array.timezones);   //get timezones in string array
        mRecyclerView = findViewById(R.id.availableCitiesRecyclerView);

        isChecked = new boolean[timeZones.length];  //boolean array to keep track of checked/unchecked cities
        Arrays.fill(isChecked, false);
        isChecked = dbHelper.loadFromDB(isChecked);

        //refresh recycler view upon swipe down
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerView(false);
                pullToRefresh.setRefreshing(false);
            }
        });

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

        refreshRecyclerView(false);  //set adapter to recycler view

        updateUIEverySec();
    }

    private void updateUIEverySec(){
            Thread thread = new Thread() {
                @Override
                public void run() {
                        try {
                            while (!this.isInterrupted()) {
                                if (!selectionEnabled)
                                    Thread.sleep(10000);
                                else
                                    Thread.sleep(1000000);  //allow selection/deletion with peace
                                runOnUiThread(() -> {
                                    refreshRecyclerView(true);
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.i("TAG", e.getMessage());
                        }
                    }
            };
            thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.option_menu:
                selectionEnabled = true;
                refreshRecyclerView(selectionEnabled);
                break;
            case R.id.API_Activity:
                Intent intent = new Intent(this,APIActivity.class);
                startActivity(intent);
                break;
            case R.id.delete_option:
                MyAdapter myAdapter = new MyAdapter(this, timeZones, imagesArr, isChecked, true);
                List<Integer> temp;
                temp = myAdapter.deleteSelections();
                for (Integer i:temp){
                    isChecked[i] = false;
                }
                selectionEnabled = false;
                refreshRecyclerView(selectionEnabled);
                updateUIEverySec();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshRecyclerView(boolean enabledSelection){
        MyAdapter myAdapter = new MyAdapter(this, timeZones, imagesArr, isChecked, enabledSelection);
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
                isChecked = data.getBooleanArrayExtra("checked1");
                refreshRecyclerView(false);
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

    @Override
    public void onPause() {
        dbHelper.saveToDB(isChecked);
        super.onPause();
    }

}