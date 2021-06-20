package com.example.worldclock;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DownloadService extends IntentService {
    List<String> cities = new ArrayList<>();
    List<String> dateTimes = new ArrayList<>();


    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int i = 0;
        String[] timeZonesArr = intent.getStringArrayExtra("tzArr");
        for (String s:timeZonesArr) {
            JSONObject json;
            String dt;
            try {
                json = readJsonFromUrl("https://timezoneapi.io/api/timezone/?" + s + "&token=aRwJTcUQtzkSLQomTmOg");
                dt = json.getJSONObject("data").getJSONObject("datetime").getString("date_time_txt");
                if (!dt.equals("No value for data")) {
                    cities.add(s);
                    dateTimes.add(dt);
                    Log.d("Service", "onHandleIntent: " + dt);
                    //publishProgress(i++);
                    i++;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        publishProgress(i);
    }

    private void publishProgress(int i){
        Intent intent = new Intent(this,DownloadService.class);
        intent.putExtra("Download_progress",i);
        intent.putExtra("Download_cities", (Serializable) cities);
        intent.putExtra("Download_times", (Serializable) dateTimes);
        sendBroadcast(intent);
        Log.d("World", "publishProgress: in");
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
}