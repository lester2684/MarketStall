package com.example.mrl.marketstall.model;

public class BrewTypeInfo
{
    private int brewName;
    private int value;
    private int image;

    public BrewTypeInfo(int brewName, int value, int image)
    {
        this.brewName = brewName;
        this.value = value;
        this.image = image;
    }

    public int getBrewName() {
        return brewName;
    }

    public int getValue() {
        return value;
    }

    public int getImage() {
        return image;
    }
}
