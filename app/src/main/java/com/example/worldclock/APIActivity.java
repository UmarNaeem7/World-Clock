package com.example.worldclock;

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
    String[] timeZones; // = {"Asia/Karachi"};
    RecyclerView mRecyclerView;
    LoadFromAPITask loadFromAPITask = new LoadFromAPITask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apiactivity);

        countTextView = findViewById(R.id.countTextView);
        mTextView = findViewById(R.id.loadingTextView);
        mRecyclerView = findViewById(R.id.apiRecyclerView);
        timeZones = getResources().getStringArray(R.array.timezones);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setMax(100);
        loadFromAPITask.execute(timeZones);
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
            MyAPIAdapter myAdapter = new MyAPIAdapter(getApplicationContext(), result);
            mRecyclerView.setAdapter(myAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }
}