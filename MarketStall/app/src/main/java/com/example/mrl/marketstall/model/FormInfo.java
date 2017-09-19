package com.example.mrl.marketstall.model;


public class FormInfo
{
    private String text;
    private int image;
    private int hint;
    private String inputType;
    private boolean show;

    public FormInfo()
    {

    }

    public FormInfo(String text, int image, int hint, boolean show, String inputType)
    {
        this.text = text;
        this.image = image;
        this.hint = hint;
        this.show = show;
        this.inputType = inputType;
    }

    public int getImage()
    {
        return image;
    }

    public String getText()
    {
        return text;
    }

    public int getHint()
    {
        return hint;
    }

    public boolean getShow()
    {
        return show;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getInputType() {
        return inputType;
    }
}
