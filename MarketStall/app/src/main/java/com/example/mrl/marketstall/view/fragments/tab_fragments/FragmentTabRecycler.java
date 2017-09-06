package com.example.mrl.marketstall.view.fragments.tab_fragments;

import android.content.Context;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.adapter.RecyclerGenericAdapter;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.model.Brew;
import com.example.mrl.marketstall.model.BrewRecipe;
import com.example.mrl.marketstall.model.Coffee;
import com.example.mrl.marketstall.ui.Animations;
import com.example.mrl.marketstall.ui.DividerItemDecoration;
import com.example.mrl.marketstall.utils.ImageUtils;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.view.fragments.FragmentDetails;
import com.example.mrl.marketstall.view.fragments.FragmentForm;
import com.example.mrl.marketstall.view.fragments.FragmentTabHost;
import com.example.mrl.marketstall.viewholder.RecyclerViewHolder;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

import static com.example.mrl.marketstall.R.id.fab_menu;

public class FragmentTabRecycler extends Fragment implements Callbacks
{
    private final String TAG = getClass().getSimpleName();
    private View view;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabBrewRecipe;
    private FloatingActionButton fabCoffee;
    private FloatingActionButton fabBrew;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerGenericAdapter<Coffee> coffeeRecyclerGenericAdapter;
    private RecyclerGenericAdapter<Brew> brewRecyclerGenericAdapter;
    private RecyclerGenericAdapter<BrewRecipe> brewRecipeRecyclerGenericAdapter;
//    private DataSourceCoffee dataSourceCoffee;
//    private DataSourceBrew dataSourceBrew;
//    private DataSourceBrewRecipe dataSourceBrewRecipe;
    private List<Coffee> coffees;
    private List<Brew> brews;
    private List<BrewRecipe> brewRecipes;
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
        setupRecyclerView();
        updateCoffeeList();

