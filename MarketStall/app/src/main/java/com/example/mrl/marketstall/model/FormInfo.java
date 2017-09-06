package com.example.mrl.marketstall.model;


public class FormInfo
{
    private String text;
    private int image;
    private int hint;

    public FormInfo(String text, int image, int hint)
    {
        this.text = text;
        this.image = image;
        this.hint = hint;
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

    public void setText(String text)
    {
        this.text = text;
    }
}
