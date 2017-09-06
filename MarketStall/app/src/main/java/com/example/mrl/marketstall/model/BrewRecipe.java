package com.example.mrl.marketstall.model;

import com.example.mrl.marketstall.R;

import java.util.ArrayList;
import java.util.List;

public class BrewRecipe
{
    private final String TAG = getClass().getSimpleName();

    private int id;
    private int brewType;
    private double waterTemp;
    private double waterWeight;
    private double coffeeDose;
    private double ratio;
    private double bloomTime;
    private double brewYield;

    private int TOTAL_FORM_SIZE;

    public BrewRecipe(int id, int brewType, double waterTemp, double waterWeight, double coffeeDose, double ratio, double bloomTime, double brewYield)
    {
        this.id = id;
        this.brewType = brewType;
        this.waterTemp = waterTemp;
        this.waterWeight = waterWeight;
        this.coffeeDose = coffeeDose;
        this.ratio = ratio;
        this.bloomTime = bloomTime;
        this.brewYield = brewYield;
    }

    public BrewRecipe()
    {
        this.brewType = 0;
        this.waterTemp = 0;
        this.waterWeight = 0;
        this.coffeeDose = 0;
        this.ratio = 0;
        this.bloomTime = 0;
        this.brewYield = 0;
    }

    public List<FormInfo> getDetails()
    {
        List<FormInfo> details = new ArrayList<>();
        details.add(0, new FormInfo(String.valueOf(brewType), R.drawable.button, R.string.brew_type));
        details.add(1, new FormInfo(String.valueOf(waterTemp), R.drawable.button, R.string.water_temp));
        details.add(2, new FormInfo(String.valueOf(waterWeight), R.drawable.button, R.string.water_weight));
        details.add(3, new FormInfo(String.valueOf(coffeeDose), R.drawable.button, R.string.coffee_dose));
        details.add(4, new FormInfo(String.valueOf(ratio), R.drawable.button, R.string.ratio));
        details.add(5, new FormInfo(String.valueOf(bloomTime), R.drawable.button, R.string.bloom_time));
        details.add(6, new FormInfo(String.valueOf(brewYield), R.drawable.button, R.string.brew_yield));
        TOTAL_FORM_SIZE = details.size();
        return details;
    }

    private void checkValue(List<FormInfo> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            FormInfo formInfo = list.get(i);
            if (formInfo.getText().trim().isEmpty())
            {
                formInfo.setText("0");
            }
        }
    }

    public void setByList(List<FormInfo> list)
    {
        checkValue(list);
        this.brewType = Integer.parseInt(list.get(0).getText());
        this.waterTemp = Double.parseDouble(list.get(1).getText());
        this.waterWeight = Double.parseDouble(list.get(2).getText());
        this.coffeeDose = Double.parseDouble(list.get(3).getText());
        this.ratio = Double.parseDouble(list.get(4).getText());
        this.bloomTime = Double.parseDouble(list.get(5).getText());
        this.brewYield = Double.parseDouble(list.get(6).getText());
    }

    public int getTOTAL_FORM_SIZE() {
        return TOTAL_FORM_SIZE;
    }

    public double getBloomTime() {
        return bloomTime;
    }

    public double getBrewYield() {
        return brewYield;
    }

    public double getCoffeeDose() {
        return coffeeDose;
    }

    public double getRatio() {
        return ratio;
    }

    public double getWaterTemp() {
        return waterTemp;
    }

    public double getWaterWeight() {
        return waterWeight;
    }

    public int getId() {
        return id;
    }

    public int getBrewType() {
        return brewType;
    }

    public String getTAG() {
        return TAG;
    }

    public void setBloomTime(double bloomTime) {
        this.bloomTime = bloomTime;
    }

    public void setBrewType(int brewType) {
        this.brewType = brewType;
    }

    public void setBrewYield(double brewYield) {
        this.brewYield = brewYield;
    }

    public void setCoffeeDose(double coffeeDose) {
        this.coffeeDose = coffeeDose;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public void setWaterTemp(double waterTemp) {
        this.waterTemp = waterTemp;
    }

    public void setWaterWeight(double waterWeight) {
        this.waterWeight = waterWeight;
    }
}

