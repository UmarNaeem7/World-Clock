package com.example.worldclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class APIActivity extends AppCompatActivity {
    TextView mTextView;
    TextView countTextView;
    ProgressBar mProgressBar;
    String[] timeZones;
    RecyclerView mRecyclerView;
    List<TimeZone> timeZoneList = new ArrayList<>();
    List<String> cities = new ArrayList<>();
    List<String> dateTimes = new ArrayList<>();
    LoadFromAPITask loadFromAPITask = new LoadFromAPITask();
    APIDBHelper apidbHelper = new APIDBHelper(this);

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Log.d("API", "onReceive: Boradcaster called");
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    int p = bundle.getInt("Download_progress");
                    onProgressUpdate(p);
                    if (p == 1 || p == 297) {
                        cities = (List<String>) bundle.getSerializable("Download_cities");
                        dateTimes = (List<String>) bundle.getSerializable("Download_times");
                        hideViews();
                        for (int i=0;i<cities.size();i++){
                            timeZoneList.add(new TimeZone(cities.get(i),dateTimes.get(i)));
                        }
                        MyAPIAdapter myAdapter = new MyAPIAdapter(getApplicationContext(), timeZoneList);
                        mRecyclerView.setAdapter(myAdapter);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                }
            }
        }
    };

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apiactivity);

        countTextView = findViewById(R.id.countTextView);
        mTextView = findViewById(R.id.loadingTextView);
        mRecyclerView = findViewById(R.id.apiRecyclerView);
        timeZones = getResources().getStringArray(R.array.timezones);
        mProgressBar = findViewById(R.id.progressBar);
        if (internetIsConnected())
            loadFromAPITask.execute(timeZones);
        else {
            Log.d("World", "onCreate: No internet");
            apidbHelper.loadFromDB(cities,dateTimes);
            List<TimeZone> result = new ArrayList<>();
            for (int i=0;i<cities.size();i++){
                result.add(new TimeZone(cities.get(i),dateTimes.get(i)));
            }
            hideViews();
            MyAPIAdapter myAdapter = new MyAPIAdapter(this, result);
            mRecyclerView.setAdapter(myAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        /*Intent intent = new Intent(getApplicationContext(), DownloadService.class);
        intent.putExtra("tzArr",timeZones);
        startService(intent);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter());
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private static String readAll(BufferedReader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } finally {
            is.close();
        }
    }

    public void hideViews(){
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
        countTextView.setVisibility(View.INVISIBLE);
    }

    void onProgressUpdate(int progress) {
        Log.d("World", "onProgressUpdate: " + progress);
        mProgressBar.setProgress(progress/3);
        countTextView.setText(progress + " of 297 loaded");
    }

    public class LoadFromAPITask extends AsyncTask<String, Integer, List<TimeZone>> {
        @Override
        protected List<TimeZone> doInBackground(String... objects) {
            int i = 0;
            List<TimeZone> timeZones = new ArrayList<>();
            for (String s:objects) {
                JSONObject json;
                String dt;
                if (!isCancelled()) {
                    try {
                        json = readJsonFromUrl("https://timezoneapi.io/api/timezone/?" + s + "&token=aRwJTcUQtzkSLQomTmOg");
                        dt = json.getJSONObject("data").getJSONObject("datetime").getString("date_time_txt");
                        if (!dt.equals("No value for data")) {
                            timeZones.add(new TimeZone(s, dt));
                            publishProgress(i++);
                            if (i==297)
                                return timeZones;
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return timeZones;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d("World", "onProgressUpdate: " + progress[0]);
            mProgressBar.setProgress(progress[0]/3);
            countTextView.setText(progress[0] + " of 297 loaded");
        }

        protected void onPostExecute(List<TimeZone> result) {
            hideViews();
            apidbHelper.deleteAll();
            for (int i=0;i<result.size();i++)
                apidbHelper.addtoDB(result.get(i).getTimezone(),result.get(i).getDatetime());
            MyAPIAdapter myAdapter = new MyAPIAdapter(getApplicationContext(), result);
            mRecyclerView.setAdapter(myAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }
}