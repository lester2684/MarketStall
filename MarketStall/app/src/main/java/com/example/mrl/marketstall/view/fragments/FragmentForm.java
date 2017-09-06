package com.example.mrl.marketstall.view.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.adapter.RecyclerGenericAdapter;
import com.example.mrl.marketstall.adapter.SpinnerGenericAdapter;
import com.example.mrl.marketstall.interfaces.Callbacks;
import com.example.mrl.marketstall.interfaces.CallbacksTabEdit;
import com.example.mrl.marketstall.model.Brew;
import com.example.mrl.marketstall.model.BrewRecipe;
import com.example.mrl.marketstall.model.BrewTypeInfo;
import com.example.mrl.marketstall.model.Coffee;
import com.example.mrl.marketstall.model.FormInfo;
import com.example.mrl.marketstall.ui.Animations;
import com.example.mrl.marketstall.utils.ImageUtils;
import com.example.mrl.marketstall.utils.Utils;
import com.example.mrl.marketstall.value.Values;
import com.example.mrl.marketstall.viewholder.RecyclerViewHolderForm;
import com.example.mrl.marketstall.viewholder.RecyclerViewHolderFormSpinner;
import com.example.mrl.marketstall.viewholder.SpinnerViewHolder;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.bumptech.glide.Glide.with;

public class FragmentForm extends Fragment implements Callbacks, CallbacksTabEdit
{
    private final String TAG = getClass().getSimpleName();
    private static int LOAD_IMAGE_RESULTS = 1;
    private View view;
    private Toolbar toolbar;
    private ImageView toolbarImageViewMain;
    private TextView toolbarTextView;
    private FloatingActionMenu fabMenu;
    private RecyclerView recyclerViewForm;
//    private DataSourceCoffee dataSourceCoffee;
//    private DataSourceBrew dataSourceBrew;
//    private DataSourceBrewRecipe dataSourceBrewRecipe;
    private BrewRecipe brewRecipe;
    private Coffee coffee;
    private Brew brew;
    private ArrayList<BrewRecipe> brewRecipes;
    private ArrayList<Coffee> coffees;
    private Calendar calendar;
    private String tabType;
    private Boolean editValue = false;
    private Uri imageURI;
    private boolean imageBoolean = false;
    private boolean showMenu = false;
    private DatePickerDialog datePickerDialog;
    private int datePosition = 0;
    private List<FormInfo> formList;
    private String formType;
    private boolean imageSelected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_coffee_form, container, false);

        setupValues();
        setupToolbar();
        setupFAB();
        setupDatePicker();
        setupRecyclerViewForm();

        return view;
    }

    private void setupValues()
    {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarImageViewMain = (ImageView) getActivity().findViewById(R.id.toolbar_image_main);
        toolbarTextView = (TextView) getActivity().findViewById(R.id.toolbar_text_view);
        fabMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fab_menu);

        recyclerViewForm = (RecyclerView) view.findViewById(R.id.recyclerViewForm);

