package com.example.mrl.marketstall.view.fragments.tab_fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.interfaces.CallbacksTabEdit;
import com.example.mrl.marketstall.model.Brew;
import com.example.mrl.marketstall.model.BrewTypeInfo;
import com.example.mrl.marketstall.model.Coffee;
import com.example.mrl.marketstall.ui.Animations;
import com.example.mrl.marketstall.utils.ImageUtils;
import com.example.mrl.marketstall.utils.Utils;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.view.fragments.FragmentForm;
import com.example.mrl.marketstall.viewholder.SpinnerViewHolder;
import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.adapter.SpinnerGenericAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentTabBrewDetailsEdit extends Fragment implements Callbacks, CallbacksTabEdit
{
    private final String TAG = getClass().getSimpleName();
    private static int LOAD_IMAGE_RESULTS = 1;
    private View view;
    private ImageView toolbarImageViewMain;
    private TextView toolbarTextView;
    private FloatingActionMenu fabMenu;
    private EditText editTextName;
    private EditText editTextBrewDate;
    private EditText editTextPrice;
    private EditText editTextTDS;
    private Spinner spinnerCoffee;
    private TextView textViewAddCoffee;
    private ImageView imageButtonAddCoffee;
    private Spinner spinnerBrewType;
    private DatePickerDialog datePickerDialog;
//    private DataSourceBrew dataSourceBrew;
//    private DataSourceCoffee dataSourceCoffee;
    private Brew brew;
    private Boolean editValue = false;
    private Calendar calendar;
    private ArrayList<Coffee> coffees;
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
        setupSpinner();
        setupDatePicker();

        return view;
    }

    private void setupValues()
    {
        toolbarImageViewMain = (ImageView) getActivity().findViewById(R.id.toolbar_image_main);
        fabMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fab_menu);
        editTextName = (EditText) view.findViewById(R.id.text_name);
        editTextBrewDate = (EditText) view.findViewById(R.id.text_brew_date);
        editTextPrice = (EditText) view.findViewById(R.id.text_price);
        editTextTDS = (EditText) view.findViewById(R.id.text_TDS);
        spinnerCoffee = (Spinner) view.findViewById(R.id.spinner_coffee);
        textViewAddCoffee = (TextView) view.findViewById(R.id.text_view_add_coffee);
        imageButtonAddCoffee = (ImageView) view.findViewById(R.id.button_add_coffee);
        spinnerBrewType = (Spinner) view.findViewById(R.id.spinner_brew_type);

//        dataSourceCoffee = new DataSourceCoffee(view.getContext());
//        dataSourceBrew = new DataSourceBrew(view.getContext());
//        dataSourceCoffee.open();
//        dataSourceBrew.open();

        calendar = Calendar.getInstance();
