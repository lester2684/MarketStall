package com.example.mrl.marketstall.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.example.mrl.marketstall.model.FormInfo;
import com.example.mrl.marketstall.ui.Animations;
import com.example.mrl.marketstall.ui.ItemOffsetDecoration;
import com.example.mrl.marketstall.utils.ImageUtils;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.viewholder.CardViewHolder;
import com.example.mrl.marketstall.viewholder.RecyclerViewHolderDetails;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

public class FragmentDetails extends Fragment implements Callbacks
{
    private final String TAG = getClass().getSimpleName();
    private View view;
    private Toolbar toolbar;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabEdit;
    private FloatingActionButton fabDelete;
    private FloatingActionButton fabBrew;
    private TextView addInfoButton;
    private RecyclerView recyclerViewBrews;
    private RecyclerView recyclerViewDetails;
    private RecyclerGenericAdapter<FormInfo> detailsInfoRecyclerAdapter;
    private RecyclerGenericAdapter<Brew> brewRecyclerAdapter;
//    private DataSourceCoffee dataSourceCoffee;
//    private DataSourceBrewRecipe dataSourceBrewRecipe;
//    private DataSourceBrew dataSourceBrew;
    private Coffee coffee;
    private BrewRecipe brewRecipe;
    private boolean showMenu = false;
    private ArrayList<Brew> brews;
    private List<FormInfo> detailsList;
    private String detailsType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_details, container, false);

        setupValues();
        setupToolbar();
        setupFAB();
        setupView();
        setupRecyclerViewDetails();
        switch (detailsType)
        {
            case Values.BREW:
            case Values.COFFEE:
                setupRecyclerViewBrews();
                break;
            case Values.BREW_RECIPE:
                recyclerViewBrews.setVisibility(View.GONE);
                break;
        }

        return view;
    }

    private void setupValues()
    {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        fabMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fab_menu);
        fabEdit = (FloatingActionButton) getActivity().findViewById(R.id.fab2);
        fabDelete = (FloatingActionButton) getActivity().findViewById(R.id.fab3);
        fabBrew = (FloatingActionButton) getActivity().findViewById(R.id.fab1);
        addInfoButton = (TextView) view.findViewById(R.id.button_add_info);
        recyclerViewBrews = (RecyclerView) view.findViewById(R.id.recycler_view_brews);
        recyclerViewDetails = (RecyclerView) view.findViewById(R.id.recyclerViewDetails);

        detailsType = getArguments().getString(Values.DETAILS_TYPE);

