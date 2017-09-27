package com.example.mrl.marketstall.view.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.adapter.ViewPagerAdapter;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.ui.ZoomOutPageTransformer;
import com.example.mrl.marketstall.utils.Utils;
import com.example.mrl.marketstall.value.Values;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import static com.example.mrl.marketstall.ui.Animations.toolbarAnimation;

public class FragmentTabHost extends Fragment implements Callbacks
{
    private final String TAG = getClass().getSimpleName();
    private View view;
    private Toolbar toolbar;
    private FloatingActionMenu fabMenu;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Bundle bundle;
    private boolean showMenu = false;
    private String tabType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.fragment_tab_host, container, false);

        setupValues();
        setupToolbar();
        setupTabHost();
        setupViewPager();

        return view;
    }

    private void setupValues()
    {
        toolbar = getActivity().findViewById(R.id.toolbar);
        fabMenu = getActivity().findViewById(R.id.fab_menu);
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        bundle = getArguments();

        tabType = bundle.getString(Values.TAB_TYPE);

        assert tabType != null;
        switch (tabType)
        {
            case Values.TAB_RECYCLERS:
                break;
        }
    }

    private void setupToolbar()
    {
        AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar_layout);
        appBarLayout.setExpanded(true);
        toolbar.getMenu().clear();
    }

    private void setupTabHost()
    {
        switch (tabType)
        {
            case Values.TAB_RECYCLERS:
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_items)));
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_map)));
                break;
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                switch (tabType)
                {
                    case Values.TAB_RECYCLERS:
                        viewPager.setCurrentItem(tab.getPosition());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
    }

    private void setupViewPager()
    {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), getChildFragmentManager());
        List<Fragment> fragmentList = new ArrayList<>();
        switch (tabType)
        {
            case Values.TAB_RECYCLERS:
                fragmentList.add(new FragmentRecycler());
                fragmentList.add(new FragmentMap());

                Bundle bundleItem = new Bundle();
                Bundle bundleMap = new Bundle();
                bundleItem.putString(Values.RECYCLER_TYPE, Values.ITEM);

                List<Bundle> bundleList = new ArrayList<>();
                bundleList.add(bundleItem);
                bundleList.add(bundleMap);

                for(int i = 0; i < fragmentList.size(); i++)
                {
                    fragmentList.get(i).setArguments(bundleList.get(i));
                    viewPagerAdapter.addFragment(fragmentList.get(i));
                }
                break;
        }

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                switch (tabType)
                {
                    case Values.TAB_RECYCLERS:
                        tabLayout.getTabAt(position).select();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

    }

    @Override
    public void onReturn(Fragment fromFragment, String fromTabType)
    {
        Log.i(TAG, "onReturn: " + fromFragment.getClass().getSimpleName() + " " + fromTabType + " "+ tabType);
        if (fromFragment instanceof FragmentForm)
        {
            toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, null, R.raw.photo_coffee_cherries);
        }
        else if (fromFragment instanceof FragmentForm)
        {
            toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, null, R.raw.photo_coffee_cherries);
        }
        else if (fromFragment instanceof FragmentDetails)
        {
            FragmentDetails fragmentDetails = (FragmentDetails) fromFragment;
            switch (fragmentDetails.getDetailsType())
            {
                case Values.ITEM:
                    toolbarAnimation(getActivity(), R.anim.slide_right_out, R.anim.slide_left_in, null, R.raw.photo_coffee_cherries);
                    break;
            }
        }
        else if (fromFragment instanceof FragmentTabHost && fromTabType.equals(Values.TAB_ITEM_DETAILS))
        {
            toolbarAnimation(getActivity(), R.anim.slide_right_out, R.anim.slide_left_in, null, R.raw.photo_coffee_cherries);
        }
        else if (fromFragment instanceof FragmentTabHost && fromTabType.equals(Values.TAB_ITEM_EDIT) && !tabType.equals(Values.TAB_ITEM_DETAILS))
        {
            toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, null, R.raw.photo_pour_over);
        }
        else if (fromFragment instanceof FragmentTabHost && fromTabType.equals(Values.TAB_ITEM_NEW))
        {
            toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, null, R.raw.photo_coffee_cherries);
        }
    }

    @Override
    public void onResume()
    {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Log.i(TAG, "onPause: ");
        super.onPause();
        Utils.closeKeyboard(getActivity());
        if (fabMenu.isOpened())
        {
            fabMenu.close(true);
        }
    }

    @Override
    public void onBackPressedCallback()
    {
        Log.i(TAG, "backButtonWasPressed: ");

    }

    @Override
    public void backPress()
    {
        getActivity().onBackPressed();

    }

    @Override
    public String getTabType()
    {
        return tabType;
    }

    @Override
    public void toolbarExpanded()
    {
        fabMenu.showMenuButton(true);
        if (showMenu)
        {
            showMenu = false;
        }
    }

    @Override
    public void toolbarCollapsed()
    {
        fabMenu.hideMenuButton(true);
        if (!showMenu)
        {
            showMenu = true;
        }
    }

    @Override
    public String getTAG()
    {

        return TAG;
    }
}