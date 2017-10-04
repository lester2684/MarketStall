package com.example.mrl.marketstall.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.view.fragments.FragmentMap;
import com.example.mrl.marketstall.view.fragments.FragmentRecycler;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.example.mrl.marketstall.R.id.fab_menu;
import static com.example.mrl.marketstall.utils.Utils.getCurrentFragment;

public class MainActivity extends RuntimePermissionsActivity implements NavigationView.OnNavigationItemSelectedListener, AppBarLayout.OnOffsetChangedListener,  View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private final String TAG = getClass().getSimpleName();
    private Callbacks currentFragmentCallback;
    private Callbacks nextFragmentCallback;
    private ActionBarDrawerToggle toggle;

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser user;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private ImageView icon;
    private LinearLayout googleAuthLayout;
    private LayoutInflater inflater;
    public ProgressDialog mProgressDialog;
    private FrameLayout frameLayout;
    private DrawerLayout drawLayout;
    private FloatingActionMenu fabMenu;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        setupToolbar();
        setupView();
        setupAuth();
        signIn();
        updateUI(user);
    }
    private void checkPermissions()
    {
        MainActivity.super.requestAppPermissions(Values.permissions, R.string.runtime_permissions_error_message, Values.permissionCode);
    }

    private void setupToolbar()
    {
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        toolbar = findViewById(R.id.toolbar);
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
                                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                                drawer.openDrawer(GravityCompat.START);
                            }
                        });
                    }
                }
            }
        });
        TextView toolbarTextView = findViewById(R.id.toolbar_text_view);
        toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
        ImageView toolbarImageViewSecond = findViewById(R.id.toolbar_image_main);
        Glide
                .with(this)
                .load(R.raw.photo_coffee_cherries)
                .into(toolbarImageViewSecond);
    }

    private void setupView()
    {
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        googleAuthLayout = (LinearLayout) inflater.inflate(R.layout.fragment_google_auth, null);
        frameLayout = findViewById(R.id.fragment_container_main);
        frameLayout.addView(googleAuthLayout);

        drawLayout = findViewById(R.id.drawer_layout);
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        icon = findViewById(R.id.google_icon);
        Glide
                .with(this)
                .load(R.raw.firebase_lockup_400)
                .into(icon);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
        fabMenu = this.findViewById(fab_menu);
        fabMenu.hideMenuButton(true);

        drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void setupAuth()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        mAuth = FirebaseAuth.getInstance();
    }

    private void toRecycler()
    {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fabMenu.showMenuButton(true);
        frameLayout.removeAllViews();
        Bundle bundle = new Bundle();
        bundle.putString(Values.RECYCLER_TYPE, Values.ITEM);
        bundle.putString(Values.USER, user.getUid());
        FragmentRecycler fragment = new FragmentRecycler();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out, R.anim.slide_up_in, R.anim.slide_down_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTAG())
                .commit();
    }

    private void toMap()
    {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fabMenu.hideMenuButton(true);
        frameLayout.removeAllViews();
        FragmentMap fragment = new FragmentMap();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out, R.anim.slide_up_in, R.anim.slide_down_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTAG())
                .commit();
    }

    private void toGoogleAuth()
    {
        fabMenu.setVisibility(View.INVISIBLE);
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        setupView();
        setupAuth();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut()
    {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess()
    {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user)
    {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            this.user = mAuth.getCurrentUser();
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);

            drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        } else {
            drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Sign in first!",     Toast.LENGTH_SHORT).show();
                }
            });
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    private void updateUser(FirebaseUser user)
    {
        hideProgressDialog();
        if (user != null)
        {

            this.user = mAuth.getCurrentUser();

            drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        }
        else
            {
                drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Sign in first!",     Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public FirebaseUser getUser()
    {
        return user;
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            updateUI(mAuth.getCurrentUser());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void hideProgressDialog()
    {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUser(currentUser);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUser(currentUser);
    }

    @Override
    public void onClick(View view)
    {
        int i = view.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.disconnect_button) {
            revokeAccess();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess())
                {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    // Google Sign In failed, update UI appropriately
                    updateUI(null);
                }
                break;
        }

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
        if ((-(offset)) < appBarLayout.getTotalScrollRange()-160)
        {
            fabMenu.showMenuButton(true);
        }
        else if ((-(offset)) >= appBarLayout.getTotalScrollRange()-160)
        {
            fabMenu.hideMenuButton(true);
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
                if (getSupportFragmentManager().getBackStackEntryCount() > 1)
                {
                    super.onBackPressed();
                    nextFragmentCallback = (Callbacks) getCurrentFragment(getSupportFragmentManager());
                    if(nextFragmentCallback != null)
                    {
                        nextFragmentCallback.onReturn(fragment, currentFragmentCallback.getTabType());
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_user_auth:
                toGoogleAuth();
                break;
            case R.id.nav_list:
                toRecycler();
                break;
            case R.id.nav_map:
                toMap();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}