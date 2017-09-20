package com.example.mrl.marketstall.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.view.fragments.FragmentTabHost;

import static com.example.mrl.marketstall.utils.Utils.getCurrentFragment;

public class MainActivity extends RuntimePermissionsActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AppBarLayout.OnOffsetChangedListener{

    private final String TAG = getClass().getSimpleName();
    private Callbacks currentFragmentCallback;
    private Callbacks nextFragmentCallback;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        setupToolbar();
        toRecycler();

    }

    @Override
    public void onPermissionsGranted(int requestCode) {
    }

    private void checkPermissions()
    {
        MainActivity.super.requestAppPermissions(Values.permissions, R.string.runtime_permissions_error_message, Values.permissionCode);
    }

    private void setupToolbar()
    {
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            @Override
            public void onBackStackChanged()
            {
                if (getSupportFragmentManager().getBackStackEntryCount() > 1)
                {
                    // show back button
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
                    }
                }
                else
                {
                    //show hamburger
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        toggle.syncState();
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                                drawer.openDrawer(GravityCompat.START);
                            }
                        });
                    }
                }

            }
        });
        TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_text_view);
        toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
        ImageView toolbarImageViewSecond = (ImageView) findViewById(R.id.toolbar_image_main);
        Glide
                .with(this)
                .load(R.raw.photo_coffee_cherries)
                .into(toolbarImageViewSecond);
    }

    @Override
    public void onClick(View view)
    {

    }

    private void toRecycler()
    {
        Bundle bundle = new Bundle();
        bundle.putString(Values.TAB_TYPE, Values.TAB_RECYCLERS);
        FragmentTabHost fragment = new FragmentTabHost();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out, R.anim.slide_up_in, R.anim.slide_down_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTAG())
                .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset)
    {
        currentFragmentCallback = (Callbacks) getCurrentFragment(getSupportFragmentManager());
        if(currentFragmentCallback != null)
        {
            if ((-(offset)) < appBarLayout.getTotalScrollRange()-160)
            {
                currentFragmentCallback.toolbarExpanded();
            }
            else if ((-(offset)) >= appBarLayout.getTotalScrollRange()-160)
            {
                currentFragmentCallback.toolbarCollapsed();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        assert drawer != null;

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            currentFragmentCallback = (Callbacks) getCurrentFragment(getSupportFragmentManager());
            if(currentFragmentCallback != null)
            {
                currentFragmentCallback.onBackPressedCallback();
                Fragment fragment = (Fragment) currentFragmentCallback;
                Log.i(TAG, "onBackPressed: ");
                if (getSupportFragmentManager().getBackStackEntryCount() > 1)
                {
                    super.onBackPressed();
                    nextFragmentCallback = (Callbacks) getCurrentFragment(getSupportFragmentManager());
                }
                if(nextFragmentCallback != null)
                {
                    nextFragmentCallback.onReturn(fragment, currentFragmentCallback.getTabType());
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}