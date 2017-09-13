package com.example.mrl.marketstall.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.adapter.RecyclerGenericAdapter;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.interfaces.CallbacksTabEdit;
import com.example.mrl.marketstall.model.FormInfo;
import com.example.mrl.marketstall.model.Item;
import com.example.mrl.marketstall.utils.GPSTracker;
import com.example.mrl.marketstall.utils.Utils;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.viewholder.RecyclerViewHolderForm;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.bumptech.glide.Glide.with;

public class FragmentForm extends Fragment implements Callbacks, CallbacksTabEdit {
    private final String TAG = getClass().getSimpleName();
    private static int LOAD_IMAGE_RESULTS = 1;
    private View view;
    private Toolbar toolbar;
    private ImageView toolbarImageViewMain;
    private TextView toolbarTextView;
    private FloatingActionMenu fabMenu;
    private RecyclerView recyclerViewForm;
    private DatabaseReference mDatabase;
    private DatabaseReference itemCloudEndPoint;
    private double latitude;
    private double longitude;
    private GPSTracker gps;
    private Item item;
    private String tabType;
    private Boolean editValue = false;
    private Uri imageURI;
    private boolean imageBoolean = false;
    private boolean showMenu = false;
    private List<FormInfo> formList;
    private String formType;
    private boolean imageSelected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_form, container, false);

        setupValues();
        setupToolbar();
        setupFAB();

        return view;
    }

    private void setupValues() {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarImageViewMain = (ImageView) getActivity().findViewById(R.id.toolbar_image_main);
        toolbarTextView = (TextView) getActivity().findViewById(R.id.toolbar_text_view);
        fabMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fab_menu);

        recyclerViewForm = (RecyclerView) view.findViewById(R.id.recyclerViewForm);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        itemCloudEndPoint = mDatabase.child("items");

        gps = new GPSTracker(getActivity());

        formType = getArguments().getString(Values.FORM_TYPE);
        editValue = getArguments().getBoolean(Values.EDIT_VALUE);
        formList = new ArrayList<>();

        switch (formType) {
            case Values.ITEM:
                if (editValue) {
                    itemCloudEndPoint.child(getArguments().getString(Values.SELECTED_ITEM)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                item = dataSnapshot.getValue(Item.class);
                                for (FormInfo detail : item.getDetails()) {
                                    if (detail.getShow()) {
                                        Log.d(TAG, "onDataChangeXXXXXXXXXXXXXXXXXXXXX: " + detail.getHint());
                                        formList.add(detail);
                                    }
                                }
                                setupRecyclerViewForm();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, databaseError.getMessage());
                        }
                    });
                } else {
                    item = new Item();
                    item.setId(itemCloudEndPoint.push().getKey());
                    for (FormInfo detail : item.getDetails()) {
                        if (detail.getShow()) {
                            formList.add(detail);
                        }
                    }
                    setupRecyclerViewForm();
                }
                break;
        }
    }

    private void setupToolbar() {
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar_layout);
        appBarLayout.setExpanded(true);
        toolbar.getMenu().clear();
        switch (formType) {
            case Values.ITEM:
                toolbarTextView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        openGallery();
                    }
                });
                toolbarTextView.setText(R.string.change_image);
                toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in));

                break;
        }
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(" ");
    }

    private void setupFAB() {
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_save, getActivity().getTheme()));
        fabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        if (!fabMenu.isShown()) {
            showFabMenu();
        }
    }

    private void setupRecyclerViewForm() {
        RecyclerGenericAdapter<FormInfo> formInfoRecyclerAdapter = null;
        switch (formType) {
            case Values.ITEM:
                formInfoRecyclerAdapter = new RecyclerGenericAdapter<FormInfo>(getActivity(), formList) {
                    @Override
                    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked) {
                        final View holder;
                        holder = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_row_form, parent, false);
                        return new RecyclerViewHolderForm(holder);
                    }

                    @Override
                    public void onBindData(Context context, RecyclerView.ViewHolder holder, FormInfo item, final int recyclerPosition) {
                        final RecyclerViewHolderForm viewHolder;
                        viewHolder = (RecyclerViewHolderForm) holder;
                        viewHolder.editText.setText(item.getText());
                        viewHolder.textInputLayout.setHint(getString(item.getHint()));
                        if (item.getImage() != 0) {
                            with(getContext())
                                    .load(item.getImage())
                                    .into(viewHolder.icon);
                        }
                        viewHolder.editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                formList.get(recyclerPosition).setText(viewHolder.editText.getText().toString());
                            }
                        });
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
                break;
        }
        recyclerViewForm.setAdapter(formInfoRecyclerAdapter);
        recyclerViewForm.setHasFixedSize(true);
        recyclerViewForm.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    private boolean validateName()
    {
        View viewItem = recyclerViewForm.getLayoutManager().findViewByPosition(0);
        EditText editText = (EditText) viewItem.findViewById(R.id.edit_text);
        TextInputLayout textInputLayout = (TextInputLayout) viewItem.findViewById(R.id.text_input_layout);
        if (editText.getText().toString().trim().isEmpty())
        {
            switch (formType)
            {
                case Values.ITEM:
                    textInputLayout.setError(getString(R.string.text_error_name_item));
                    break;
            }
            requestFocus(editText);
            return false;
        }
        else
        {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view)
    {
        if (view.requestFocus())
        {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void openGallery()
    {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, LOAD_IMAGE_RESULTS);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == Activity.RESULT_OK && data != null)
        {
            imageURI = data.getData();
            with(getContext())
                    .load(imageURI)
                    .error(R.raw.photo_coffee_beans)
                    .into(toolbarImageViewMain);
            imageBoolean = true;
        }
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_save, getActivity().getTheme()));
    }

    public String getFormType() {
        return formType;
    }

    @Override
    public void save()
    {
        Log.i(TAG, "save: ");
        switch (formType)
        {
            case Values.ITEM:
                if (!validateName())
                {
                    return;
                }
                item.setByList(formList);
//              if (imageBoolean)
//              {
//                   String imageName = ImageUtils.saveImage(getActivity(), imageURI, item.getType(), item.getId(), item.getImageName());
//                   item.setImageName(imageName);
//               }
                SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.time_format));
                item.setDateCreated(dateFormat.format(Calendar.getInstance().getTime()));
                if(gps.canGetLocation()){
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    Log.d(TAG, "save: " + latitude +" " + longitude);
                }else{
                    gps.showSettingsAlert();
                }
                item.setLocation(longitude+","+latitude);
                itemCloudEndPoint.child(item.getId()).setValue(item);
                backPress();
                break;
        }
    }

    @Override
    public void delete()
    {
        Log.i(TAG, "deleteCoffee: ");
//        ImageUtils.deleteImage(getActivity(), brew.getImageName());
        itemCloudEndPoint.child(item.getId()).removeValue();
    }

    @Override
    public void setItem(Item item) {
        Log.i(TAG, "setBrew: ");
        this.item = item;
    }

    @Override
    public Item getItem() {
        item.setByList(formList);
        return item;
    }

    @Override
    public void backPress()
    {
        Utils.closeKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    @Override
    public String getTabType() {
        return tabType;
    }

    @Override
    public void onResume()
    {
        Log.i(TAG, "onResume: ");
        super.onResume();
        switch (formType)
        {
            case Values.ITEM:
                toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in));
                break;
        }
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_save, getActivity().getTheme()));
    }

    @Override
    public void onPause()
    {
        Log.i(TAG, "onPause: ");
        super.onPause();
        switch (formType)
        {
            case Values.ITEM:
                toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_out));
                break;
        }
        Utils.closeKeyboard(getActivity());
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.fab_add, getActivity().getTheme()));
    }

    @Override
    public String getTAG()
    {

        return TAG;
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