//        dataSourceCoffee = new DataSourceCoffee(view.getContext());
//        dataSourceBrew = new DataSourceBrew(view.getContext());
//        dataSourceBrewRecipe = new DataSourceBrewRecipe(view.getContext());
//        dataSourceCoffee.open();
//        dataSourceBrewRecipe.open();
//        dataSourceBrew.open();

        calendar = Calendar.getInstance();

        formType = getArguments().getString(Values.FORM_TYPE);
        editValue = getArguments().getBoolean(Values.EDIT_VALUE);

        switch (formType)
        {
            case Values.COFFEE:
                if (editValue)
                {
//                    coffee = dataSourceCoffee.getById(getArguments().getInt(Values.SELECTED_COFFEE));
                }
                else
                {
                    coffee = new Coffee();
                }
                formList = coffee.getDetails();
                break;
            case Values.BREW_RECIPE:
                if (editValue)
                {
//                    brewRecipe = dataSourceBrewRecipe.getById(getArguments().getInt(Values.SELECTED_BREW_RECIPE));
                }
                else
                {
                    brewRecipe = new BrewRecipe();
                }
                formList = brewRecipe.getDetails();
                break;
            case Values.BREW:
//                coffees = dataSourceCoffee.getAll();
//                brewRecipes = dataSourceBrewRecipe.getAll();
                tabType = getArguments().getString(Values.TAB_TYPE);
                if(editValue)
                {
//                    brew = dataSourceBrew.getById(getArguments().getInt(Values.SELECTED_BREW));
                }
                else
                {
                    brew = new Brew();
                    brew.setCoffeeId(getArguments().getInt(Values.SELECTED_COFFEE));
                }
                formList = brew.getDetails();
                break;
        }
    }

    private void setupToolbar()
    {
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar_layout);
        appBarLayout.setExpanded(true);
        toolbar.getMenu().clear();
        switch (formType)
        {
            case Values.BREW:
            case Values.COFFEE:
                toolbarTextView.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        openGallery();
                    }
                });
                toolbarTextView.setText(R.string.change_image);
                toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in));

                break;
            case Values.BREW_RECIPE:
                break;
    }
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(" ");
    }

    private void setupDatePicker()
    {
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                View viewItem = recyclerViewForm.getLayoutManager().findViewByPosition(datePosition);
                EditText editText = (EditText) viewItem.findViewById(R.id.edit_text);
                editText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
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

    private void setupRecyclerViewForm()
    {
        RecyclerGenericAdapter<FormInfo> formInfoRecyclerAdapter = null;
        switch (formType)
        {
            case Values.COFFEE:
                formInfoRecyclerAdapter = new RecyclerGenericAdapter<FormInfo>(getActivity(), formList)
                {
                    @Override
                    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked)
                    {
                        final View holder;
                        holder = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_row_form, parent, false);
                        return new RecyclerViewHolderForm(holder);
                    }

                    @Override
                    public void onBindData(Context context, RecyclerView.ViewHolder holder, FormInfo item, final int recyclerPosition)
                    {
                        final RecyclerViewHolderForm viewHolder;
                        viewHolder = (RecyclerViewHolderForm) holder;
                        viewHolder.editText.setText(item.getText());
                        viewHolder.textInputLayout.setHint(getString(item.getHint()));
                        if (item.getImage() != 0)
                        {
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
                        if (item.getImage() == R.drawable.button) {
                            datePosition = recyclerPosition;
                            viewHolder.editText.setFocusable(false);
                            viewHolder.editText.setFocusableInTouchMode(false);
                            viewHolder.editText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    datePickerDialog.show();
                                }
                            });
                        }
                    }

                    @Override
                    public OnRecyclerItemClicked onGetRecyclerItemClickListener()
                    {
                        return new OnRecyclerItemClicked() {
                            @Override
                            public void onItemClicked(View view, int position) {

                            }
                        };
                    }
                };
                break;
            case Values.BREW_RECIPE:
                formInfoRecyclerAdapter = new RecyclerGenericAdapter<FormInfo>(getActivity(), formList)
                {
                    @Override
                    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked)
                    {
                        final View holder;
                        switch (viewType)
                        {
                            case 0:
                                holder = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_row_spinner, parent, false);
                                return new RecyclerViewHolderFormSpinner(holder);
                            default:
                                holder = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_row_form, parent, false);
                                return new RecyclerViewHolderForm(holder);
                        }
                    }

                    @Override
                    public void onBindData(Context context, RecyclerView.ViewHolder holder, FormInfo item, final int recyclerPosition)
                    {
                        final RecyclerViewHolderForm viewHolder;
                        switch (recyclerPosition)
                        {
                            case 0:
                                final RecyclerViewHolderFormSpinner viewHolderFormSpinner = (RecyclerViewHolderFormSpinner) holder;
                                viewHolderFormSpinner.spinner.setAdapter(new SpinnerGenericAdapter<BrewTypeInfo>(getContext(), R.layout.spinner_row_brew_type, Values.BREW_TYPES) {
                                    @Override
                                    public View getCustomView(int position, View convertView, ViewGroup parent, List<BrewTypeInfo> items, int layoutResourceId) {
                                        SpinnerViewHolder holder;
                                        if (convertView == null) {
                                            LayoutInflater inflater = LayoutInflater.from(getContext());
                                            convertView = inflater.inflate(layoutResourceId, parent, false);
                                            holder = new SpinnerViewHolder(convertView);
                                            holder.title1 = (TextView) convertView.findViewById(R.id.text_title1);
                                            holder.title2 = (TextView) convertView.findViewById(R.id.text_title2);
                                            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
                                            convertView.setTag(holder);
                                        } else {
                                            holder = (SpinnerViewHolder) convertView.getTag();
                                        }

                                        BrewTypeInfo type = items.get(position);
                                        holder.title1.setText(getContext().getString(type.getBrewName()));
                                        with(getContext())
                                                .load(type.getImage())
                                                .into(holder.imageView);
                                        return convertView;
                                    }
                                });
                                viewHolderFormSpinner.spinner.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        Utils.closeKeyboard(getActivity());
                                        return false;
                                    }
                                });
                                viewHolderFormSpinner.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                                        formList.get(recyclerPosition).setText(String.valueOf(position));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {

                                    }
                                });
                                viewHolderFormSpinner.spinner.setSelection(brewRecipe.getBrewType());
                                if (item.getImage() != 0) {
                                    with(getContext())
                                            .load(item.getImage())
                                            .into(viewHolderFormSpinner.icon);
                                }
                                break;
                            default:
                                viewHolder = (RecyclerViewHolderForm) holder;
                                viewHolder.editText.setText(item.getText());
                                viewHolder.editText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                viewHolder.textInputLayout.setHint(getString(item.getHint()));
                                with(getContext())
                                        .load(item.getImage())
                                        .into(viewHolder.icon);
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
                                break;
                        }
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
            case Values.BREW:
                formInfoRecyclerAdapter = new RecyclerGenericAdapter<FormInfo>(getActivity(), formList)
                {
                    @Override
                    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType, OnRecyclerItemClicked onRecyclerItemClicked)
                    {
                        final View holder;
                        switch (viewType)
                        {
                            case 1:
                            case 2:
                            case 3:
                                holder = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_row_spinner, parent, false);
                                return new RecyclerViewHolderFormSpinner(holder);
                            default:
                                holder = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_row_form, parent, false);
                                return new RecyclerViewHolderForm(holder);
                        }
                    }

                    @Override
                    public void onBindData(Context context, RecyclerView.ViewHolder holder, FormInfo item, final int recyclerPosition)
                    {
                        RecyclerViewHolderFormSpinner viewHolderFormSpinner;
                        switch (recyclerPosition)
                        {
                            case 1:
                                viewHolderFormSpinner = (RecyclerViewHolderFormSpinner) holder;
                                viewHolderFormSpinner.spinner.setAdapter(new SpinnerGenericAdapter<Coffee>(getContext(), R.layout.spinner_row, coffees) {
                                    @Override
                                    public View getCustomView(int position, View convertView, ViewGroup parent, List<Coffee> items, int layoutResourceId) {
                                        SpinnerViewHolder holder;
                                        if (convertView == null)
                                        {
                                            LayoutInflater inflater = LayoutInflater.from(getContext());
                                            convertView = inflater.inflate(layoutResourceId, parent, false);
                                            holder = new SpinnerViewHolder(convertView);
                                            holder.title1 = (TextView) convertView.findViewById(R.id.text_title1);
                                            holder.title2 = (TextView) convertView.findViewById(R.id.text_title2);
                                            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
                                            convertView.setTag(holder);
                                        } else {
                                            holder = (SpinnerViewHolder) convertView.getTag();
                                        }
                                        if (position < items.size())
                                        {
                                            Coffee coffee = items.get(position);
                                            holder.title1.setText(coffee.getName());
                                            if (!coffee.getLocation().trim().isEmpty()) {
                                                holder.title1.setText(coffee.getName() + " - " + coffee.getLocation());
                                            }
                                            if (!coffee.getRoastDate().trim().isEmpty() && !coffee.getRoaster().trim().isEmpty()) {
                                                holder.title2.setText(coffee.getRoaster() + " - " + coffee.getRoastDate());
                                            } else if (!coffee.getRoastDate().trim().isEmpty() && coffee.getRoaster().trim().isEmpty()) {
                                                holder.title2.setText(getString(R.string.unknown_roaster) + " - " + coffee.getRoastDate());
                                            } else if (coffee.getRoastDate().trim().isEmpty() && !coffee.getRoaster().trim().isEmpty()) {
                                                holder.title2.setText(coffee.getRoaster());
                                            } else if (coffee.getRoastDate().trim().isEmpty() && coffee.getRoaster().trim().isEmpty()) {
                                                holder.title2.setText(getString(R.string.unknown_roaster));
                                            }
                                            Glide
                                                    .with(getContext())
                                                    .load(ImageUtils.getCoffeePhotoUri(getContext(), coffee))
                                                    .error(R.raw.photo_coffee_beans)
                                                    .crossFade()
                                                    .into(holder.imageView);
                                        }
                                        return convertView;
                                    }
                                });
                                viewHolderFormSpinner.spinner.setSelection(brew.getCoffeeId() - 1);
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolderFormSpinner.spinner.getLayoutParams();
                                layoutParams.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.form_icon_margin_32));
                                viewHolderFormSpinner.spinner.setLayoutParams(layoutParams);
                                if (coffees.size() == 0) {
                                    viewHolderFormSpinner.spinner.setVisibility(View.INVISIBLE);
                                    viewHolderFormSpinner.textView.setVisibility(View.VISIBLE);
                                } else {
                                    viewHolderFormSpinner.spinner.setVisibility(View.VISIBLE);
                                    viewHolderFormSpinner.textView.setVisibility(View.INVISIBLE);
                                }
                                viewHolderFormSpinner.button.setVisibility(View.VISIBLE);
                                viewHolderFormSpinner.button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addCoffee();
                                    }
                                });
                                viewHolderFormSpinner.spinner.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        Utils.closeKeyboard(getActivity());
                                        return false;
                                    }
                                });
                                viewHolderFormSpinner.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapter, View v, int SpinnerPosition, long id) {
                                        formList.get(recyclerPosition).setText(String.valueOf(SpinnerPosition));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {

                                    }
                                });
                                with(getContext())
                                        .load(item.getImage())
                                        .into(viewHolderFormSpinner.icon);
                                break;
                            case 2:
                                viewHolderFormSpinner = (RecyclerViewHolderFormSpinner) holder;
                                viewHolderFormSpinner.spinner.setAdapter(new SpinnerGenericAdapter<BrewRecipe>(getContext(), R.layout.spinner_row, brewRecipes)
                                {
                                    @Override
                                    public View getCustomView(int position, View convertView, ViewGroup parent, List<BrewRecipe> items, int layoutResourceId)
                                    {
                                        SpinnerViewHolder holder;
                                        if (convertView == null) {
                                            LayoutInflater inflater = LayoutInflater.from(getContext());
                                            convertView = inflater.inflate(layoutResourceId, parent, false);
                                            holder = new SpinnerViewHolder(convertView);
                                            holder.title1 = (TextView) convertView.findViewById(R.id.text_title1);
                                            holder.title2 = (TextView) convertView.findViewById(R.id.text_title2);
                                            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
                                            convertView.setTag(holder);
                                        } else {
                                            holder = (SpinnerViewHolder) convertView.getTag();
                                        }

                                        BrewRecipe brewRecipe = brewRecipes.get(position);
                                        if(brewRecipe != null)
                                        {
                                            holder.title1.setText(brewRecipe.getId() +"");
                                            Glide
                                                    .with(getContext())
                                                    .load(R.raw.photo_coffee_cup)
                                                    .into(holder.imageView);
                                        }
                                        return convertView;
                                    }
                                });
                                viewHolderFormSpinner.spinner.setSelection(0);
                                RelativeLayout.LayoutParams layoutParamsBrewRecipes = (RelativeLayout.LayoutParams) viewHolderFormSpinner.spinner.getLayoutParams();
                                layoutParamsBrewRecipes.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.form_icon_margin_32));
                                viewHolderFormSpinner.spinner.setLayoutParams(layoutParamsBrewRecipes);
                                if (brewRecipes.size() == 0)
                                {
                                    viewHolderFormSpinner.spinner.setVisibility(View.INVISIBLE);
                                    viewHolderFormSpinner.textView.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    viewHolderFormSpinner.spinner.setVisibility(View.VISIBLE);
                                    viewHolderFormSpinner.textView.setVisibility(View.INVISIBLE);
                                }
                                viewHolderFormSpinner.button.setVisibility(View.VISIBLE);
                                viewHolderFormSpinner.button.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        addBrewRecipe();
                                    }
                                });
                                viewHolderFormSpinner.spinner.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        Utils.closeKeyboard(getActivity());
                                        return false;
                                    }
                                });
                                viewHolderFormSpinner.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapter, View v, int SpinnerPosition, long id) {
                                        formList.get(recyclerPosition).setText(String.valueOf(SpinnerPosition));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {

                                    }
                                });
                                with(getContext())
                                        .load(item.getImage())
                                        .into(viewHolderFormSpinner.icon);
                                break;
                            case 3:
                                viewHolderFormSpinner = (RecyclerViewHolderFormSpinner) holder;
                                viewHolderFormSpinner.spinner.setAdapter(new SpinnerGenericAdapter<BrewTypeInfo>(getContext(), R.layout.spinner_row_brew_type, Values.BREW_TYPES)
                                {
                                    @Override
                                    public View getCustomView(int position, View convertView, ViewGroup parent, List<BrewTypeInfo> items, int layoutResourceId) {
                                        SpinnerViewHolder holder;
                                        if (convertView == null) {
                                            LayoutInflater inflater = LayoutInflater.from(getContext());
                                            convertView = inflater.inflate(layoutResourceId, parent, false);
                                            holder = new SpinnerViewHolder(convertView);
                                            holder.title1 = (TextView) convertView.findViewById(R.id.text_title1);
                                            holder.title2 = (TextView) convertView.findViewById(R.id.text_title2);
                                            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
                                            convertView.setTag(holder);
                                        } else {
                                            holder = (SpinnerViewHolder) convertView.getTag();
                                        }

                                        BrewTypeInfo type = items.get(position);
                                        holder.title1.setText(getContext().getString(type.getBrewName()));
                                        with(getContext())
                                                .load(type.getImage())
                                                .into(holder.imageView);
                                        return convertView;
                                    }
                                });
                                viewHolderFormSpinner.spinner.setSelection(brew.getBrewType());
                                viewHolderFormSpinner.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapter, View v, int SpinnerPosition, long id) {
                                        formList.get(recyclerPosition).setText(String.valueOf(SpinnerPosition));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {

                                    }
                                });
                                with(getContext())
                                        .load(item.getImage())
                                        .into(viewHolderFormSpinner.icon);
                                break;
                            default:
                                final RecyclerViewHolderForm viewHolder = (RecyclerViewHolderForm) holder;
                                viewHolder.editText.setText(item.getText());
                                switch (recyclerPosition)
                                {
                                    case 5:
                                    case 6:
                                    case 7:
                                        viewHolder.editText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                        break;
                                }
                                if (item.getHint() != 0)
                                {
                                    viewHolder.textInputLayout.setHint(getString(item.getHint()));
                                }
                                with(getContext())
                                        .load(item.getImage())
                                        .into(viewHolder.icon);
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
                                if (item.getImage() == R.drawable.button)
                                {
                                    datePosition = recyclerPosition;
                                    viewHolder.editText.setFocusable(false);
                                    viewHolder.editText.setFocusableInTouchMode(false);
                                    viewHolder.editText.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            datePickerDialog.show();
                                        }
                                    });
                                }
                                break;
                        }
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
                case Values.COFFEE:
                    textInputLayout.setError(getString(R.string.text_error_name_coffee));
                    break;
                case Values.BREW_RECIPE:
                    break;
                case Values.BREW:
                    textInputLayout.setError(getString(R.string.text_error_name_brew));
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
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
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

    private void addBrewRecipe()
    {
        Animations.toolbarAnimation(getActivity(), R.anim.fade_out, R.anim.fade_in, R.raw.photo_coffee_cup, R.raw.photo_coffee_beans);
        Bundle bundle = new Bundle();
        bundle.putString(Values.FORM_TYPE, Values.BREW_RECIPE);
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

    public String getFormType() {
        return formType;
    }

    @Override
    public void save()
    {
        Log.i(TAG, "save: ");
        switch (formType)
        {
            case Values.COFFEE:
                if (!validateName())
                {
                    return;
                }
                coffee.setByList(formList);
                if (editValue)
                {
                    if (imageBoolean)
                    {
                        String imageName = ImageUtils.saveImage(getActivity(), imageURI, coffee.getType(), coffee.getId(), coffee.getImageName());
                        coffee.setImageName(imageName);
                    }
//                    dataSourceCoffee.update(coffee);
                }
                else
                {
//                    coffee.setId(dataSourceCoffee.printAutoIncrements());
                    if (imageBoolean)
                    {
                        String imageName = ImageUtils.saveImage(getActivity(), imageURI, coffee.getType(), coffee.getId(), coffee.getImageName());
                        coffee.setImageName(imageName);
                    }
//                    dataSourceCoffee.add(coffee);
                }
                backPress();
                break;
            case Values.BREW_RECIPE:
                brewRecipe.setByList(formList);
                if (editValue)
                {
//                    dataSourceBrewRecipe.update(brewRecipe);
                }
                else
                {
//                    brewRecipe.setId(dataSourceBrewRecipe.printAutoIncrements());
//                    dataSourceBrewRecipe.add(brewRecipe);
                }
                backPress();
                break;
            case Values.BREW:
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
                        brew.setByList(formList);
                        if (editValue)
                        {
//                            dataSourceBrew.update(brew);
                        }
                        else
                        {
//                            brew.setId(dataSourceBrew.printAutoIncrements());
//                            dataSourceBrew.add(brew);
                        }
                        backPress();
                    }
                }
                break;
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
        brew.setByList(formList);
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
            case Values.BREW:
            case Values.COFFEE:
                toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in));
                break;
            case Values.BREW_RECIPE:
                break;
        }
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.button, getActivity().getTheme()));
//        dataSourceCoffee.open();
//        dataSourceBrewRecipe.open();
//        dataSourceBrew.open();
//        coffees = dataSourceCoffee.getAll();
//        brewRecipes = dataSourceBrewRecipe.getAll();
    }

    @Override
    public void onPause()
    {
        Log.i(TAG, "onPause: ");
        super.onPause();
        switch (formType)
        {
            case Values.BREW:
            case Values.COFFEE:
                toolbarTextView.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_out));
                break;
            case Values.BREW_RECIPE:
                break;
        }
        Utils.closeKeyboard(getActivity());
        fabMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.fab_add, getActivity().getTheme()));
//        dataSourceBrewRecipe.close();
//        dataSourceCoffee.close();
//        dataSourceBrew.close();
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
