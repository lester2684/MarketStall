package com.example.mrl.marketstall.model;

import com.example.mrl.marketstall.R;

import java.util.ArrayList;
import java.util.List;

public class Item
{
    private final String TAG = getClass().getSimpleName();

    private String id;
    private String name;
    private double price;
    private String userId;
    private String dateCreated;
    private String location;

    private int TOTAL_FORM_SIZE;

    public Item()
    {

    }

    public List<FormInfo> getDetails()
    {
        List<FormInfo> details = new ArrayList<>();
        details.add(0, new FormInfo(name, R.raw.icon_name, R.string.text_name, true));
        details.add(1, new FormInfo(dateCreated, R.drawable.ic_date, R.string.text_date, false));
        details.add(2, new FormInfo(String.valueOf(price), R.raw.icon_price, R.string.text_price, true));
        details.add(3, new FormInfo(String.valueOf(location), R.drawable.ic_location, R.string.text_location, false));
        TOTAL_FORM_SIZE = details.size() -3;
        return details;
    }

    public void setByList(List<FormInfo> list)
    {
        this.name = list.get(0).getText();
        this.price = Double.parseDouble(list.get(1).getText());
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTOTAL_FORM_SIZE() {
        return TOTAL_FORM_SIZE;
    }

    public void setTOTAL_FORM_SIZE(int TOTAL_FORM_SIZE) {
        this.TOTAL_FORM_SIZE = TOTAL_FORM_SIZE;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTAG() {
        return TAG;
    }

    public String getUserId() {
        return userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
