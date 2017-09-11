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
import com.example.mrl.marketstall.model.Item;
import com.example.mrl.marketstall.utils.Utils;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.view.fragments.FragmentTabHost;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.bumptech.glide.Glide.with;
import static com.example.mrl.marketstall.R.id.fab2;

public class FragmentTabItemDetails extends Fragment implements Callbacks
{
    private final String TAG = getClass().getSimpleName();
    private View view;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabEdit;
    private FloatingActionButton fabDelete;
    private FloatingActionButton fab1;
    private TextView textDate;
    private TextView textPrice;
    private TextView addInfoButton;
    private DatabaseReference mDatabase;
    private DatabaseReference itemCloudEndPoint;
    private Item item;
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
        textDate = (TextView) view.findViewById(R.id.text_item_date);
        textPrice = (TextView) view.findViewById(R.id.text_price);
        addInfoButton = (TextView) view.findViewById(R.id.button_add_info);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        itemCloudEndPoint = mDatabase.child("items");
        itemCloudEndPoint.child(getArguments().getString(Values.SELECTED_ITEM)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    item = noteSnapshot.getValue(Item.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
        tabType = getArguments().getString(Values.TAB_TYPE);
    }

    private void setupToolbar()
    {
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(item.getName());
    }

    private void setupView()
    {
        ImageView icon;
        if (!item.getDateCreated().trim().isEmpty())
        {
            textDate.setText(item.getDateCreated());
            textDate.setVisibility(View.VISIBLE);
            icon = (ImageView) view.findViewById(R.id.icon_date);
            icon.setVisibility(View.VISIBLE);
        }
        if (item.getPrice() != 0.0)
        {
            textPrice.setText(String.valueOf(item.getPrice()));
            textPrice.setVisibility(View.VISIBLE);
            icon = (ImageView) view.findViewById(R.id.icon_price);
            icon.setVisibility(View.VISIBLE);
            with(getContext())
                    .load(R.drawable.button)
                    .into(icon);
        }
        if (String.valueOf(item.getDateCreated()).trim().isEmpty() || item.getPrice() == 0.0)
        {
            addInfoButton.setVisibility(View.VISIBLE);
            addInfoButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    editItem();
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
                editItem();
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
                deleteItem();
            }
        });

        fab1.setVisibility(View.GONE);
        if(!fabMenu.isShown())
        {
            showFabMenu();
        }
    }

    private void editItem()
    {
        Log.i(TAG, "editItem: ");
        Bundle bundle = new Bundle();
        bundle.putString(Values.SELECTED_ITEM, item.getId());
        bundle.putBoolean(Values.EDIT_VALUE, true);
        bundle.putString(Values.TAB_TYPE, Values.TAB_ITEM_EDIT);
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

    private void deleteItem()
    {
        Log.i(TAG, "deleteitem: ");
//        ImageUtils.deleteImage(getActivity(), brew.getImageName());
        itemCloudEndPoint.child(item.getId()).removeValue();
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
        Utils.closeKeyboard(getActivity());
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