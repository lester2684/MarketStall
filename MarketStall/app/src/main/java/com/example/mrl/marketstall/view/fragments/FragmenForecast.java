package com.example.mrl.marketstall.view.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.model.Item;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmenForecast extends Fragment implements Callbacks {

    private final String TAG = getClass().getSimpleName();
    private View view;
    private Toolbar toolbar;
    private DatabaseReference mDatabase;
    private DatabaseReference itemCloudEndPoint;
    private FloatingActionMenu fabMenu;
    private List<Item> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_map, container, false);
        setupValues();
        setupToolbar();
        setupFAB();

        return view;
    }

    private void setupValues() {
        toolbar = getActivity().findViewById(R.id.toolbar);
        fabMenu = getActivity().findViewById(R.id.fab_menu);
        items = new ArrayList<>();

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        itemCloudEndPoint = mDatabase.child("items");
        itemCloudEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
                    Item item = itemSnapshot.getValue(Item.class);
                    items.add(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    private void setupToolbar() {
        AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar_layout);
        appBarLayout.setExpanded(true);
        toolbar.getMenu().clear();
        CollapsingToolbarLayout collapsingToolbarLayout = getActivity().findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(getString(R.string.map));
    }

    private void setupFAB() {
        fabMenu.hideMenuButton(true);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void onReturn(Fragment fragment, String fromTabType) {

    }

    @Override
    public void onBackPressedCallback() {

        getActivity().onBackPressed();
    }

    @Override
    public void backPress() {

    }

    @Override
    public String getTabType() {
        return null;
    }
}
