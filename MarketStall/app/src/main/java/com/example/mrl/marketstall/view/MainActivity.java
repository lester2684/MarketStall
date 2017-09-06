package com.example.mrl.marketstall.view;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.view.fragments.FragmentTabHost;

import static com.example.mrl.marketstall.utils.Utils.getCurrentFragment;
import static com.example.mrl.marketstall.value.Values.WRITE_EXTERNAL_RESULT;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AppBarLayout.OnOffsetChangedListener{

    private final String TAG = getClass().getSimpleName();
    private Callbacks currentFragmentCallback;
    private Callbacks nextFragmentCallback;
    private ActionBarDrawerToggle toggle;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        setupToolbar();
        toRecycler();
    }

    private void checkPermissions()
    {
        int has_Read_external_storage_Permission = ContextCompat.checkSelfPermission(MainActivity.this, Values.READ_EXTERNAL_STORAGE);
        if (has_Read_external_storage_Permission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Values.READ_EXTERNAL_STORAGE}, Values.WRITE_EXTERNAL_RESULT);
        }
    }

    private void setupToolbar()
    {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case WRITE_EXTERNAL_RESULT:
                if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "WRITE_EXTERNAL_RESULT Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}