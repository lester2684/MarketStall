package com.example.mrl.marketstall.utils;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.mrl.marketstall.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.Random;

public class ImageUtils
{
    private static final String TAG = "ImageUtils";

    private static final String FORWARD_SLASH = "/";

    public static void deleteImage(Activity activity, String imageName)
    {
        File sd = Environment.getExternalStorageDirectory();
        String destinationImagePath = activity.getResources().getString(R.string.photos_folders);
        File destinationFolder = new File(sd, destinationImagePath);

        if (!destinationFolder.exists())
        {
            boolean destination = destinationFolder.mkdirs();
            Log.i(TAG, "deleteImage: " + destinationImagePath + " " + destination);
        }
        if (imageName != null)
        {
            File file = new File(destinationFolder, imageName);
            if (file.exists())
            {
                boolean delete = file.delete();
                Log.i(TAG, "deleteImage: deleteCoffee" + imageName + " " + delete);
            }
        }
    }

    public static String saveImage(Activity activity, Uri sourceUri, String type, int id, String oldImageName)
    {
        Random gen = new Random();
        int n = 10000;
        n = gen.nextInt(n);
        String newFileName =   type + "-" + id + "-" + n + ".jpg";

        File sd = Environment.getExternalStorageDirectory();
        String destinationImagePath = activity.getResources().getString(R.string.photos_folders);
        File destinationFolder = new File(sd, destinationImagePath);

        if (!destinationFolder.exists())
        {
            boolean destination = destinationFolder.mkdirs();
            Log.i(TAG, "saveImage: " + destinationImagePath + " " + destination);
        }
        if (!(oldImageName == null))
        {
            File file = new File(destinationFolder, oldImageName);
            if (file.exists() && !Objects.equals(newFileName, oldImageName))
            {
                boolean delete = file.delete();
                Log.i(TAG, "saveImage: deleteCoffee" + oldImageName + " " + delete);
            }
        }
        try
        {
            Bitmap bitmap = BitmapFactory.decodeFile(getPath(activity, sourceUri));
            File newFile = new File(destinationFolder, newFileName);
            FileOutputStream out = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return newFileName;
    }

    private static String getPath(Activity activity, Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

//    public static Uri getCoffeePhotoUri(Context context, Coffee coffee)
//    {
//        File sd = Environment.getExternalStorageDirectory();
//        File destination = new File(sd, context.getString(R.string.photos_folders));
//        File photo = new File(destination, FORWARD_SLASH + coffee.getImageName());
//        return Uri.fromFile(photo);
//    }
//
//    public static Uri getBrewPhotoUri(Context context, Brew brew)
//    {
//        File sd = Environment.getExternalStorageDirectory();
//        File destination = new File(sd, context.getString(R.string.photos_folders));
//        File photo = new File(destination, FORWARD_SLASH + brew.getImageName());
//        return Uri.fromFile(photo);
//    }
}
