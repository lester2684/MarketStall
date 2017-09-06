package com.example.mrl.marketstall.model;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.value.Values;

import java.util.ArrayList;
import java.util.List;

public class Coffee
{
    private final String TAG = getClass().getSimpleName();

    private int id;
    private String type;
    private String name;
    private String roaster;
    private String roastDate;
    private String imageName;
    private String location;
    private String farm;
    private String variety;
    private String elevation;
    private String process;


    private int TOTAL_FORM_SIZE;

    public Coffee(int id, String name, String roaster, String roastDate, String imageName, String location, String farm, String variety, String elevation, String process)
    {
        this.id = id;
        this.type = Values.COFFEE;

        this.name = name;
        this.roaster = roaster;
        this.roastDate = roastDate;
        this.imageName = imageName;
        this.location = location;
        this.farm = farm;
        this.variety = variety;
        this.elevation = elevation;
        this.process = process;
    }

    public Coffee()
    {
        this.type = Values.COFFEE;
        this.roastDate = "DD/MM/YYYY";
        this.name = "";
        this.roaster = "";
        this.roastDate = "";
        this.imageName = "";
        this.location = "";
        this.farm = "";
        this.variety = "";
        this.elevation = "";
        this.process = "";
    }

    public List<FormInfo> getDetails()
    {
        List<FormInfo> details = new ArrayList<>();
        details.add(0, new FormInfo(name, R.raw.icon_name, R.string.text_name));
        details.add(1, new FormInfo(roaster, R.raw.icon_roaster, R.string.text_roaster));
        details.add(2, new FormInfo(roastDate, R.drawable.button, R.string.text_roast_date));
        details.add(3, new FormInfo(location, R.drawable.button, R.string.text_location));
        details.add(4, new FormInfo(farm, R.raw.icon_farm, R.string.text_farm));
        details.add(5, new FormInfo(variety, R.raw.icon_variety, R.string.text_variety));
        details.add(6, new FormInfo(process, R.raw.icon_process, R.string.text_process));
        details.add(7, new FormInfo(elevation, R.drawable.button, R.string.text_elevation));
        TOTAL_FORM_SIZE = details.size();
        return details;
    }

    public void setByList(List<FormInfo> list)
    {
        this.name = list.get(0).getText();
        this.roaster = list.get(1).getText();
        this.roastDate = list.get(2).getText();
        this.location = list.get(3).getText();
        this.farm = list.get(4).getText();
        this.variety = list.get(5).getText();
        this.elevation = list.get(6).getText();
        this.process = list.get(7).getText();
    }


    public int getTOTAL_FORM_SIZE() {
        return TOTAL_FORM_SIZE;
    }

    public int getId() {
        return id;
    }

    public String getRoastDate()
    {
        return roastDate;
    }

    public String getRoaster() {
        return roaster;
    }

    public void setRoastDate(String roastDate) {
        this.roastDate = roastDate;
    }

    public void setRoaster(String roaster) {
        this.roaster = roaster;
    }

    public String getType()
    {
        return type;
    }

    public String getImageName()
    {
        return imageName;
    }

    public void setImageName(String imageName)
    {
        this.imageName = imageName;
    }

    public String getElevation() {
        return elevation;
    }

    public String getName() {
        return name;
    }

    public String getProcess() {
        return process;
    }

    public String getFarm() {
        return farm;
    }

    public String getLocation() {
        return location;
    }

    public String getVariety() {
        return variety;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }
}
