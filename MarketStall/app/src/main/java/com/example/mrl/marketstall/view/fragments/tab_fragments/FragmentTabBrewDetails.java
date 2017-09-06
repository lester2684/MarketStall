package com.example.mrl.marketstall.view.fragments.tab_fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.model.Brew;
import com.example.mrl.marketstall.utils.ImageUtils;
import com.example.mrl.marketstall.utils.Utils;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.view.fragments.FragmentTabHost;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import static com.bumptech.glide.Glide.with;
import static com.example.mrl.marketstall.R.id.fab2;

public class FragmentTabBrewDetails extends Fragment implements Callbacks
{
    private final String TAG = getClass().getSimpleName();
    private View view;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabEdit;
    private FloatingActionButton fabDelete;
    private FloatingActionButton fab1;
    private TextView textBrewType;
    private TextView textBrewDate;
    private TextView textTDS;
    private TextView textExtractionYield;
    private TextView textPrice;
    private TextView addInfoButton;
//    private DataSourceBrew dataSourceBrew;
//    private DataSourceCoffee dataSourceCoffee;
    private Brew brew;
    private String tabType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_tab_brew_details, container, false);

        setupValues();
        setupToolbar();
        setupView();
        setupFAB();

        return view;
    }

    private void setupValues()
    {
        fabMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fab_menu);
        fabEdit = (FloatingActionButton) getActivity().findViewById(fab2);
        fabDelete = (FloatingActionButton) getActivity().findViewById(R.id.fab3);
        fab1 = (FloatingActionButton) getActivity().findViewById(R.id.fab1);
        textBrewType = (TextView) view.findViewById(R.id.text_brew_type);
        textBrewDate = (TextView) view.findViewById(R.id.text_brew_date);
        textTDS = (TextView) view.findViewById(R.id.text_TDS);
        textExtractionYield = (TextView) view.findViewById(R.id.text_extraction_yield);
        textPrice = (TextView) view.findViewById(R.id.text_price);
        addInfoButton = (TextView) view.findViewById(R.id.button_add_info);

//        dataSourceCoffee = new DataSourceCoffee(view.getContext());
//        dataSourceBrew = new DataSourceBrew(view.getContext());
//        dataSourceCoffee.open();
//        dataSourceBrew.open();

//        brew = dataSourceBrew.getById(getArguments().getInt(Values.SELECTED_BREW));
        tabType = getArguments().getString(Values.TAB_TYPE);
    }

    private void setupToolbar()
    {
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(brew.getName());
    }

    private void setupView()
    {
        ImageView icon;
        if (!String.valueOf(brew.getBrewType()).trim().isEmpty())
        {
            icon = (ImageView) view.findViewById(R.id.icon_brew_type);
            icon.setVisibility(View.VISIBLE);
            textBrewType.setVisibility(View.VISIBLE);

            with(getContext())
                    .load(Values.BREW_TYPES.get(brew.getBrewType()).getImage())
                    .into(icon);
            textBrewType.setText(getString(Values.BREW_TYPES.get(brew.getBrewType()).getBrewName()));
        }
        if (!brew.getBrewDate().trim().isEmpty())
        {
            textBrewDate.setText(brew.getBrewDate());
            textBrewDate.setVisibility(View.VISIBLE);
            icon = (ImageView) view.findViewById(R.id.icon_brew_date);
            icon.setVisibility(View.VISIBLE);
        }
        if (brew.getTDS() != 0.0)
        {
            textTDS.setText(String.valueOf(brew.getTDS()));
            textTDS.setVisibility(View.VISIBLE);
            icon = (ImageView) view.findViewById(R.id.icon_TDS);
            icon.setVisibility(View.VISIBLE);
            with(getContext())
                    .load(R.drawable.button)
                    .into(icon);
        }
        if (brew.getExtractionYield() != 0.0)
        {
            textExtractionYield.setText(String.valueOf(brew.getExtractionYield()));
            textExtractionYield.setVisibility(View.VISIBLE);
            icon = (ImageView) view.findViewById(R.id.icon_extraction_yield);
            icon.setVisibility(View.VISIBLE);
            with(getContext())
                    .load(R.drawable.button)
                    .into(icon);
        }
        if (brew.getPrice() != 0.0)
        {
            textPrice.setText(String.valueOf(brew.getPrice()));
            textPrice.setVisibility(View.VISIBLE);
            icon = (ImageView) view.findViewById(R.id.icon_price);
            icon.setVisibility(View.VISIBLE);
            with(getContext())
                    .load(R.raw.icon_price)
                    .into(icon);
        }
        if (String.valueOf(brew.getBrewType()).trim().isEmpty() || brew.getBrewDate().trim().isEmpty() || brew.getTDS() == 0.0 || brew.getExtractionYield() == 0.0 || brew.getPrice() == 0.0)
        {
            addInfoButton.setVisibility(View.VISIBLE);
            addInfoButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    editBrew();
                }
            });
        }
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

    private void setFAB()
    {
        fabEdit.setVisibility(View.INVISIBLE);
        fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
        fabEdit.setLabelText(getString(R.string.button_edit));
        fabEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                edit();
            }
        });

        fabDelete.setVisibility(View.INVISIBLE);
        fabDelete.setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
        fabDelete.setLabelText(getString(R.string.button_delete));
        fabDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                delete();
            }
        });

        fab1.setVisibility(View.GONE);
        if(!fabMenu.isShown())
        {
            showFabMenu();
        }
    }

    private void edit()
    {
        Log.i(TAG, "editCoffee: ");
        Bundle bundle = new Bundle();
        bundle.putInt(Values.SELECTED_BREW, brew.getId());
        bundle.putBoolean(Values.EDIT_VALUE, true);
        bundle.putString(Values.TAB_TYPE, Values.TAB_BREW_EDIT);
        FragmentTabHost fragment = new FragmentTabHost();
        fragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out, R.anim.slide_up_in, R.anim.slide_down_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTag())
                .commit();
    }

    private void delete()
    {
        Log.i(TAG, "deleteCoffee: ");
        ImageUtils.deleteImage(getActivity(), brew.getImageName());
//        dataSourceBrew.delete(brew.getId());
        backPress();
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

    private void editBrew()
    {
        Log.i(TAG, "editCoffee: ");
        Bundle bundle = new Bundle();
        bundle.putInt(Values.SELECTED_BREW, brew.getId());
        bundle.putBoolean(Values.EDIT_VALUE, true);
        bundle.putString(Values.TAB_TYPE, Values.TAB_BREW_EDIT);
        FragmentTabHost fragment = new FragmentTabHost();
        fragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out, R.anim.slide_up_in, R.anim.slide_down_out)
                .replace(R.id.fragment_container_main, fragment, fragment.getTAG())
                .addToBackStack(fragment.getTag())
                .commit();
    }

    @Override
    public void onResume()
    {
        Log.i(TAG, "onResume: ");
        super.onResume();
//        dataSourceCoffee.open();
//        dataSourceBrew.open();
    }

    @Override
    public void onPause()
    {
        Log.i(TAG, "onPause: ");
        Utils.closeKeyboard(getActivity());
//        dataSourceCoffee.close();
//        dataSourceBrew.close();
        super.onPause();
    }
    
    public String getTAG()
    {

        return TAG;
    }

    @Override
    public void toolbarExpanded()
    {

    }

    @Override
    public void toolbarCollapsed()
    {

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

    @Override
    public String getTabType() {
        return tabType;
    }

    @Override
    public void onReturn(Fragment fragment, String tabType)
    {
        Log.i(TAG, "onReturn: ");

    }
}