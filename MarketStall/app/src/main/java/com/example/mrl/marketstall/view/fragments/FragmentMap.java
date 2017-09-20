package com.example.mrl.marketstall.view.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.custom.WorkaroundMapFragment;
import com.example.mrl.marketstall.model.Item;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentMap extends Fragment {

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
        setupMap();

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
                setupMap();
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
        collapsingToolbarLayout.setTitle(" ");
    }

    private void setupFAB() {
        fabMenu.hideMenuButton(true);
    }

    private void setupMap() {
        final NestedScrollView nestedScrollView = getActivity().findViewById(R.id.nested_scroll_view);
        WorkaroundMapFragment workaroundMapFragment = (WorkaroundMapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        workaroundMapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                nestedScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        workaroundMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMyLocationEnabled(true);
                for (Item item : items) {
                    LatLng itemLocation = new LatLng(item.getLatitude(), item.getLongitude());
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    googleMap.addMarker(new MarkerOptions().position(itemLocation).title(item.getName()));
                }
            }
        });
        nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroyView()
    {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
        Fragment workaroundMapFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        if (workaroundMapFragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(workaroundMapFragment).commit();
        }
        Fragment fragment = getFragmentManager().findFragmentById(R.id.map);
        if (workaroundMapFragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }
}
