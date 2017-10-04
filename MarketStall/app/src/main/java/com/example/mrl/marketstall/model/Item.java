package com.example.mrl.marketstall.model;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.value.Values;

import java.util.ArrayList;
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
    private float latitude;
    private float longitude;
    private String imageName;

    public Item()
    {

    }

    public List<ItemInfo> detailsToForm() {
        List<ItemInfo> details = new ArrayList<>();
        details.add(0, new ItemInfo(name, R.raw.icon_name, R.string.text_name, true, false, Values.TEXT_FIElD, Values.TEXT_VIEW));
        details.add(1, new ItemInfo(dateCreated, R.drawable.ic_date, R.string.text_date, false, true, Values.TEXT_FIElD, Values.TEXT_VIEW));
        details.add(2, new ItemInfo(String.valueOf(price), R.raw.icon_price, R.string.text_price, true, true, Values.TEXT_FIElD, Values.TEXT_VIEW));
        details.add(3, new ItemInfo(String.valueOf(latitude) + "," + String.valueOf(longitude), R.drawable.ic_location, R.string.text_location, false, false, Values.TEXT_FIElD, Values.MAP_VIEW));
        details.add(4, new ItemInfo(String.valueOf(category), R.drawable.ic_category, R.string.text_category, true, true, Values.SPINNER, Values.TEXT_VIEW));
        details.add(5, new ItemInfo(String.valueOf(quality_rating), R.drawable.ic_quality, R.string.text_quality_rating, true, true, Values.RATING_BAR, Values.TEXT_VIEW));
        return details;
    }

    @Override
    public String toString() {

        return getName();
    }

    public void setByList(List<ItemInfo> list)
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getCategory() {
        return category;
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

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

}
