package com.example.mrl.marketstall.model;

public class Forecast {
    private String day;
    private float count;
    private float average;

    public Forecast()
    {

    }

    public Forecast(String day, float count, float average) {
        this.day = day;
        this.count = count;
        this.average = average;
    }

    public float getAverage() {
        return average;
    }

    public float getCount() {
        return count;
    }

    public String getDay() {
        return day;
    }
}