        return view;
    }

    private void setupValues()
    {
        fabMenu = (FloatingActionMenu) getActivity().findViewById(fab_menu);
        fabBrewRecipe = (FloatingActionButton) getActivity().findViewById(R.id.fab2);
        fabCoffee = (FloatingActionButton) getActivity().findViewById(R.id.fab3);
        fabBrew = (FloatingActionButton) getActivity().findViewById(R.id.fab1);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerType = getArguments().getString(Values.RECYCLER_TYPE);
        tabType = getArguments().getString(Values.TAB_TYPE);

//        dataSourceCoffee = new DataSourceCoffee(getContext());
//        dataSourceBrew = new DataSourceBrew(getContext());
//        dataSourceBrewRecipe = new DataSourceBrewRecipe(getContext());
//        dataSourceCoffee.open();
//        dataSourceBrew.open();
//        dataSourceBrewRecipe.open();
//
//        brews = dataSourceBrew.getAll();
//        coffees = dataSourceCoffee.getAll();
//        brewRecipes = dataSourceBrewRecipe.getAll();
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
                updateCoffeeList();
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
            case Values.BREW:
                if (brews.size() == 0)
                {
                    blankMessage.setText(getString(R.string.add_brew_message));
                    blankMessage.setVisibility(View.VISIBLE);
                }
                else
                {
                    blankMessage.setVisibility(View.GONE);
                }
                brewRecyclerGenericAdapter = new RecyclerGenericAdapter<Brew>(getActivity(), brews)
                {
                    @Override
                    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked)
                    {
                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        View view = inflater.inflate(R.layout.recycler_row, parent, false);
                        return new RecyclerViewHolder(view, onRecyclerItemClicked);
                    }

                    @Override
                    public void onBindData(Context context, RecyclerView.ViewHolder holder, Brew item, int position)
                    {
                        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
                        recyclerViewHolder.title1.setText(item.getName());
                        recyclerViewHolder.title2.setText(getString(R.string.text_brew_date) + ": " + item.getBrewDate());

                        Glide
                                .with(context)
                                .load(ImageUtils.getBrewPhotoUri(context,item))
                                .error(R.raw.photo_coffee_cup)
                                .crossFade()
                                .into(recyclerViewHolder.circularImageView);
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
                recyclerView.setAdapter(brewRecyclerGenericAdapter);
                break;
            case Values.COFFEE:
                if (coffees.size() == 0)
                {
                    blankMessage.setText(getString(R.string.add_coffee_message));
                    blankMessage.setVisibility(View.VISIBLE);
                }
                else
                {
                    blankMessage.setVisibility(View.GONE);
                }
                coffeeRecyclerGenericAdapter = new RecyclerGenericAdapter<Coffee>(getActivity() , coffees)
                {
                    @Override
                    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked)
                    {
                        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_row, parent, false);
                        return new RecyclerViewHolder(view, onRecyclerItemClicked);
                    }

                    @Override
                    public void onBindData(Context context, RecyclerView.ViewHolder holder, Coffee item, int position)
                    {
                        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
                        recyclerViewHolder.title1.setText(item.getName());
                        if (!item.getLocation().trim().isEmpty())
                        {
                            recyclerViewHolder.title2.setText(item.getLocation());
                        }
                        if (!item.getRoastDate().trim().isEmpty() && !item.getRoaster().trim().isEmpty())
                        {
                            recyclerViewHolder.title3.setText(item.getRoaster() + ": " + item.getRoastDate());
                        }
                        else if (!item.getRoastDate().trim().isEmpty() && item.getRoaster().trim().isEmpty())
                        {
                            recyclerViewHolder.title3.setText( getString(R.string.unknown_roaster) + ": " + item.getRoastDate());
                        }
                        else if (item.getRoastDate().trim().isEmpty() && !item.getRoaster().trim().isEmpty())
                        {
                            recyclerViewHolder.title3.setText(item.getRoaster());
                        }
                        else if (item.getRoastDate().trim().isEmpty() && item.getRoaster().trim().isEmpty())
                        {
                            recyclerViewHolder.title3.setText(getString(R.string.unknown_roaster));
                        }
                        Glide
                                .with(getContext())
                                .load(ImageUtils.getCoffeePhotoUri(context, item))
                                .error(R.raw.photo_coffee_beans)
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
                                toSelected(position);
                            }
                        };
                    }
                };
                recyclerView.setAdapter(coffeeRecyclerGenericAdapter);
                break;
            case Values.BREW_RECIPE:
                if (brewRecipes.size() == 0)
                {
                    blankMessage.setText(getString(R.string.add_brew_recipe_message));
                    blankMessage.setVisibility(View.VISIBLE);
                }
                else
                {
                    blankMessage.setVisibility(View.GONE);
                }
                brewRecipeRecyclerGenericAdapter = new RecyclerGenericAdapter<BrewRecipe>(getActivity() , brewRecipes)
                {
                    @Override
                    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked)
                    {
                        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_row, parent, false);
                        return new RecyclerViewHolder(view, onRecyclerItemClicked);
                    }

                    @Override
                    public void onBindData(Context context, RecyclerView.ViewHolder holder, BrewRecipe item, int position)
                    {
                        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
                        recyclerViewHolder.title1.setText(item.getId() +"");
                        Glide
                                .with(getContext())
                                .load(R.raw.photo_coffee_beans)
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
                                toSelected(position);
                            }
                        };
                    }
                };
                recyclerView.setAdapter(brewRecipeRecyclerGenericAdapter);
                break;
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void setFAB()
    {
        fabBrew.setVisibility(View.INVISIBLE);
        fabBrew.setLabelText(getString(R.string.button_brew));
        fabBrew.setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
        fabBrew.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addBrew();
            }
        });

        fabBrewRecipe.setVisibility(View.INVISIBLE);
        fabBrewRecipe.setLabelText(getString(R.string.button_brew_recipe));
        fabBrewRecipe.setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
        fabBrewRecipe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addBrewRecipe();
            }
        });

        fabCoffee.setVisibility(View.INVISIBLE);
        fabCoffee.setLabelText(getString(R.string.button_coffee));
        fabCoffee.setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
        fabCoffee.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addCoffee();
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

    private void updateCoffeeList()
    {
//        brews = dataSourceBrew.getAll();
//        coffees = dataSourceCoffee.getAll();
//        brewRecipes = dataSourceBrewRecipe.getAll();
        switch (recyclerType)
        {
            case Values.BREW:
                brewRecyclerGenericAdapter.clear();
                brewRecyclerGenericAdapter.addAll(brews);
                break;
            case Values.COFFEE:
                coffeeRecyclerGenericAdapter.clear();
                coffeeRecyclerGenericAdapter.addAll(coffees);
                break;
            case Values.BREW_RECIPE:
                brewRecipeRecyclerGenericAdapter.clear();
                brewRecipeRecyclerGenericAdapter.addAll(brewRecipes);
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
            case Values.BREW:
                Uri brewPhotoUri = ImageUtils.getBrewPhotoUri(getContext(), brews.get(position));
                Animations.toolbarAnimation(getActivity(), R.anim.slide_left_out, R.anim.slide_right_in, brewPhotoUri.getPath(), R.raw.photo_pour_over);
                bundle.putInt(Values.SELECTED_BREW, brews.get(position).getId());
                bundle.putBoolean(Values.EDIT_VALUE, true);
                bundle.putString(Values.TAB_TYPE, Values.TAB_BREW_DETAILS);
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
            case Values.COFFEE:
                Uri coffeePhotoUri = ImageUtils.getCoffeePhotoUri(getContext(), coffees.get(position));
                Animations.toolbarAnimation(getActivity(), R.anim.slide_left_out, R.anim.slide_right_in, coffeePhotoUri.getPath(), R.raw.photo_coffee_beans);
                bundle.putString(Values.DETAILS_TYPE, Values.COFFEE);
                bundle.putInt(Values.SELECTED_COFFEE, coffees.get(position).getId());
                FragmentDetails fragmentDetailsCoffee = new FragmentDetails();
                fragmentDetailsCoffee.setArguments(bundle);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                        .replace(R.id.fragment_container_main, fragmentDetailsCoffee, fragmentDetailsCoffee.getTAG())
                        .addToBackStack(fragmentDetailsCoffee.getTag())
                        .commit();
                break;
            case Values.BREW_RECIPE:
                Animations.toolbarAnimation(getActivity(), R.anim.slide_left_out, R.anim.slide_right_in, R.raw.photo_coffee_cup, R.raw.photo_coffee_beans);
                bundle.putString(Values.DETAILS_TYPE, Values.BREW_RECIPE);
                bundle.putInt(Values.SELECTED_BREW_RECIPE, brewRecipes.get(position).getId());
                FragmentDetails fragmentDetailsBrewRecipe = new FragmentDetails();
                fragmentDetailsBrewRecipe.setArguments(bundle);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                        .replace(R.id.fragment_container_main, fragmentDetailsBrewRecipe, fragmentDetailsBrewRecipe.getTAG())
                        .addToBackStack(fragmentDetailsBrewRecipe.getTag())
                        .commit();
                break;
        }
    }

    private void addCoffee()
    {
        Log.i(TAG, "addCoffee: ");
        Animations.toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, R.raw.photo_coffee_beans, R.raw.photo_coffee_beans);
        Bundle bundle = new Bundle();
        bundle.putString(Values.FORM_TYPE, Values.COFFEE);
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

    private void addBrew()
    {
        Log.i(TAG, "addBrew: ");
        Animations.toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, R.raw.photo_pour_over, R.raw.photo_pour_over);
        Bundle bundle = new Bundle();
        bundle.putInt(Values.SELECTED_COFFEE, 1);
        bundle.putBoolean(Values.EDIT_VALUE, false);
        bundle.putString(Values.TAB_TYPE, Values.TAB_BREW_NEW);
        FragmentTabHost fragment = new FragmentTabHost();
        fragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out, R.anim.slide_up_in, R.anim.slide_down_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTAG())
                .commit();
    }

    private void addBrewRecipe()
    {
        Log.i(TAG, "addBrewRecipe: ");
        Animations.toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, R.raw.photo_pour_over, R.raw.photo_pour_over);
        Bundle bundle = new Bundle();
        bundle.putString(Values.FORM_TYPE, Values.BREW_RECIPE);
        bundle.putInt(Values.SELECTED_BREW_RECIPE, 1);
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
//        dataSourceCoffee.open();
//        dataSourceBrew.open();
//        dataSourceBrewRecipe.open();
        updateCoffeeList();
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
//        dataSourceCoffee.close();
//        dataSourceBrew.close();
//        dataSourceBrewRecipe.close();
    }

    @Override
    public String getTabType()
    {
        return tabType;
    }
}
