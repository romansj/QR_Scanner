package com.example.qrscanner;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.exceptions.Exceptions;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SaveHelper {
    /**
     * Saves the image as PNG to the app's cache directory.
     *
     * @param image Bitmap to save.
     * @return Uri of the saved file or null
     */
    private Uri saveImage(Bitmap image) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(MyApp.getInstance().getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(MyApp.getInstance(), "com.mydomain.fileprovider", file);

        } catch (IOException e) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }


    public void saveImage(Bitmap image, Callback callback) {
        Single<Uri> single = Single.just(saveImage(image));
        single.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(uri -> {
            callback.onReceiveUri(uri);
        });
    }

    Callback callback;

    public interface Callback {
        void onReceiveUri(Uri uri);
    }


    public void saveImageExternal(Bitmap image, Callback callback) {
        if (!isExternalStorageWritable()) {
            Log.e("saveImageExternal", "isExternalStorageWritable: false");
            return;
        }

        Single.just(saveImageExternal(image, "qr_code")).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(uri -> {
            callback.onReceiveUri(uri);
        });


    }


    private Uri saveImageExternal(Bitmap bitmap, @NonNull String name) {
        Uri imageUri = null;

        try {
            OutputStream fos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = MyApp.getInstance().getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".png");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                File image = new File(imagesDir, name + ".png");
                fos = new FileOutputStream(image);
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Objects.requireNonNull(fos).close();
        } catch (Exception e) {
            Exceptions.propagate(e);
        }

        return imageUri;
    }


    /**
     * Checks if the external storage is writable.
     *
     * @return true if storage is writable, false otherwise
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
