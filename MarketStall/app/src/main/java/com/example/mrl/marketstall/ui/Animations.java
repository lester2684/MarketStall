package com.example.mrl.marketstall.ui;

import android.app.Activity;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mrl.marketstall.R;

public class Animations
{
    private final String TAG = getClass().getSimpleName();

    public static void toolbarAnimation(final Activity activity, final int out, final int in, Object loadImage, int errorImage)
    {
        ImageView toolbarImageViewMain = (ImageView) activity.findViewById(R.id.toolbar_image_main);
        ImageView toolbarImageViewSecond = (ImageView) activity.findViewById(R.id.toolbar_image_second);
        Glide
                .with(activity)
                .load(loadImage)
                .error(errorImage)
                .into(toolbarImageViewSecond);
        toolbarImageViewMain.startAnimation(AnimationUtils.loadAnimation(activity, out));
        toolbarImageViewSecond.startAnimation(AnimationUtils.loadAnimation(activity, in));
        toolbarImageViewSecond.setId(R.id.toolbar_image_main);
        toolbarImageViewMain.setId(R.id.toolbar_image_second);
    }
}
