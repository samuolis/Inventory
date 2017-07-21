package com.example.android.inventory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class Utils {

    public static final String LOG_TAG = Utils.class.getSimpleName();

    private void Utils() {
    }


    public static Bitmap getBitmapFromUri(Uri uri, Context context, ImageView imageView) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        InputStream input = null;
        try {
            input = context.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            Log.i(LOG_TAG, "" + targetW + " " + targetH + " " + photoW + " " + photoH);
            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);


            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            input = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, context.getString(R.string.exception_image_load_failed), fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, context.getString(R.string.exception_image_load_failed), e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }
}
