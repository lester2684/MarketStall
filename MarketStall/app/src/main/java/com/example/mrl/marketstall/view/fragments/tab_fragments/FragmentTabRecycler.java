package com.example.mrl.marketstall.view.fragments.tab_fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.adapter.RecyclerGenericAdapter;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.model.Item;
import com.example.mrl.marketstall.ui.Animations;
import com.example.mrl.marketstall.ui.DividerItemDecoration;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.view.fragments.FragmentForm;
import com.example.mrl.marketstall.view.fragments.FragmentTabHost;
import com.example.mrl.marketstall.viewholder.RecyclerViewHolder;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.mrl.marketstall.R.id.fab_menu;

public class FragmentTabRecycler extends Fragment implements Callbacks
{
    private final String TAG = getClass().getSimpleName();
    private View view;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabAddItem;
    private FloatingActionButton fab2;
    private FloatingActionButton fab1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerGenericAdapter<Item> itemRecyclerGenericAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference itemCloudEndPoint;
    private List<Item> items;
    private String recyclerType;
    private String tabType;

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
        fabMenu = (FloatingActionMenu) getActivity().findViewById(fab_menu);
        fabAddItem = (FloatingActionButton) getActivity().findViewById(R.id.fab3);
        fab2 = (FloatingActionButton) getActivity().findViewById(R.id.fab2);
        fab1 = (FloatingActionButton) getActivity().findViewById(R.id.fab1);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerType = getArguments().getString(Values.RECYCLER_TYPE);
        tabType = getArguments().getString(Values.TAB_TYPE);

        items = new ArrayList<>();
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        itemCloudEndPoint = mDatabase.child("items");
        itemCloudEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
                    Item item = itemSnapshot.getValue(Item.class);
                    items.add(item);
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
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar_layout);
        appBarLayout.setExpanded(true);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
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
                updateList();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupRecyclerView()
    {
        TextView blankMessage = (TextView) view.findViewById(R.id.blank_message);
        switch (recyclerType)
        {
            case Values.ITEM:
                if (items.size() == 0)
                {
                    blankMessage.setText(getString(R.string.add_item_message));
                    blankMessage.setVisibility(View.VISIBLE);
                }
                else
                {
                    blankMessage.setVisibility(View.GONE);
                }
                itemRecyclerGenericAdapter = new RecyclerGenericAdapter<Item>(getActivity(), items)
                {
                    @Override
                    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked)
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
                        recyclerViewHolder.title2.setText(getString(R.string.text_date) + ": " + item.getDateCreated());

//                        Glide
//                                .with(context)
//                                .load(ImageUtils.getBrewPhotoUri(context,item))
//                                .error(R.raw.photo_coffee_cup)
//                                .crossFade()
//                                .into(recyclerViewHolder.circularImageView);
                    }

                    @Override
                    public OnRecyclerItemClicked onGetRecyclerItemClickListener()
                    {
                        return new OnRecyclerItemClicked() {
                            @Override
                            public void onItemClicked(View view, int position)
                            {
                                toSelected(position);
                            }
                        };
                    }
                };
                recyclerView.setAdapter(itemRecyclerGenericAdapter);
                break;
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void setFAB()
    {
        fabAddItem.setVisibility(View.INVISIBLE);
        fab2.setVisibility(View.GONE);
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

        if(!fabMenu.isShown())
        {
            showFabMenu();
        }
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

    private void updateList()
    {
        switch (recyclerType)
        {
            case Values.ITEM:
                itemRecyclerGenericAdapter.setItems(items);
                break;
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void toSelected(final int position)
    {
        Log.i(TAG, "toSelected: ");
        Bundle bundle = new Bundle();
        switch (recyclerType)
        {
            case Values.ITEM:
                Animations.toolbarAnimation(getActivity(), R.anim.slide_left_out, R.anim.slide_right_in, R.raw.photo_pour_over, R.raw.photo_pour_over);
                bundle.putString(Values.SELECTED_ITEM, items.get(position).getId());
                bundle.putBoolean(Values.EDIT_VALUE, true);
                bundle.putString(Values.TAB_TYPE, Values.TAB_ITEM_DETAILS);
                FragmentTabHost fragmentTabHost = new FragmentTabHost();
                fragmentTabHost.setArguments(bundle);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                        .replace(R.id.fragment_container_main, fragmentTabHost, fragmentTabHost.getTAG())
                        .addToBackStack(fragmentTabHost.getTag())
                        .commit();
                break;
        }
    }

    private void addItem()
    {
        Log.i(TAG, "addItem: ");
        Animations.toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, R.raw.photo_pour_over, R.raw.photo_pour_over);
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
    public void onBackPressedCallback()
    {
        Log.i(TAG, "backButtonWasPressed: ");

    }

    @Override
    public void backPress()
    {
        getActivity().onBackPressed();

    }

    public String getTAG()
    {
        return TAG;

    }

    @Override
    public void onReturn(Fragment fromFragment, String fromTabType)
    {
        Log.i(TAG, "onReturn: from " + fromFragment.getClass().getSimpleName());

    }

    @Override
    public void toolbarExpanded()
    {
        fabMenu.showMenuButton(true);

    }

    @Override
    public void toolbarCollapsed()
    {
        fabMenu.hideMenuButton(true);

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
        if (fabMenu.isOpened())
        {
            fabMenu.close(true);
        }
    }

    @Override
    public String getTabType()
    {
        return tabType;
    }
}
