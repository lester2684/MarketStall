package com.example.mrl.marketstall.view.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.adapter.RecyclerGenericAdapter;
import com.example.mrl.marketstall.custom.WorkaroundMapFragment;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.model.Item;
import com.example.mrl.marketstall.model.ItemInfo;
import com.example.mrl.marketstall.utils.ImageUtils;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.viewholder.RecyclerViewHolderDetailTextView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
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

public class FragmentDetails extends Fragment implements Callbacks {
    private final String TAG = getClass().getSimpleName();
    private View view;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabEdit;
    private FloatingActionButton fabDelete;
    private FloatingActionButton fabForecast;
    private RecyclerView recyclerViewDetails;
    private RecyclerGenericAdapter<ItemInfo> detailsInfoRecyclerAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference itemCloudEndPoint;
    private Item item;
    private List<ItemInfo> detailsList;
    private String detailsType;
    private WorkaroundMapFragment workaroundMapFragment;
    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_details, container, false);

        setupValues();
        setupToolbar();
        return view;
    }

    private void setupValues() {
        toolbar = getActivity().findViewById(R.id.toolbar);
        collapsingToolbarLayout = getActivity().findViewById(R.id.collapsing_toolbar_layout);
        fabMenu = getActivity().findViewById(R.id.fab_menu);
        fabEdit = getActivity().findViewById(R.id.fab2);
        fabDelete = getActivity().findViewById(R.id.fab3);
        fabForecast = getActivity().findViewById(R.id.fab1);
        recyclerViewDetails = view.findViewById(R.id.recyclerViewDetails);

        detailsType = getArguments().getString(Values.DETAILS_TYPE);
        userID = getArguments().getString(Values.USER);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        itemCloudEndPoint = mDatabase.child("items");
        itemCloudEndPoint.child(getArguments().getString(Values.SELECTED_ITEM)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    item = dataSnapshot.getValue(Item.class);
                    detailsList = new ArrayList<>();
                    for (ItemInfo detail : item.detailsToForm()) {
                        if (detail.getShowDetail()) {
                            detailsList.add(detail);
                        }
                    }
                    collapsingToolbarLayout.setTitle(item.getName());
                    setupRecyclerViewDetails();
                    setupMap();
                    setupFAB();
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
    }

    private void setupFAB() {
        if (userID.equals(item.getUserId())) {
            fabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fabMenu.isOpened()) {
                        fabMenu.close(true);
                    } else {
                        fabMenu.open(true);
                    }
                }
            });
            if (fabMenu.isOpened()) {
                fabMenu.close(true);
                int delay = getResources().getInteger(R.integer.fab_delay);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setFAB();
                    }
                }, delay);
            } else {
                setFAB();
            }
        }
        else
        {
            fabMenu.setVisibility(View.INVISIBLE);
            fabMenu.hideMenuButton(true);
        }
    }

    private void setupRecyclerViewDetails() {
        detailsInfoRecyclerAdapter = new RecyclerGenericAdapter<ItemInfo>(getActivity(), detailsList) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked, List items) {
                final View holder;
                ItemInfo itemInfo = (ItemInfo) items.get(viewType);
                switch (itemInfo.getDetailViewType()) {
                    case Values.TEXT_VIEW:
                        holder = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_row_details_text_view, parent, false);
                        return new RecyclerViewHolderDetailTextView(holder);
                    default:
                        holder = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_row_details_text_view, parent, false);
                        return new RecyclerViewHolderDetailTextView(holder);
                }
            }

            @Override
            public void onBindData(Context context, RecyclerView.ViewHolder holder, ItemInfo itemInfo, int position) {
                switch (itemInfo.getDetailViewType()) {
                    case Values.TEXT_VIEW:
                        RecyclerViewHolderDetailTextView recyclerViewHolderDetailTextView = (RecyclerViewHolderDetailTextView) holder;
                        recyclerViewHolderDetailTextView.text.setText(itemInfo.getText());
                        if (itemInfo.getImage() != 0) {
                            Glide.with(getContext())
                                    .load(itemInfo.getImage())
                                    .into(recyclerViewHolderDetailTextView.icon);
                        }
                        break;
                }
            }

            @Override
            public OnRecyclerItemClicked onGetRecyclerItemClickListener() {
                return new OnRecyclerItemClicked() {
                    @Override
                    public void onItemClicked(View view, int position) {

                    }
                };
            }
        };
        recyclerViewDetails.setAdapter(detailsInfoRecyclerAdapter);
        recyclerViewDetails.setHasFixedSize(true);
        recyclerViewDetails.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }

    private void setFAB() {
        fabEdit.setVisibility(View.INVISIBLE);
        fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit, getActivity().getTheme()));
        fabEdit.setLabelText(getString(R.string.button_edit));
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });

        fabDelete.setVisibility(View.INVISIBLE);
        fabDelete.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete, getActivity().getTheme()));
        fabDelete.setLabelText(getString(R.string.button_delete));
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        fabForecast.setVisibility(View.INVISIBLE);
        fabForecast.setImageDrawable(getResources().getDrawable(R.drawable.ic_forecast, getActivity().getTheme()));
        fabForecast.setLabelText(getString(R.string.button_forecast));
        fabForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forecast();
            }
        });
        
        if (!fabMenu.isShown()) {
            showFabMenu();
        }
    }

    private void setupMap() {
        workaroundMapFragment = new WorkaroundMapFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.map_fragment_container2, workaroundMapFragment, workaroundMapFragment.getTAG())
                .commitAllowingStateLoss();
        workaroundMapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                NestedScrollView nestedScrollView = getActivity().findViewById(R.id.nested_scroll_view);
                nestedScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        workaroundMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng itemLocation = new LatLng(item.getLatitude(), item.getLongitude());
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.addMarker(new MarkerOptions().position(itemLocation).title(item.getName()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(itemLocation,15));
            }
        });
        NestedScrollView nestedScrollView = getActivity().findViewById(R.id.nested_scroll_view);
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

    private void showFabMenu() {
        if (fabMenu.getVisibility() == View.INVISIBLE) {
            fabMenu.setVisibility(View.VISIBLE);
            int delay = 1000;
            fabMenu.hideMenuButton(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fabMenu.showMenuButton(true);
                }
            }, delay);
        }
    }

    private void delete() {
        Log.i(TAG, "delete: ");
        switch (detailsType) {
            case Values.ITEM:
                ImageUtils.deleteImage(getActivity(), item);
                itemCloudEndPoint.child(item.getId()).setValue(null);
        }
        backPress();
    }

    private void edit() {
        Log.i(TAG, "edit: ");
        Bundle bundle = new Bundle();
        switch (detailsType) {
            case Values.ITEM:
                bundle.putString(Values.FORM_TYPE, Values.ITEM);
                bundle.putString(Values.SELECTED_ITEM, item.getId());
                bundle.putBoolean(Values.EDIT_VALUE, true);
                FragmentForm fragmentForm = new FragmentForm();
                fragmentForm.setArguments(bundle);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out, R.anim.slide_up_in, R.anim.slide_down_out)
                        .replace(R.id.fragment_container_main, fragmentForm, fragmentForm.getTAG())
                        .addToBackStack(fragmentForm.getTag())
                        .commit();
                break;
        }


    }

    private void forecast() {
        Log.i(TAG, "forecast: ");
        Bundle bundle = new Bundle();
        bundle.putString(Values.ITEM_NAME, item.getName());
        FragmentForecast fragment = new FragmentForecast();
        fragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out, R.anim.slide_up_in, R.anim.slide_down_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTag())
                .commit();
    }

    public String getDetailsType() {

        return detailsType;
    }

    @Override
    public String getTAG() {

        return TAG;
    }

    @Override
    public void backPress() {
        getActivity().onBackPressed();

    }

    @Override
    public String getTabType() {
        return null;
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
        if (userID.equals(item.getUserId())) {
            if (fabMenu.isOpened()) {
                fabMenu.close(true);
            }
        }
    }

    @Override
    public void onBackPressedCallback() {
        Log.i(TAG, "onBackPressedCallback: ");

    }

    @Override
    public void onReturn(Fragment fromFragment, String fromTabType) {
        Log.i(TAG, "onReturn: ");
    }
}
