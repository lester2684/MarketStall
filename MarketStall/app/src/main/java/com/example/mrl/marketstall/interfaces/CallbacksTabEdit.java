package com.example.mrl.marketstall.interfaces;

import com.example.mrl.marketstall.model.Item;

public interface CallbacksTabEdit
{
    void save();
    void delete();
    void setItem(Item item);
    Item getItem();
}
