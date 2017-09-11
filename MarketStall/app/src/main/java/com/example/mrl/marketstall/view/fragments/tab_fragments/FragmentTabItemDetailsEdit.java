package com.example.mrl.marketstall.view.fragments.tab_fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.interfaces.CallbacksTabEdit;
import com.example.mrl.marketstall.model.Item;
import com.example.mrl.marketstall.utils.Utils;
import com.example.mrl.marketstall.value.Values;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class FragmentTabItemDetailsEdit extends Fragment implements Callbacks, CallbacksTabEdit
{
    private final String TAG = getClass().getSimpleName();
    private static int LOAD_IMAGE_RESULTS = 1;
    private View view;
    private ImageView toolbarImageViewMain;
    private TextView toolbarTextView;
    private FloatingActionMenu fabMenu;
    private EditText editTextName;
    private EditText editTextDate;
    private EditText editTextPrice;
    private DatabaseReference mDatabase;
    private DatabaseReference itemCloudEndPoint;
    private Item item;
    private Boolean editValue = false;
    private Calendar calendar;
    private String tabType;
    private Uri imageURI;
    private boolean imageSelected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_tab_brew_details_edit, container, false);

        setupValues();
        setupToolbar();
        setupView();
        setupFAB();

        return view;
    }

    private void setupValues()
    {
        toolbarImageViewMain = (ImageView) getActivity().findViewById(R.id.toolbar_image_main);
        fabMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fab_menu);
        editTextName = (EditText) view.findViewById(R.id.text_name);
        editTextPrice = (EditText) view.findViewById(R.id.text_price);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        itemCloudEndPoint = mDatabase.child("items");

        if(editValue)
        {
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
        }
        else
        {
            item = new Item();
            String key = itemCloudEndPoint.push().getKey();
            item.setId(key);
            itemCloudEndPoint.child(key).setValue(item);
        }

        calendar = Calendar.getInstance();
        editValue = getArguments().getBoolean(Values.EDIT_VALUE);
        tabType = getArguments().getString(Values.TAB_TYPE);


    }

    private void setupToolbar()
    {
        toolbarTextView = (TextView) getActivity().findViewById(R.id.toolbar_text_view);
        toolbarTextView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                openGallery();
            }
        });
        toolbarTextView.setText(R.string.change_image);
        toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in));
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(" ");
    }

    private void setupView()
    {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
        editTextDate.setText(date);

        editTextName.setText(item.getName());
        ImageView icon = (ImageView) view.findViewById(R.id.icon_name);
        Glide
                .with(getContext())
                .load(R.raw.icon_name)
                .into(icon);
        icon = (ImageView) view.findViewById(R.id.icon_name);
        Glide
                .with(getContext())
                .load(R.raw.icon_coffee_bean)
                .into(icon);
        editTextPrice.setText(String.valueOf(item.getPrice()));
        icon = (ImageView) view.findViewById(R.id.icon_price);
        Glide
                .with(getContext())
                .load(R.raw.icon_price)
                .into(icon);
    }

    private void setupFAB()
    {
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
        fabMenu.setOnMenuButtonClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                save();
            }
        });
        if(!fabMenu.isShown())
        {
            showFabMenu();
        }
    }

    @Override
    public void save()
    {
        Log.i(TAG, "save: ");
        if (validateName())
        {
//                if (imageSelected)
//                {
//                    String imageName = ImageUtils.saveImage(getActivity(), imageURI, item.getId(), item.getImageName());
//                    item.setImageName(imageName);
//                }
            item.setName(editTextName.getText().toString());
            item.setDateCreated(editTextDate.getText().toString());
            if (!editTextPrice.getText().toString().trim().isEmpty())
            {
                item.setPrice(Double.parseDouble(editTextPrice.getText().toString()));
            }
            else
            {
                item.setPrice(0.0);
            }
            itemCloudEndPoint.child(item.getId()).setValue(item);
            backPress();
        }
    }

    private boolean validateName()
    {
        TextInputLayout textInputLayoutName = (TextInputLayout) view.findViewById(R.id.text_input_layout_name);
        EditText editTextName = textInputLayoutName.getEditText();
        assert editTextName != null;
        if (editTextName.getText().toString().trim().isEmpty())
        {
            textInputLayoutName.setError(getString(R.string.text_error_name_item));
            editTextName.requestFocus();
            return false;
        }
        else
        {
            textInputLayoutName.setErrorEnabled(false);
        }
        return true;
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
            Glide
                    .with(getContext())
                    .load(imageURI)
                    .error(R.raw.photo_pour_over)
                    .into(toolbarImageViewMain);
            imageSelected = true;
        }
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
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
    public void delete()
    {
        Log.i(TAG, "deleteCoffee: ");
//        ImageUtils.deleteImage(getActivity(), brew.getImageName());
        itemCloudEndPoint.child(item.getId()).removeValue();
    }

    @Override
    public void setItem(Item item) {
        Log.i(TAG, "setItem: ");
        this.item = item;
    }

    @Override
    public Item getItem() {
        item.setName(editTextName.getText().toString());
        return item;
    }

    @Override
    public void onResume()
    {
        Log.i(TAG, "onResume: ");
        super.onResume();
        toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in));
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
    }

    @Override
    public void onPause()
    {
        Log.i(TAG, "onPause: ");
        toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_out));
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.fab_add, getActivity().getTheme()));
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