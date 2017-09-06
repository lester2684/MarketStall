package com.example.mrl.marketstall.interfaces;

import com.example.mrl.marketstall.model.Brew;

public interface CallbacksTabEdit
{
    void save();
    void delete();
    void setBrew(Brew brew);
    Brew getBrew();
}
