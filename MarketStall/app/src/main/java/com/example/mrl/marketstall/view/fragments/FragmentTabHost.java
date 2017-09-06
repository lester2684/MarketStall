package com.example.mrl.marketstall.view.fragments;

import android.net.Uri;
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
import com.example.mrl.marketstall.interfaces.CallbacksTabEdit;
import com.example.mrl.marketstall.model.Brew;
import com.example.mrl.marketstall.ui.ZoomOutPageTransformer;
import com.example.mrl.marketstall.utils.ImageUtils;
import com.example.mrl.marketstall.utils.Utils;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.view.fragments.tab_fragments.FragmentTabBrewDetails;
import com.example.mrl.marketstall.view.fragments.tab_fragments.FragmentTabRecycler;
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
//    private DataSourceBrew dataSourceBrew;
    private Bundle bundle;
    private Brew brew;
    private boolean showMenu = false;
    private boolean brewUpdated = false;
    private boolean pager = false;
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
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        fabMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fab_menu);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

//        dataSourceBrew = new DataSourceBrew(view.getContext());
//        dataSourceBrew = new DataSourceBrew(view.getContext());
//        dataSourceBrew.open();

        bundle = getArguments();

        tabType = bundle.getString(Values.TAB_TYPE);

        assert tabType != null;
        switch (tabType)
        {
            case Values.TAB_RECYCLERS:
                break;
            case Values.TAB_BREW_EDIT:

            case Values.TAB_BREW_DETAILS:
//                brew = dataSourceBrew.getById(bundle.getInt(Values.SELECTED_BREW));
                break;
            case Values.TAB_BREW_NEW:
                brew = new Brew();
                brew.setCoffeeId(bundle.getInt(Values.SELECTED_COFFEE));
                break;
        }
    }

    private void setupToolbar()
    {
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar_layout);
        appBarLayout.setExpanded(true);
        toolbar.getMenu().clear();
    }

    private void setupTabHost()
    {
        switch (tabType)
        {
            case Values.TAB_RECYCLERS:
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_coffees)));
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_brews)));
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_brew_recipes)));
                break;
            case Values.TAB_BREW_EDIT:
            case Values.TAB_BREW_NEW:
            case Values.TAB_BREW_DETAILS:
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_details)));
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title_flavour)));
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
                    case Values.TAB_BREW_EDIT:
                    case Values.TAB_BREW_NEW:
                        if (!brewUpdated)
                        {
                            pager = false;
                            updateTabs();
                            brewUpdated = true;
                        }
                        else
                        {
                            brewUpdated = false;
                        }
                        viewPager.setCurrentItem(tab.getPosition());
                        if (fabMenu.isOpened())
                        {
                            fabMenu.close(true);
                        }
                        break;
                    case Values.TAB_RECYCLERS:
                    case Values.TAB_BREW_DETAILS:
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
                fragmentList.add(new FragmentTabRecycler());
                fragmentList.add(new FragmentTabRecycler());
                fragmentList.add(new FragmentTabRecycler());

                Bundle bundleCoffee = new Bundle();
                Bundle bundleBrew = new Bundle();
                Bundle bundleBrewRecipes = new Bundle();
                bundleCoffee.putString(Values.RECYCLER_TYPE, Values.COFFEE);
                bundleBrew.putString(Values.RECYCLER_TYPE, Values.BREW);
                bundleBrewRecipes.putString(Values.RECYCLER_TYPE, Values.BREW_RECIPE);

                List<Bundle> bundleList = new ArrayList<>();
                bundleList.add(bundleCoffee);
                bundleList.add(bundleBrew);
                bundleList.add(bundleBrewRecipes);

                for(int i = 0; i < fragmentList.size(); i++)
                {
                    fragmentList.get(i).setArguments(bundleList.get(i));
                    viewPagerAdapter.addFragment(fragmentList.get(i));
                }
                break;
            case Values.TAB_BREW_EDIT:
            case Values.TAB_BREW_NEW:
                bundle.putString(Values.FORM_TYPE, Values.BREW);
                fragmentList.add(0, new FragmentForm());
                for(int i = 0; i < fragmentList.size(); i++)
                {
                    fragmentList.get(i).setArguments(bundle);
                    viewPagerAdapter.addFragment(fragmentList.get(i));
                }
                break;
            case Values.TAB_BREW_DETAILS:
                fragmentList.add(0, new FragmentTabBrewDetails());
                for(int i = 0; i < fragmentList.size(); i++)
                {
                    fragmentList.get(i).setArguments(bundle);
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
                    case Values.TAB_BREW_EDIT:
                    case Values.TAB_BREW_NEW:
                        if (!brewUpdated)
                        {
                            pager = true;
                            updateTabs();
                            brewUpdated = true;
                        }
                        else
                        {
                            brewUpdated = false;
                        }
                        tabLayout.getTabAt(position).select();
                        if (fabMenu.isOpened())
                        {
                            fabMenu.close(true);
                        }
                        break;
                    case Values.TAB_RECYCLERS:
                    case Values.TAB_BREW_DETAILS:
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

    private void updateTabs()
    {
        Log.i(TAG, "updateTabs: ");
        ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();
        CallbacksTabEdit callbacksTabEdit;
        if (pager)
        {
            callbacksTabEdit = (CallbacksTabEdit) viewPagerAdapter.getItem(tabLayout.getSelectedTabPosition());
        }
        else
        {
            callbacksTabEdit = (CallbacksTabEdit) viewPagerAdapter.getItem(viewPager.getCurrentItem());
        }
        Brew tabBrew = callbacksTabEdit.getBrew();
        Log.i(TAG, "updateTabs: update brew from " + callbacksTabEdit.getClass().getSimpleName());
        if(callbacksTabEdit instanceof FragmentForm)
        {
            FragmentForm fragmentForm = (FragmentForm) callbacksTabEdit;
            if (fragmentForm.getFormType().equals(Values.BREW)) {
                brew.setName(tabBrew.getName());
                brew.setCoffeeId(tabBrew.getCoffeeId());
            }
        }
        for (int i = 0; i < viewPagerAdapter.getCount(); i++)
        {
            CallbacksTabEdit tab = (CallbacksTabEdit) viewPagerAdapter.getItem(i);
            tab.setBrew(brew);
        }
    }

    @Override
    public void onReturn(Fragment fromFragment, String fromTabType)
    {
        Log.i(TAG, "onReturn: " + fromFragment.getClass().getSimpleName() + " " + fromTabType + " "+ tabType);
        if (fromFragment instanceof FragmentForm)
        {
            toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, R.raw.photo_coffee_cherries, R.raw.photo_coffee_cherries);
        }
        else if (fromFragment instanceof FragmentForm)
        {
            toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, R.raw.photo_coffee_cherries, R.raw.photo_coffee_cherries);
        }
        else if (fromFragment instanceof FragmentDetails)
        {
            FragmentDetails fragmentDetails = (FragmentDetails) fromFragment;
            switch (fragmentDetails.getDetailsType())
            {
                case Values.BREW_RECIPE:
                    toolbarAnimation(getActivity(), R.anim.slide_right_out, R.anim.slide_left_in, R.raw.photo_coffee_cherries, R.raw.photo_coffee_cherries);
                    break;
                case Values.COFFEE:
                    toolbarAnimation(getActivity(), R.anim.slide_right_out, R.anim.slide_left_in, R.raw.photo_coffee_cherries, R.raw.photo_coffee_cherries);
                    break;
            }
        }
        else if (fromFragment instanceof FragmentTabHost && fromTabType.equals(Values.TAB_BREW_DETAILS))
        {
            toolbarAnimation(getActivity(), R.anim.slide_right_out, R.anim.slide_left_in, R.raw.photo_coffee_cherries, R.raw.photo_coffee_cherries);
        }
        else if (fromFragment instanceof FragmentTabHost && fromTabType.equals(Values.TAB_BREW_EDIT) && !tabType.equals(Values.TAB_BREW_DETAILS))
        {
            Uri uri = ImageUtils.getBrewPhotoUri(getContext(), brew);
            toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, uri.getPath(), R.raw.photo_pour_over);
        }
        else if (fromFragment instanceof FragmentTabHost && fromTabType.equals(Values.TAB_BREW_NEW))
        {
            toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, R.raw.photo_coffee_cherries, R.raw.photo_coffee_cherries);
        }
    }

    @Override
    public void onResume()
    {
        Log.i(TAG, "onResume: ");
        super.onResume();
//        dataSourceBrew.open();
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
//        dataSourceBrew.close();
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