//        coffees = dataSourceCoffee.getAll();
        editValue = getArguments().getBoolean(Values.EDIT_VALUE);
        tabType = getArguments().getString(Values.TAB_TYPE);

        if(editValue)
        {
//            brew = dataSourceBrew.getById(getArguments().getInt(Values.SELECTED_BREW));
        }
        else
        {
            brew = new Brew();
            brew.setCoffeeId(getArguments().getInt(Values.SELECTED_COFFEE));
        }
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
        if(editValue)
        {
            editTextBrewDate.setText(brew.getBrewDate());
        }
        else
        {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            editTextBrewDate.setText(date);
        }
        editTextName.setText(brew.getName());
        ImageView icon = (ImageView) view.findViewById(R.id.icon_name);
        Glide
                .with(getContext())
                .load(R.raw.icon_name)
                .into(icon);
        icon = (ImageView) view.findViewById(R.id.icon_coffee);
        Glide
                .with(getContext())
                .load(R.raw.icon_coffee_bean)
                .into(icon);
        editTextBrewDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                datePickerDialog.show();
            }
        });
        editTextTDS.setText(String.valueOf(brew.getTDS()));
        icon = (ImageView) view.findViewById(R.id.icon_TDS);
        Glide
                .with(getContext())
                .load(R.drawable.button)
                .into(icon);
        editTextPrice.setText(String.valueOf(brew.getPrice()));
        icon = (ImageView) view.findViewById(R.id.icon_price);
        Glide
                .with(getContext())
                .load(R.raw.icon_price)
                .into(icon);
        updateCoffeeList();
        imageButtonAddCoffee.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addCoffee();
            }
        });
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
//        ArrayList<Coffee> coffees = dataSourceCoffee.getAll();
        if (validateName())
        {
            if (coffees.size() == 0)
            {
                Toast.makeText(getActivity(), getString(R.string.no_coffee_error), Toast.LENGTH_LONG).show();
            }
            else
            {
                if (imageSelected)
                {
                    String imageName = ImageUtils.saveImage(getActivity(), imageURI, brew.getType(), brew.getId(), brew.getImageName());
                    brew.setImageName(imageName);
                }
                brew.setName(editTextName.getText().toString());
                brew.setBrewType(spinnerBrewType.getSelectedItemPosition());
                brew.setBrewDate(editTextBrewDate.getText().toString());
                if (!editTextPrice.getText().toString().trim().isEmpty())
                {
                    brew.setPrice(Double.parseDouble(editTextPrice.getText().toString()));
                }
                else
                {
                    brew.setPrice(0.0);
                }
                if (!editTextTDS.getText().toString().trim().isEmpty())
                {
                    brew.setTDS(Double.parseDouble(editTextTDS.getText().toString()));
                }
                else
                {
                    brew.setTDS(0.0);
                }
                if (editValue)
                {
//                    dataSourceBrew.update(brew);
                }
                else
                {
//                    dataSourceBrew.add(brew);
                }
                backPress();
            }
        }
    }

    private boolean validateName()
    {
        TextInputLayout textInputLayoutName = (TextInputLayout) view.findViewById(R.id.text_input_layout_name);
        EditText editTextName = textInputLayoutName.getEditText();
        assert editTextName != null;
        if (editTextName.getText().toString().trim().isEmpty())
        {
            textInputLayoutName.setError(getString(R.string.text_error_name_brew));
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

    private void setupSpinner()
    {
        spinnerCoffee.setAdapter(new SpinnerGenericAdapter<Coffee>(getContext(), R.layout.spinner_row, coffees)
        {
            @Override
            public View getCustomView(int position, View convertView, ViewGroup parent, List<Coffee> items, int layoutResourceId)
            {
                SpinnerViewHolder holder;
                if(convertView==null)
                {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.spinner_row, parent, false);
                    holder = new SpinnerViewHolder(view);
                    holder.title1 = (TextView) convertView.findViewById(R.id.text_title1);
                    holder.title2 = (TextView) convertView.findViewById(R.id.text_title2);
                    holder.imageView = (CircularImageView) convertView.findViewById(R.id.image_view);
                    convertView.setTag(holder);
                }
                else
                {
                    holder = (SpinnerViewHolder) convertView.getTag();
                }

                Coffee coffee = coffees.get(position);
                if(coffee != null)
                {
                    holder.title1.setText(coffee.getName());
                    if (!coffee.getLocation().trim().isEmpty())
                    {
                        holder.title1.setText(coffee.getName() + " - " + coffee.getLocation());
                    }
                    if (!coffee.getRoastDate().trim().isEmpty() && !coffee.getRoaster().trim().isEmpty())
                    {
                        holder.title2.setText(coffee.getRoaster() + " - " + coffee.getRoastDate());
                    }
                    else if (!coffee.getRoastDate().trim().isEmpty() && coffee.getRoaster().trim().isEmpty())
                    {
                        holder.title2.setText( getString(R.string.unknown_roaster) + " - " + coffee.getRoastDate());
                    }
                    else if (coffee.getRoastDate().trim().isEmpty() && !coffee.getRoaster().trim().isEmpty())
                    {
                        holder.title2.setText(coffee.getRoaster());
                    }
                    else if (coffee.getRoastDate().trim().isEmpty() && coffee.getRoaster().trim().isEmpty())
                    {
                        holder.title2.setText(getString(R.string.unknown_roaster));
                    }
                    Glide
                            .with(getContext())
                            .load(ImageUtils.getCoffeePhotoUri(getContext(),coffee))
                            .error(R.raw.photo_coffee_beans)
                            .crossFade()
                            .into(holder.imageView);
                }
                return convertView;
            }
        });
        spinnerCoffee.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                Utils.closeKeyboard(getActivity());
                return false;
            }
        });
        spinnerCoffee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id)
            {
                brew.setCoffeeId(coffees.get(position).getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });
        spinnerCoffee.setSelection(brew.getCoffeeId() - 1);
        spinnerBrewType.setAdapter(new SpinnerGenericAdapter<BrewTypeInfo>(getContext(), R.layout.spinner_row_brew_type, Values.BREW_TYPES)
        {
            @Override
            public View getCustomView(int position, View convertView, ViewGroup parent, List<BrewTypeInfo> items, int layoutResourceId)
            {
                SpinnerViewHolder holder;
                if(convertView==null)
                {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(layoutResourceId, parent, false);
                    holder = new SpinnerViewHolder(convertView);
                    holder.title1 = (TextView) convertView.findViewById(R.id.text_title1);
                    holder.title2 = (TextView) convertView.findViewById(R.id.text_title2);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
                    convertView.setTag(holder);
                }
                else
                {
                    holder = (SpinnerViewHolder) convertView.getTag();
                }

                BrewTypeInfo type = items.get(position);
                holder.title1.setText(getContext().getString(type.getBrewName()));
                Glide
                        .with(getContext())
                        .load(type.getImage())
                        .into(holder.imageView);
                return convertView;
            }
        });
        spinnerBrewType.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                Utils.closeKeyboard(getActivity());
                return false;
            }
        });
        spinnerBrewType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id)
            {
                brew.setBrewType(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });
        spinnerBrewType.setSelection(brew.getBrewType());
    }

    private void setupDatePicker()
    {
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                editTextBrewDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void addCoffee()
    {
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

    private void updateCoffeeList()
    {
//        coffees = dataSourceCoffee.getAll();
        if (coffees.size() == 0)
        {
            spinnerCoffee.setVisibility(View.INVISIBLE);
            textViewAddCoffee.setVisibility(View.VISIBLE);
        }
        else
        {
            spinnerCoffee.setVisibility(View.VISIBLE);
            textViewAddCoffee.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setBrew(Brew brew)
    {
        Log.i(TAG, "setBrew: ");
        this.brew = brew;
    }
    
    @Override
    public Brew getBrew()
    {
        brew.setName(editTextName.getText().toString());
        return brew;
    }

    @Override
    public void delete()
    {
        Log.i(TAG, "deleteCoffee: ");
        ImageUtils.deleteImage(getActivity(), brew.getImageName());
//        dataSourceBrew.delete(brew.getId());
    }

    @Override
    public void onResume()
    {
        Log.i(TAG, "onResume: ");
        super.onResume();
        toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in));
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
//        dataSourceCoffee.open();
//        dataSourceBrew.open();
        updateCoffeeList();
    }

    @Override
    public void onPause()
    {
        Log.i(TAG, "onPause: ");
        toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_out));
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.fab_add, getActivity().getTheme()));
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