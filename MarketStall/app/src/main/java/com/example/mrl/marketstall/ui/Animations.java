package com.example.mrl.marketstall.ui;

import android.app.Activity;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mrl.marketstall.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

public class Animations
{
    private final String TAG = getClass().getSimpleName();

    public static void toolbarAnimation(final Activity activity, final int out, final int in, StorageReference loadImage, int errorImage)
    {
        ImageView toolbarImageViewMain = activity.findViewById(R.id.toolbar_image_main);
        ImageView toolbarImageViewSecond = activity.findViewById(R.id.toolbar_image_second);
        if (loadImage == null )
        {
            Glide
                    .with(activity)
                    .load(errorImage)
                    .error(errorImage)
                    .into(toolbarImageViewSecond);
        }
        else
        {
            Glide
                    .with(activity)
                    .using(new FirebaseImageLoader())
                    .load(loadImage)
                    .error(errorImage)
                    .into(toolbarImageViewSecond);
        }
        toolbarImageViewMain.startAnimation(AnimationUtils.loadAnimation(activity, out));
        toolbarImageViewSecond.startAnimation(AnimationUtils.loadAnimation(activity, in));
        toolbarImageViewSecond.setId(R.id.toolbar_image_main);
        toolbarImageViewMain.setId(R.id.toolbar_image_second);
    }
}
