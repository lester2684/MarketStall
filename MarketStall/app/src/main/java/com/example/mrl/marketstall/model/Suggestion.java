package com.example.mrl.marketstall.model;

public class Suggestion {

    private String name;
    private int count;

    public Suggestion()
    {

    }
    public Suggestion(String name)
    {
        this.name = name;
        this.count = 1;
    }

    public void addCount() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }
}
