package com.example.mrl.marketstall.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.example.mrl.marketstall.model.FormInfo;
import com.example.mrl.marketstall.model.Item;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.viewholder.RecyclerViewHolderDetails;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentDetails extends Fragment implements Callbacks
{
    private final String TAG = getClass().getSimpleName();
    private View view;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabEdit;
    private FloatingActionButton fabDelete;
    private TextView addInfoButton;
    private RecyclerView recyclerViewDetails;
    private RecyclerGenericAdapter<FormInfo> detailsInfoRecyclerAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference itemCloudEndPoint;
    private Item item;
    private boolean showMenu = false;
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

        return view;
    }

    private void setupValues()
    {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        fabMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fab_menu);
        fabEdit = (FloatingActionButton) getActivity().findViewById(R.id.fab2);
        fabDelete = (FloatingActionButton) getActivity().findViewById(R.id.fab3);
        addInfoButton = (TextView) view.findViewById(R.id.button_add_info);
        recyclerViewDetails = (RecyclerView) view.findViewById(R.id.recyclerViewDetails);

        detailsType = getArguments().getString(Values.DETAILS_TYPE);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        itemCloudEndPoint = mDatabase.child("items");
        itemCloudEndPoint.child(getArguments().getString(Values.SELECTED_ITEM)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    item = dataSnapshot.getValue(Item.class);
                    updateListDetails();
                    collapsingToolbarLayout.setTitle(item.getName());
                    setupView();
                    setupRecyclerViewDetails();
                }
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
            case Values.ITEM:
                total_form_size = item.getTOTAL_FORM_SIZE();
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
        detailsInfoRecyclerAdapter = new RecyclerGenericAdapter<FormInfo>(getActivity() , detailsList)
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

    private void setFAB()
    {
        fabEdit.setVisibility(View.INVISIBLE);
        fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit, getActivity().getTheme()));
        fabEdit.setLabelText(getString(R.string.button_edit));
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                edit();
            }
        });

        fabDelete.setVisibility(View.INVISIBLE);
        fabDelete.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete, getActivity().getTheme()));
        fabDelete.setLabelText(getString(R.string.button_delete));
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                delete();
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

    private void updateListDetails()
    {
        List<FormInfo> formInfos = new ArrayList<>();
        detailsList = new ArrayList<>();
        switch (detailsType)
        {
            case Values.ITEM:
                formInfos = item.getDetails();
                formInfos = formInfos.subList(1, formInfos.size());
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

    private void delete()
    {
        Log.i(TAG, "delete: ");
        switch (detailsType)
        {
            case Values.ITEM:
//                ImageUtils.deleteImage(getActivity(), coffee.getImageName());
                itemCloudEndPoint.child(item.getId()).setValue(null);
        }
        backPress();
    }

    private void edit()
    {
        Log.i(TAG, "edit: ");
        Bundle bundle = new Bundle();
        switch (detailsType)
        {
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
                case Values.ITEM:
//                    Uri uri = ImageUtils.getCoffeePhotoUri(getContext(), item);
//                    Animations.toolbarAnimation(getActivity(), R.anim.slide_right_out, R.anim.slide_left_in, uri.getPath(), R.raw.photo_coffee_beans);
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