//        dataSourceCoffee = new DataSourceCoffee(view.getContext());
//        dataSourceBrewRecipe = new DataSourceBrewRecipe(view.getContext());
//        dataSourceBrew = new DataSourceBrew(view.getContext());
//        dataSourceCoffee.open();
//        dataSourceBrewRecipe.open();
//        dataSourceBrew.open();

        detailsList = new ArrayList<>();

        switch (detailsType)
        {
            case Values.COFFEE:
//                coffee = dataSourceCoffee.getById(getArguments().getInt(Values.SELECTED_COFFEE));
//                brews = dataSourceBrew.getByCoffeeID(coffee.getId());
                updateList();
                break;
            case Values.BREW_RECIPE:
//                brewRecipe = dataSourceBrewRecipe.getById(getArguments().getInt(Values.SELECTED_BREW_RECIPE));
                updateList();
                break;

        }
    }

    private void setupToolbar()
    {
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar_layout);
        appBarLayout.setExpanded(true);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);

        switch (detailsType)
        {
            case Values.COFFEE:
                collapsingToolbarLayout.setTitle(coffee.getName());
                break;
            case Values.BREW_RECIPE:
                collapsingToolbarLayout.setTitle(brewRecipe.getId()+"");
                break;
        }
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
            fabMenu.close(true);
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

    private void setupView()
    {
        int total_form_size=0;
        switch (detailsType)
        {
            case Values.COFFEE:
                total_form_size = coffee.getTOTAL_FORM_SIZE() - 1;
                break;
            case Values.BREW_RECIPE:
                total_form_size = brewRecipe.getTOTAL_FORM_SIZE() - 1;
                break;
        }
        if (detailsList.size() < total_form_size )
        {
            addInfoButton.setVisibility(View.VISIBLE);
            addInfoButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    edit();
                }
            });
        }
    }

    private void setupRecyclerViewDetails()
    {
        List<FormInfo> list = detailsList.subList(1, detailsList.size());
        detailsInfoRecyclerAdapter = new RecyclerGenericAdapter<FormInfo>(getActivity() , list)
        {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked)
            {
                final View view = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_row_details, parent, false);
                return new RecyclerViewHolderDetails(view);
            }

            @Override
            public void onBindData(Context context, RecyclerView.ViewHolder holder, FormInfo item, int position)
            {
                RecyclerViewHolderDetails viewHolder = (RecyclerViewHolderDetails) holder;
                viewHolder.text.setText(item.getText());
                if (item.getImage() != 0)
                {
                    Glide
                            .with(getContext())
                            .load(item.getImage())
                            .into(viewHolder.icon);
                }
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
        recyclerViewDetails.setAdapter(detailsInfoRecyclerAdapter);
        recyclerViewDetails.setHasFixedSize(true);
        recyclerViewDetails.setLayoutManager(new GridLayoutManager(getActivity(),2));
    }

    private void setupRecyclerViewBrews()
    {
        recyclerViewBrews.setVisibility(View.VISIBLE);
        if (brews.size() > 0)
        {
            TextView textViewBrew = (TextView) view.findViewById(R.id.text_brew);
            textViewBrew.setVisibility(View.VISIBLE);
            recyclerViewBrews.setVisibility(View.VISIBLE);
            brewRecyclerAdapter = new RecyclerGenericAdapter<Brew>(getActivity() , brews)
            {
                @Override
                public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked)
                {
                    final View view = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_card, parent, false);
                    return new CardViewHolder(view, onRecyclerItemClicked);
                }

                @Override
                public void onBindData(Context context, RecyclerView.ViewHolder holder, Brew item, int position)
                {
                    CardViewHolder cardViewHolder = (CardViewHolder) holder;
                    cardViewHolder.title1.setText(item.getName());
                    Glide
                            .with(context)
                            .load(ImageUtils.getBrewPhotoUri(context,item))
                            .error(R.raw.photo_coffee_cherries)
                            .crossFade()
                            .into(cardViewHolder.circularImageView);
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
            recyclerViewBrews.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewBrews.setAdapter(brewRecyclerAdapter);
            recyclerViewBrews.setHasFixedSize(true);
            int margin = (int) getResources().getDimension(R.dimen.recycler_margin_5);
            RecyclerView.ItemDecoration itemDecoration = new ItemOffsetDecoration(margin, margin, margin, margin);
            recyclerViewBrews.addItemDecoration(itemDecoration);
            recyclerViewBrews.addItemDecoration(itemDecoration);
        }
    }

    private void setFAB()
    {
        fabEdit.setVisibility(View.INVISIBLE);
        fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
        fabEdit.setLabelText(getString(R.string.button_edit));
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                edit();
            }
        });

        fabDelete.setVisibility(View.INVISIBLE);
        fabDelete.setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
        fabDelete.setLabelText(getString(R.string.button_delete));
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                deleteCoffee();
            }
        });

        fabBrew.setVisibility(View.INVISIBLE);
        fabBrew.setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
        fabBrew.setLabelText(getString(R.string.button_brew));
        fabBrew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                addBrew();
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
        detailsList = new ArrayList<>();
        List<FormInfo> formInfos = new ArrayList<>();
        switch (detailsType)
        {
            case Values.COFFEE:
                formInfos = coffee.getDetails();
                break;
            case Values.BREW_RECIPE:
                formInfos = brewRecipe.getDetails();
                break;
        }
        for (int i = 0; i < formInfos.size(); i++)
        {
            if (!formInfos.get(i).getText().trim().isEmpty() && !formInfos.get(i).getText().equals("0.0"))
            {
                detailsList.add(formInfos.get(i));
            }
        }
    }

    private void updateListAdapters()
    {
        switch (detailsType)
        {
            case Values.COFFEE:
//                brews = dataSourceBrew.getByCoffeeID(coffee.getId());
                if (brews.size() > 0)
                {
                    brewRecyclerAdapter.clear();
                    brewRecyclerAdapter.addAll(brews);
                }
                break;
            case Values.BREW_RECIPE:
                break;
        }
        updateList();
        if (detailsList.size() > 0)
        {
            detailsInfoRecyclerAdapter.clear();
            List<FormInfo> list = detailsList.subList(1, detailsList.size());
            detailsInfoRecyclerAdapter.addAll(list);
        }
    }

    private void toSelected(int position)
    {
        Bundle bundle = new Bundle();
        switch (detailsType)
        {
            case Values.COFFEE:
                Uri uri = ImageUtils.getBrewPhotoUri(getContext(), brews.get(position));
                Animations.toolbarAnimation(getActivity(), R.anim.slide_left_out, R.anim.slide_right_in, uri.getPath(), R.raw.photo_coffee_cherries);
                bundle.putInt(Values.SELECTED_BREW, brews.get(position).getId());
                bundle.putBoolean(Values.EDIT_VALUE, true);
                bundle.putString(Values.TAB_TYPE, Values.TAB_BREW_DETAILS);

                break;
            case Values.BREW_RECIPE:
                bundle.putInt(Values.SELECTED_BREW, brews.get(position).getId());
                bundle.putBoolean(Values.EDIT_VALUE, true);
                bundle.putString(Values.TAB_TYPE, Values.TAB_BREW_DETAILS);
                break;
        }
        FragmentTabHost fragment = new FragmentTabHost();
        fragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTag())
                .commit();
    }

    private void deleteCoffee()
    {
        Log.i(TAG, "deleteCoffee: ");

        switch (detailsType)
        {
            case Values.COFFEE:
                ImageUtils.deleteImage(getActivity(), coffee.getImageName());
//                dataSourceCoffee.delete(getActivity(), coffee);
                break;
            case Values.BREW_RECIPE:
//                dataSourceBrewRecipe.delete(brewRecipe);
                break;
        }
        backPress();
    }

    private void edit()
    {
        Log.i(TAG, "edit: ");
        Bundle bundle = new Bundle();
        switch (detailsType)
        {
            case Values.COFFEE:
                bundle.putString(Values.FORM_TYPE, Values.COFFEE);
                bundle.putInt(Values.SELECTED_COFFEE, coffee.getId());
                bundle.putBoolean(Values.EDIT_VALUE, true);
                FragmentForm fragmentCoffeeForm = new FragmentForm();
                fragmentCoffeeForm.setArguments(bundle);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out, R.anim.slide_up_in, R.anim.slide_down_out)
                        .replace(R.id.fragment_container_main, fragmentCoffeeForm, fragmentCoffeeForm.getTAG())
                        .addToBackStack(fragmentCoffeeForm.getTag())
                        .commit();
                break;
            case Values.BREW_RECIPE:
                bundle.putString(Values.FORM_TYPE, Values.BREW_RECIPE);
                bundle.putInt(Values.SELECTED_BREW_RECIPE, brewRecipe.getId());
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

    private void addBrew()
    {
        Animations.toolbarAnimation(getActivity(), R.anim.slide_left_out, R.anim.slide_right_in, R.raw.photo_pour_over, R.raw.photo_pour_over);
        Bundle bundle = new Bundle();
        bundle.putInt(Values.SELECTED_COFFEE, coffee.getId());
        bundle.putInt(Values.SELECTED_BREW, -1);
        bundle.putBoolean(Values.EDIT_VALUE, false);
        bundle.putString(Values.TAB_TYPE, Values.TAB_BREW_NEW);
        FragmentTabHost fragment = new FragmentTabHost();
        fragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTag())
                .commit();
    }

    public String getDetailsType() {
        return detailsType;
    }

    @Override
    public String getTAG()
    {

        return TAG;
    }

    @Override
    public void backPress()
    {
        getActivity().onBackPressed();

    }

    @Override
    public String getTabType() {
        return null;
    }

    @Override
    public void onResume()
    {
        Log.i(TAG, "onResume: ");
        super.onResume();
//        dataSourceCoffee.open();
//        dataSourceBrew.open();
//        dataSourceBrewRecipe.open();
        updateListAdapters();
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
//        dataSourceBrewRecipe.close();
//        dataSourceBrew.close();
    }

    @Override
    public void onBackPressedCallback()
    {
        Log.i(TAG, "backButtonWasPressed: ");

    }

    @Override
    public void onReturn(Fragment fromFragment, String fromTabType)
    {
        Log.i(TAG, "onReturn: ");
        if (fromFragment instanceof FragmentTabHost)
        {
            switch (detailsType)
            {
                case Values.COFFEE:
                    Uri uri = ImageUtils.getCoffeePhotoUri(getContext(), coffee);
                    Animations.toolbarAnimation(getActivity(), R.anim.slide_right_out, R.anim.slide_left_in, uri.getPath(), R.raw.photo_coffee_beans);
                    break;
                case Values.BREW_RECIPE:
                    Animations.toolbarAnimation(getActivity(), R.anim.slide_right_out, R.anim.slide_left_in,  R.raw.photo_coffee_beans, R.raw.photo_coffee_beans);
                    break;
            }

        }
    }

    @Override
    public void toolbarExpanded()
    {
        fabMenu.showMenuButton(true);
        if (showMenu)
        {
            showMenu = false;
        }
    }

    @Override
    public void toolbarCollapsed()
    {
        fabMenu.hideMenuButton(true);
        if (!showMenu)
        {
            showMenu = true;
        }
    }
}
