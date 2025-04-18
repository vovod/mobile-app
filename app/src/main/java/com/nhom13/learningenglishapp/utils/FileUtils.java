package com.nhom13.learningenglishapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String IMAGE_DIRECTORY = "learningenglishapp_images";

    /**
     * Save an image from Uri to internal storage
     * @param context Application context
     * @param imageUri Uri of the image to save
     * @param imageName Name to save the image as
     * @return Path to the saved image
     */
    public static String saveImageToInternalStorage(Context context, Uri imageUri, String imageName) {
        try {
            // Create directory if it doesn't exist
            File directory = new File(context.getFilesDir(), IMAGE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Get bitmap from uri
            Bitmap bitmap = getBitmapFromUri(context, imageUri);
            if (bitmap == null) {
                Log.e(TAG, "Failed to get bitmap from uri");
                return "";
            }

            // Create file
            File imageFile = new File(directory, imageName + ".jpg");
            FileOutputStream fos = new FileOutputStream(imageFile);

            // Compress and save
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Error saving image: " + e.getMessage());
            return "";
        }
    }

    /**
     * Delete an image from internal storage
     * @param context Application context
     * @param imagePath Path to the image to delete
     * @return True if delete was successful
     */
    public static boolean deleteImageFromInternalStorage(Context context, String imagePath) {
        try {
            File file = new File(imagePath);
            return file.delete();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting image: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get bitmap from Uri
     * @param context Application context
     * @param uri Uri of the image
     * @return Bitmap
     */
    private static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (inputStream != null) {
            inputStream.close();
        }
        return bitmap;
    }

    /**
     * Get an image from internal storage
     * @param imagePath Path to the image
     * @return Bitmap of the image
     */
    public static Bitmap getImageFromPath(String imagePath) {
        try {
            File file = new File(imagePath);
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "Error getting image: " + e.getMessage());
            return null;
        }
    }
}