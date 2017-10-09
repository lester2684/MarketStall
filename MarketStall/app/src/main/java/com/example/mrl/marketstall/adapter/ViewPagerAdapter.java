package com.example.mrl.marketstall.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{
    private final String TAG = getClass().getSimpleName();

    private Activity activity;
    private List<Fragment> fragments = new ArrayList<>();

    public ViewPagerAdapter(Activity activity, FragmentManager fragmentManager)
    {
        super(fragmentManager);
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position)
    {

        return fragments.get(position);

    }

    public void addFragment(final Fragment fragment)
    {
        fragments.add(fragment);

    }

    @Override
    public int getCount()
    {
        return fragments.size();

    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
//            case 0:
//                return activity.getString(R.string.tab_title_details);
//            case 1:
//                return activity.getString(R.string.tab_title_flavour);
            default:
                return null;
        }

    }
}