package com.example.worldclock;

public class TimeZone {
    private String timezone;
    private String datetime;

    public TimeZone(String timezone, String datetime){
        this.timezone = timezone;
        this.datetime = datetime;
    }

    public String getTimezone(){
        return timezone;
    }

    public String getDatetime(){
        return datetime;
    }
}
