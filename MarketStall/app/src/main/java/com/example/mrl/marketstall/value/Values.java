package com.example.mrl.marketstall.value;

import java.util.Arrays;
import java.util.List;

public class Values
{
    public static final List<String> permissions = Arrays.asList("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE","android.permission.ACCESS_FINE_LOCATION");
    public static final List<Integer> permissionCodes = Arrays.asList(101, 102, 103);

    public final static String SELECTED_ITEM = "selected_item";
    public final static String EDIT_VALUE = "edit_value";
    public final static String RECYCLER_TYPE = "recycler_type";
    public final static String DETAILS_TYPE = "details_type";
    public final static String FORM_TYPE = "form_type";
    public final static String TAB_TYPE = "view_brew";

    public final static String ITEM = "item";
    public final static String TAB_RECYCLERS = "recyclers";
    public final static String TAB_ITEM_DETAILS = "item_details";
    public final static String TAB_ITEM_EDIT = "item_edit";
    public final static String TAB_ITEM_NEW = "item_new";

}
