package com.example.mrl.marketstall.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.adapter.RecyclerGenericAdapter;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.model.Forecast;
import com.example.mrl.marketstall.ui.Animations;
import com.example.mrl.marketstall.ui.DividerItemDecoration;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.viewholder.RecyclerViewHolder;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.mrl.marketstall.R.id.fab_menu;

public class FragmentForecast extends Fragment implements Callbacks
{
    private final String TAG = getClass().getSimpleName();
    private View view;
    private FloatingActionMenu fabMenu;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerGenericAdapter recyclerGenericAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference itemCloudEndPoint;
    private List weekForecast;
    private List recyclerForecast;
    private String itemName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.fragment_forecast, container, false);

        setupValues();
        setupToolbar();
        setupFAB();
        setupSwipeRefreshLayout();
        return view;
    }

    private void setupValues()
    {
        fabMenu = getActivity().findViewById(fab_menu);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recyclerView);

        itemName = getArguments().getString(Values.SELECTED_ITEM_NAME);

        weekForecast = new ArrayList<>();
        recyclerForecast = new ArrayList<>();
        mDatabase =  FirebaseDatabase.getInstance().getReference();

        itemCloudEndPoint = mDatabase.child("forecasts").child(itemName);
        itemCloudEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                weekForecast.clear();
                for (DataSnapshot arraySnapshot: dataSnapshot.getChildren()){
                    Forecast forecast = arraySnapshot.getValue(Forecast.class);
                    weekForecast.add(forecast);
                }
                setupRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    private void setupToolbar()
    {
        AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar_layout);
        appBarLayout.setExpanded(true);

        CollapsingToolbarLayout collapsingToolbarLayout = getActivity().findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle("Price Forecast");
    }

    private void setupFAB()
    {
        Log.d(TAG, "setupFAB: ");
        fabMenu.setVisibility(View.INVISIBLE);
    }

    private void setupSwipeRefreshLayout()
    {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                updateList(weekForecast);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupRecyclerView()
    {
        TextView blankMessage = view.findViewById(R.id.blank_message);
        if (weekForecast.size() == 0)
        {
            blankMessage.setText(getString(R.string.add_item_message));
            blankMessage.setVisibility(View.VISIBLE);
        }
        else
        {
            blankMessage.setVisibility(View.GONE);
        }
        recyclerGenericAdapter = new RecyclerGenericAdapter<Forecast>(getActivity(), recyclerForecast)
        {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked, List forecast)
            {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.recycler_row, parent, false);
                return new RecyclerViewHolder(view, onRecyclerItemClicked);
            }

            @Override
            public void onBindData(Context context, RecyclerView.ViewHolder holder, Forecast forecast, int position)
            {
                RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
                recyclerViewHolder.title1.setText(forecast.getDay());
                recyclerViewHolder.title2.setText(getString(R.string.average_price) + ": $" + String.format("%.2f", forecast.getAverage()));
            }

            @Override
            public OnRecyclerItemClicked onGetRecyclerItemClickListener()
            {
                return new OnRecyclerItemClicked()
                {
                    @Override
                    public void onItemClicked(View view, int position)
                    {

                    }
                };
            }
        };
        recyclerView.setAdapter(recyclerGenericAdapter);
        updateList(weekForecast);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void updateList(List list) {
        recyclerForecast = list;
        if (recyclerGenericAdapter != null) {
            recyclerGenericAdapter.clear();
            recyclerGenericAdapter.addAll(recyclerForecast);
            recyclerGenericAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
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
        Log.d(TAG, "backPress: ");
    }

    public String getTAG()
    {
        return TAG;

    }

    @Override
    public void onReturn(Fragment fromFragment, String fromTabType)
    {
        Log.i(TAG, "onReturn: from " + fromFragment.getClass().getSimpleName());
        if (fromFragment instanceof FragmentForm)
        {
            Animations.toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, null, R.raw.photo_coffee_cherries);
        }
        else if (fromFragment instanceof FragmentDetails)
        {
            Animations.toolbarAnimation(getActivity(), R.anim.slide_right_out, R.anim.slide_left_in, null, R.raw.photo_coffee_cherries);
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
    }

    @Override
    public String getTabType()
    {
        return null;
    }
}