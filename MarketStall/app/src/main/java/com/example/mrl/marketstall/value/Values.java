package com.example.mrl.marketstall.value;

import com.example.mrl.marketstall.R;
import com.example.mrl.marketstall.model.BrewTypeInfo;

import java.util.ArrayList;
import java.util.List;

public class Values
{
    public final static String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public final static int WRITE_EXTERNAL_RESULT = 101;

    public final static int TUTORIAL_SHOT_ID = 10;

    public static final String PREFS_SHOWCASE_INTERNAL = "showcase_internal";

    public final static String SELECTED_BREW = "selected_brew";
    public final static String SELECTED_COFFEE = "selected_coffee";
    public final static String SELECTED_BREW_RECIPE = "selected_brew_recipe";
    public final static String EDIT_VALUE = "edit_value";
    public final static String RECYCLER_TYPE = "recycler_type";
    public final static String DETAILS_TYPE = "details_type";
    public final static String FORM_TYPE = "form_type";
    public final static String TAB_TYPE = "view_brew";

    public final static String COFFEE = "coffee";
    public final static String BREW = "brew";
    public final static String BREW_RECIPE = "brew_recipe";
    public final static String TAB_RECYCLERS = "recyclers";
    public final static String TAB_BREW_DETAILS = "brew_details";
    public final static String TAB_BREW_EDIT = "brew_edit";
    public final static String TAB_BREW_NEW = "brew_new";

    private final static BrewTypeInfo TURKISH = new BrewTypeInfo(R.string.brew_type_turkish, 0, R.raw.type_turkish);
    private final static BrewTypeInfo CUPPING = new BrewTypeInfo(R.string.brew_type_cupping, 1, R.raw.type_cupping);
    private final static BrewTypeInfo COLD_BREW = new BrewTypeInfo(R.string.brew_type_cold_brew, 2, R.raw.type_coldbrew);
    private final static BrewTypeInfo PLUNGER = new BrewTypeInfo(R.string.brew_type_plunger, 3, R.raw.type_plunger);
    private final static BrewTypeInfo CHEMEX = new BrewTypeInfo(R.string.brew_type_chemex, 4, R.raw.type_chemex);
    private final static BrewTypeInfo POUR_OVER = new BrewTypeInfo(R.string.brew_type_pour_over, 5, R.raw.type_pourover);
    private final static BrewTypeInfo BATCH_BREW = new BrewTypeInfo(R.string.brew_type_batch_brew, 6, R.raw.type_batchbrew);
    private final static BrewTypeInfo ESPRESSO = new BrewTypeInfo(R.string.brew_type_espresso, 7, R.raw.type_espresso);
    private final static BrewTypeInfo SIPHON = new BrewTypeInfo(R.string.brew_type_siphon, 8, R.raw.type_siphon);
    private final static BrewTypeInfo AEROPRESS = new BrewTypeInfo(R.string.brew_type_aeropress, 9, R.raw.type_aeropress);

    public final static List<BrewTypeInfo> BREW_TYPES = new ArrayList<BrewTypeInfo>()
    {
        {
            add(TURKISH);
            add(CUPPING);
            add(COLD_BREW);
            add(PLUNGER);
            add(CHEMEX);
            add(POUR_OVER);
            add(BATCH_BREW);
            add(ESPRESSO);
            add(SIPHON);
            add(AEROPRESS);
        }
    };

}
