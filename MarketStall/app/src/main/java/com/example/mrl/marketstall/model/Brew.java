package com.example.mrl.marketstall.model;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.value.Values;

import java.util.ArrayList;
import java.util.List;

public class Brew
{
    private final String TAG = getClass().getSimpleName();

    private int id;
    private String type;
    private String name;
    private int coffeeId;
    private int brewRecipeId;
    private String imageName;
    private int brewType;
    private String brewDate;
    private double price;

    private double TDS;
    private static double extractionYield;

    private int clean;
    private int sweet;
    private int acidity;
    private int body;
    private int flavour;
    private int finish;
    private int balance;

    public static final String CLEAN = "Clean";
    public static final String SWEET = "Sweet";
    public static final String ACIDITY = "Acidity";
    public static final String BODY = "Body";
    public static final String FLAVOUR = "Flavour";
    public static final String FINISH = "Finish";
    public static final String BALANCE = "Balance";

    private int TOTAL_FORM_SIZE;

    public Brew(int id, int coffeeId, int brewRecipeId, String name, String imageName, int brewType, String brewDate, double price, double TDS, double extractionYield, int clean, int sweet, int acidity, int body, int flavour, int finish, int balance)
    {
        this.id = id;
        this.name = name;
        this.type = Values.BREW;
        this.imageName = imageName;
        this.coffeeId = coffeeId;
        this.brewRecipeId = brewRecipeId;
        this.brewType = brewType;
        this.brewDate = brewDate;
        this.price = price;

        this.TDS = TDS;
        Brew.extractionYield = extractionYield;

        this.clean = clean;
        this.sweet = sweet;
        this.acidity = acidity;
        this.body = body;
        this.flavour = flavour;
        this.finish = finish;
        this.balance = balance;
    }

    public Brew()
    {
        this.type = Values.BREW;

        this.clean = 5;
        this.sweet = 5;
        this.acidity = 5;
        this.body = 5;
        this.flavour = 5;
        this.finish = 5;
        this.balance = 5;
    }

    public static double calculateExtractionYield(double brewWeight, double TDS, double coffeeDose)
    {
        return extractionYield = ((brewWeight * TDS)/coffeeDose);
    }

    public void setByIndex(int xIndex, int val)
    {
        switch(xIndex)
        {
            case 0:
                setClean(val);
                break;
            case 1:
                setSweet(val);
                break;
            case 2:
                setAcidity(val);
                break;
            case 3:
                setBody(val);
                break;
            case 4:
                setFlavour(val);
                break;
            case 5:
                setFinish(val);
                break;
            case 6:
                setBalance(val);
                break;
        }
    }

    public List<FormInfo> getDetails()
    {
        List<FormInfo> details = new ArrayList<>();
        details.add(0, new FormInfo(name, R.raw.icon_name, R.string.text_name));
        details.add(1, new FormInfo(String.valueOf(coffeeId), R.raw.icon_coffee_bean, 0));
        details.add(2, new FormInfo(String.valueOf(brewRecipeId), 0, 0));
        details.add(3, new FormInfo(String.valueOf(brewType), 0, 0));
        details.add(4, new FormInfo(brewDate, R.drawable.button, R.string.text_brew_date));
        details.add(5, new FormInfo(String.valueOf(price), R.raw.icon_price, R.string.text_price));
        details.add(6, new FormInfo(String.valueOf(TDS), R.drawable.button, R.string.text_TDS_percent));
        details.add(7, new FormInfo(String.valueOf(extractionYield), R.drawable.button, R.string.extraction_yield));
        TOTAL_FORM_SIZE = details.size();
        return details;
    }

    public void setByList(List<FormInfo> list)
    {
        this.name = list.get(0).getText();
        this.coffeeId = Integer.parseInt(list.get(1).getText());
        this.brewRecipeId = Integer.parseInt(list.get(2).getText());
        this.brewType = Integer.parseInt(list.get(3).getText());
        this.brewDate = list.get(4).getText();
        this.price = Double.parseDouble(list.get(5).getText());
        this.TDS = Double.parseDouble(list.get(6).getText());
        extractionYield = Double.parseDouble(list.get(7).getText());
    }

    public int getBrewRecipeId() {
        return brewRecipeId;
    }

    public void setBrewRecipeId(int brewRecipeId)
    {
        this.brewRecipeId = brewRecipeId;
    }

    public double getExtractionYield() {
        return extractionYield;
    }

    public double getTDS() {
        return TDS;
    }

    public void setTDS(double TDS) {
        this.TDS = TDS;
    }

    public void setExtractionYield(double extractionYield)
    {
        Brew.extractionYield = extractionYield;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName()
    {
        return imageName;
    }

    public void setImageName(String imageName)
    {
        this.imageName = imageName;
    }

    public int getCoffeeId() {
        return coffeeId;
    }

    public void setCoffeeId(int coffeeId) {
        this.coffeeId = coffeeId;
    }

    public int getBrewType() {
        return brewType;
    }

    public void setBrewType(int brewType) {
        this.brewType = brewType;
    }

    public String getBrewDate() {
        return brewDate;
    }

    public void setBrewDate(String brewDate) {
        this.brewDate = brewDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSweet() {
        return sweet;
    }

    public void setSweet(int sweet) {
        this.sweet = sweet;
    }

    public int getBody() {
        return body;
    }

    public void setBody(int body) {
        this.body = body;
    }

    public int getClean() {
        return clean;
    }

    public void setClean(int clean) {
        this.clean = clean;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public int getAcidity() {
        return acidity;
    }

    public void setAcidity(int acidity) {
        this.acidity = acidity;
    }

    public int getFlavour() {
        return flavour;
    }

    public void setFlavour(int flavour) {
        this.flavour = flavour;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

}
