package com.example.mrl.marketstall.model;


public class FormInfo
{
    private String text;
    private int image;
    private int hint;
    private boolean show;

    public FormInfo(String text, int image, int hint, boolean show)
    {
        this.text = text;
        this.image = image;
        this.hint = hint;
        this.show = show;
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

}
