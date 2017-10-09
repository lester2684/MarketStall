package com.example.mrl.marketstall.model;


public class ItemInfo
{
    private String text;
    private int image;
    private int hint;
    private String inputTypeForm;
    private String detailViewType;
    private boolean showForm;
    private boolean showDetail;

    public ItemInfo()
    {

    }

    public ItemInfo(String text, int image, int hint, boolean showForm, boolean showDetail, String inputTypeForm, String detailViewType)
    {
        this.text = text;
        this.image = image;
        this.hint = hint;
        this.showForm = showForm;
        this.showDetail = showDetail;
        this.inputTypeForm = inputTypeForm;
        this.detailViewType = detailViewType;
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

    public boolean getShowForm()
    {
        return showForm;
    }

    public boolean getShowDetail()
    {
        return showDetail;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getInputTypeForm() {
        return inputTypeForm;
    }

    public String getDetailViewType() {
        return detailViewType;
    }
}
