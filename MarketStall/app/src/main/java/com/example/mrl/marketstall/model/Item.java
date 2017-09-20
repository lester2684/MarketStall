package com.example.mrl.marketstall.model;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.value.Values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Item
{
    private String id;
    private String name;
    private String category;
    private double price;
    private float quality_rating;
    private String userId;
    private String dateCreated;
    private String location;
    private String imageName;

    private int total_form_size;

    public Item()
    {

    }

    public List<FormInfo> detailsToForm()
    {
        List<FormInfo> details = new ArrayList<>();
        details.add(0, new FormInfo(name, R.raw.icon_name, R.string.text_name, true, Values.TEXT_FIElD));
        details.add(1, new FormInfo(dateCreated, R.drawable.ic_date, R.string.text_date, false, Values.TEXT_FIElD));
        details.add(2, new FormInfo(String.valueOf(price), R.raw.icon_price, R.string.text_price, true, Values.TEXT_FIElD));
        details.add(3, new FormInfo(String.valueOf(location), R.drawable.ic_location, R.string.text_location, false, Values.TEXT_FIElD));
        details.add(4, new FormInfo(String.valueOf(category), R.drawable.ic_location, R.string.text_category, true, Values.SPINNER));
        details.add(5, new FormInfo(String.valueOf(quality_rating), R.drawable.ic_location, R.string.text_quality_rating, true, Values.RATING_BAR));

        int count = 0;
        for (FormInfo detail :details) {
            if(detail.getShow())
                count++;
        }
        total_form_size = count;
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

    public int getTotal_form_size() {
        return total_form_size;
    }

    public void setTotal_form_size(int TOTAL_FORM_SIZE) {
        this.total_form_size = TOTAL_FORM_SIZE;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getCategory() {
        return category;
    }

    public int getIndexOfCategory() {
        return Arrays.asList(Values.categories).indexOf(category);
    }

    public float getQuality_rating() {
        return quality_rating;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuality_rating(float quality_rating) {
        this.quality_rating = quality_rating;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setCategoryByIndex(int categoryByIndex)
    {
        this.category = Values.categories[categoryByIndex];
    }
}
