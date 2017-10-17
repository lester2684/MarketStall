package com.example.mrl.marketstall.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.adapter.RecyclerGenericAdapter;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.model.Item;
import com.example.mrl.marketstall.model.Suggestion;
import com.example.mrl.marketstall.ui.Animations;
import com.example.mrl.marketstall.ui.DividerItemDecoration;
import com.example.mrl.marketstall.utils.ImageUtils;
import com.example.mrl.marketstall.utils.Utils;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.viewholder.RecyclerViewHolder;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.mrl.marketstall.R.id.fab_menu;

public class FragmentRecycler extends Fragment implements Callbacks
{
    private final String TAG = getClass().getSimpleName();
    private View view;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabAddItem;
    private FloatingActionButton fabForecast;
    private FloatingActionButton fab1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerGenericAdapter<Item> itemRecyclerGenericAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference itemCloudEndPoint;
    private DatabaseReference userCloudEndPoint;
    private List<Item> recyclerItems;
    private List<Item> itemsAll;
    private List<String> groupedItemsNames;
    private List<Suggestion> suggestions;
    private List<Suggestion> suggestionsSearch;
    private MaterialSearchView searchView;
    private String userID;
    private boolean searching = false;
    private String recyclerType;
    private String itemName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.fragment_recycler, container, false);

        setupValues();
        setupToolbar();
        setupFAB();
        setupSwipeRefreshLayout();
        return view;
    }

    private void setupValues()
    {
        fabMenu = getActivity().findViewById(fab_menu);
        fabAddItem = getActivity().findViewById(R.id.fab3);
        fabForecast = getActivity().findViewById(R.id.fab2);
        fab1 = getActivity().findViewById(R.id.fab1);

        searchView = getActivity().findViewById(R.id.search_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recyclerView);

        itemName = getArguments().getString(Values.SELECTED_ITEM_NAME);
        if (itemName != null) {
            itemName = itemName.toLowerCase();
        }
        userID = getArguments().getString(Values.USER);
        recyclerType = getArguments().getString(Values.RECYCLER_TYPE);

        itemsAll = new ArrayList<>();
        recyclerItems = new ArrayList<>();
        suggestions = new ArrayList<>();
        groupedItemsNames = new ArrayList<>();
        suggestionsSearch = new ArrayList<>();
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        itemCloudEndPoint = mDatabase.child("items");
        userCloudEndPoint = mDatabase.child("users").child(userID);
        itemCloudEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemsAll.clear();
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
                    Item item = itemSnapshot.getValue(Item.class);
                    switch (recyclerType)
                    {
                        case Values.GROUPED_ITEMS:
                            if (!groupedItemsNames.contains(item.getName().toLowerCase())) {
                                groupedItemsNames.add(item.getName().toLowerCase());
                                itemsAll.add(item);
                            }
                            break;
                        case Values.ITEMS:
                            if (item.getName().equalsIgnoreCase(itemName)) {
                                itemsAll.add(item);
                            }
                            break;
                    }
                }
                setupRecyclerView(itemsAll);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
        userCloudEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                suggestions.clear();
                for (DataSnapshot suggestionSnapshot: dataSnapshot.getChildren()){
                    Suggestion suggestion = suggestionSnapshot.getValue(Suggestion.class);
                    suggestions.add(suggestion);
                }
                setupSearch();
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
        switch(recyclerType)
        {
            case Values.GROUPED_ITEMS:
                collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
                break;
            case Values.ITEMS:
                collapsingToolbarLayout.setTitle(itemName.substring(0,1).toUpperCase() + itemName.substring(1));
                break;
        }
        setHasOptionsMenu(true);
    }

    private void setupFAB()
    {
        fabMenu.setOnMenuButtonClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (fabMenu.isOpened())
                {
                    fabMenu.close(true);
                }
                else
                {
                    fabMenu.open(true);
                }
            }
        });
        if (fabMenu.isOpened())
        {
            int delay = getResources().getInteger(R.integer.fab_delay);
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    setFAB();
                }
            }, delay);
        }
        else
        {
            setFAB();
        }
    }

    private void setupSwipeRefreshLayout()
    {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                searching = false;
                updateList(itemsAll);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupRecyclerView(List<Item> list)
    {
        TextView blankMessage = view.findViewById(R.id.blank_message);
        if (list.size() == 0)
        {
            blankMessage.setText(getString(R.string.add_item_message));
            blankMessage.setVisibility(View.VISIBLE);
        }
        else
        {
            blankMessage.setVisibility(View.GONE);
        }
        itemRecyclerGenericAdapter = new RecyclerGenericAdapter<Item>(getActivity(), list)
        {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked, List item)
            {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.recycler_row, parent, false);
                return new RecyclerViewHolder(view, onRecyclerItemClicked);
            }

            @Override
            public void onBindData(Context context, RecyclerView.ViewHolder holder, Item item, int position)
            {
                RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
                recyclerViewHolder.title1.setText(item.getName());
                switch(recyclerType)
                {
                    case Values.GROUPED_ITEMS:
                        recyclerViewHolder.title2.setText("");
                        break;
                    case Values.ITEMS:
                        recyclerViewHolder.title2.setText(getString(R.string.text_date) + ": " + item.getDateCreated());
                        recyclerViewHolder.title3.setText("Price: " + String.format("%.2f", item.getPrice()) +" - Quality: " +item.getQuality_rating());
                        break;
                }
                Glide
                        .with(context)
                        .using(new FirebaseImageLoader())
                        .load(ImageUtils.getImage(item))
                        .error(R.raw.photo_coffee_cup)
                        .crossFade()
                        .into(recyclerViewHolder.circularImageView);
            }

            @Override
            public OnRecyclerItemClicked onGetRecyclerItemClickListener()
            {
                return new OnRecyclerItemClicked()
                {
                    @Override
                    public void onItemClicked(View view, int position)
                    {
                        switch(recyclerType)
                        {
                            case Values.GROUPED_ITEMS:
                                toSelectedGroupedItem(position);
                                break;
                            case Values.ITEMS:
                                toSelectedItem(position);
                                break;
                        }
                    }
                };
            }
        };
        recyclerView.setAdapter(itemRecyclerGenericAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void setupSearch()
    {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList(query);
                searching = true;
                for (int i = 0; i < suggestions.size(); i++)
                {
                    Suggestion suggestion = suggestions.get(i);
                    if(suggestion.getName().equals(query))
                    {
                        suggestion.addCount();
                        userCloudEndPoint.child(String.valueOf(i)).child(Values.COUNT).setValue(suggestion.getCount());
                        return false;
                    }
                }
                suggestions.add(new Suggestion(query));
                userCloudEndPoint.setValue(suggestions);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!searching) {
                    searchList(newText);
                }
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                searching = false;
            }

            @Override
            public void onSearchViewClosed() {
            }
        });
        searchView.setCursorDrawable(R.drawable.custom_cursor);
    }

    private void setFAB()
    {
        fabAddItem.setVisibility(View.INVISIBLE);
        fab1.setVisibility(View.GONE);
        fabAddItem.setLabelText(getString(R.string.button_item));
        fabAddItem.setImageDrawable(getResources().getDrawable(R.drawable.fab_add, getActivity().getTheme()));
        fabAddItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addItem();
            }
        });

        switch(recyclerType)
        {
            case Values.GROUPED_ITEMS:
                fabForecast.setVisibility(View.GONE);
                break;
            case Values.ITEMS:
                fabForecast.setVisibility(View.INVISIBLE);
                fabForecast.setImageDrawable(getResources().getDrawable(R.drawable.ic_forecast, getActivity().getTheme()));
                fabForecast.setLabelText(getString(R.string.button_forecast));
                fabForecast.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forecast();
                    }
                });
                break;
        }

        if(!fabMenu.isShown())
        {
            showFabMenu();
        }
    }

    private void forecast() {
        Log.i(TAG, "forecast: ");
        Bundle bundle = new Bundle();
        bundle.putString(Values.SELECTED_ITEM_NAME, itemName);
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

    private void showFabMenu()
    {
        if (fabMenu.getVisibility() == View.INVISIBLE)
        {
            fabMenu.setVisibility(View.VISIBLE);
            int delay = 1000;
            fabMenu.hideMenuButton(false);
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    fabMenu.showMenuButton(true);
                }
            }, delay);
        }
    }

    private void updateList(List<Item> list)
    {
        Log.d(TAG, "updateList: ");
        suggestionsSearch.clear();
        suggestionsSearch.addAll(suggestions);
        Collections.sort(suggestionsSearch, new Comparator<Suggestion>() {
            @Override
            public int compare(Suggestion suggestion1, Suggestion suggestion2) {
                return Integer.compare(suggestion2.getCount(), suggestion1.getCount());
            }

        });
        List<Item> sortedItems = new ArrayList<>();
        ArrayList<String> suggestionsArray = new ArrayList<>();
        for (Suggestion s : suggestionsSearch)
        {
            suggestionsArray.add(s.getName());
            for (Item item : list)
            {
                if (!sortedItems.contains(item)) {
                    if (item.getName().toLowerCase().contains(s.getName().toLowerCase())) {
                        sortedItems.add(item);
                    }
                }
            }
        }
        Log.d(TAG, "updateList: " + sortedItems);
        for (Item item : list)
        {
            if (!sortedItems.contains(item))
            {
                sortedItems.add(item);
            }
        }
        String[] mStringArray = new String[suggestionsArray.size()];
        mStringArray = suggestionsArray.toArray(mStringArray);
        searchView.setSuggestions(mStringArray);
        recyclerItems = sortedItems;
        List<Item> temp = new ArrayList<>();
        for (Item item : itemsAll)
        {
            temp.add(item);
        }
        if (itemRecyclerGenericAdapter != null) {
            itemRecyclerGenericAdapter.clear();
            itemRecyclerGenericAdapter.addAll(recyclerItems);
            swipeRefreshLayout.setRefreshing(false);
        }
        itemsAll = temp;
    }

    private void searchList(String query)
    {
        List<Item> results = new ArrayList<>();
        if (query.isEmpty())
        {
            updateList(itemsAll);
        }
        for (Item item : itemsAll)
        {
            if (item.toString().toLowerCase().contains(query.toLowerCase()))
            {
                results.add(item);
            }
        }
        updateList(results);
    }

    private void toSelectedItem(final int position)
    {
        Log.i(TAG, "toSelectedItem: ");
        Bundle bundle = new Bundle();
        Item item = recyclerItems.get(position);
        Animations.toolbarAnimation(getActivity(), R.anim.slide_left_out, R.anim.slide_right_in, ImageUtils.getImage(item), R.raw.photo_pour_over);
        bundle.putString(Values.SELECTED_ITEM, item.getId());
        bundle.putBoolean(Values.EDIT_VALUE, false);
        bundle.putString(Values.DETAILS_TYPE, Values.ITEM);
        bundle.putString(Values.USER, userID);
        FragmentDetails fragment = new FragmentDetails();
        fragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTag())
                .commit();
    }

    private void toSelectedGroupedItem(final int position)
    {
        Log.i(TAG, "toSelectedGroupedItem: ");
        Bundle bundle = new Bundle();
        Item item = recyclerItems.get(position);
        Animations.toolbarAnimation(getActivity(), R.anim.slide_left_out, R.anim.slide_right_in, ImageUtils.getImage(item), R.raw.photo_pour_over);
        bundle.putString(Values.SELECTED_ITEM_NAME, item.getName());
        bundle.putString(Values.RECYCLER_TYPE, Values.ITEMS);
        bundle.putString(Values.USER, userID);
        FragmentRecycler fragment = new FragmentRecycler();
        fragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTag())
                .commit();
    }

    private void addItem()
    {
        Log.i(TAG, "addItem: ");
        Animations.toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, null, R.raw.photo_pour_over);
        Bundle bundle = new Bundle();
        bundle.putString(Values.FORM_TYPE, Values.ITEM);
        bundle.putBoolean(Values.EDIT_VALUE, false);
        FragmentForm fragment = new FragmentForm();
        fragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out, R.anim.slide_up_in, R.anim.slide_down_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTAG())
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case MaterialSearchView.REQUEST_VOICE:
                if (resultCode == RESULT_OK)
                {
                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && matches.size() > 0) {
                        String searchWrd = matches.get(0);
                        if (!TextUtils.isEmpty(searchWrd)) {
                            searchView.setQuery(searchWrd, false);
                        }
                    }
                    return;
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
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
        if (searchView.isSearchOpen())
        {
            searchView.closeSearch();
        }
        else
            {
            getActivity().onBackPressed();
        }

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
        updateList(itemsAll);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Log.i(TAG, "onPause: ");
        super.onPause();
        if (fabMenu.isOpened())
        {
            fabMenu.close(true);
        }
        if (searchView.isSearchOpen())
        {
            searchView.closeSearch();
        }
        Utils.closeKeyboard(getActivity());
    }

    @Override
    public String getTabType()
    {
        return null;
    }
}