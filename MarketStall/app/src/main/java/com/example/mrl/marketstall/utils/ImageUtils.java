package com.example.mrl.marketstall.utils;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.mrl.marketstall.model.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class ImageUtils
{
    private static final String TAG = "ImageUtils";

    public static void deleteImage(final Activity activity, Item item)
    {
        String fileName =   item.getImageName();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://market-stall.appspot.com");
        StorageReference fileRef = storageRef.child("images/"+fileName);
        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "deleteImage - onFailure: " + e);
            }
        });
    }

    public static void saveImage(final Activity activity, Uri sourceUri, Item item)
    {
        Random gen = new Random();
        int n = 10000;
        n = gen.nextInt(n);
        String fileName =   item.getId() + "-" + n + ".jpg";
        item.setImageName(fileName);
        deleteImage(activity, item);
        try
        {
            final Bitmap bitmap = BitmapFactory.decodeFile(getPath(activity, sourceUri));
            ByteArrayOutputStream file = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, file);
            byte[] data = file.toByteArray();
            file.flush();
            file.close();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://market-stall.appspot.com");
            StorageReference fileRef = storageRef.child("images/"+fileName);
            UploadTask uploadTask = fileRef.putBytes(data);
            Toast.makeText(activity, "Uploading....", Toast.LENGTH_LONG).show();
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(activity, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
            bitmap.recycle();
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
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

    public static StorageReference getImage(Item item)
    {
        String fileName =   item.getImageName();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if (fileName != null) {
            return storage.getReferenceFromUrl("gs://market-stall.appspot.com/images/" + fileName);
        }
        else
        {
            return null;
        }
    }
}
