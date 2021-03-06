package com.gabilheri.pawsalert.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 9/22/15.
 */
public final class PictureUtils {

    public static final int WIDTH = 1440;
    public static final int HEIGHT = 1080;

    private PictureUtils() {
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        // Create a matrix for manipulation
        Matrix matrix = new Matrix();
        // Resize the bitmap
        matrix.setRectToRect(new RectF(0, 0, width, height), new RectF(0, 0, newWidth, newHeight), Matrix.ScaleToFit.CENTER);
        // Return a newly created bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    public static Bitmap loadBitmapFromPath(String imagePath) {
        // bimatp factory
        BitmapFactory.Options options = new BitmapFactory.Options();
        // downsizing image as it throws OutOfMemory Exception for very large images
        options.inSampleSize = 2;
        return  BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     *
     * @param bm
     *      The bitmap to be saved
     * @param mediaStorageDir
     *      The FILE which holds the directory to be stored
     * @return
     *      The image path
     */
    public static String saveBitmap(@NonNull Bitmap bm, @Nullable File mediaStorageDir, @NonNull String fileName) {

        if (mediaStorageDir == null) {
            mediaStorageDir = FileUriUtils.createMediaStoreDir(Const.IMAGE_DIRECTORY_NAME);
        }

        if(mediaStorageDir == null) {
            return null;
        }

        // Create a media file name
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        OutputStream fOutputStream = null;
        try {
            fOutputStream = new FileOutputStream(mediaFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, fOutputStream);
            fOutputStream.flush();
            fOutputStream.close();
            return mediaFile.getPath();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String saveBitmap(Bitmap bm, File mediaStorageDir) {
        String timeStamp = DateFormatter.format(new Date());
        String fileName = ("IMG_" + timeStamp) + ".jpg";
        return saveBitmap(bm, mediaStorageDir, fileName);
    }

    public static ParseFile getParseFileFromPath(String path, String name, String sufix) {
        Bitmap bm = BitmapFactory.decodeFile(path);
        bm = resizeImage(bm);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] byteArray = stream.toByteArray();
        String fileName = name + "_" + new Date().getTime() + "_" + sufix + ".jpg";
        return new ParseFile(fileName, byteArray);
    }

    public static Bitmap resizeImage(Bitmap bm) {
        if (bm.getWidth() == bm.getHeight()) { // for Squared images...
            if (bm.getWidth() > WIDTH) { // Just in case the image is already small...
                 bm = PictureUtils.getResizedBitmap(bm, WIDTH, WIDTH);
            }
        } else if (bm.getWidth() > bm.getHeight()) { // We have a landscape image!
            if (bm.getWidth() > WIDTH) {
                bm = PictureUtils.getResizedBitmap(bm, WIDTH, HEIGHT);
            }
        } else { // We got ourselves a portrait image!
            if (bm.getWidth() > 1080) {
                bm = PictureUtils.getResizedBitmap(bm, HEIGHT, WIDTH);
            }
        }
        return bm;
    }

    public static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                String title = "gabilheri_paws_" + new Date().getTime() + ".jpg";
//                return FileUriUtils.writeToTempImageAndGetPathUri(context, bmp).toString();
                try {
                    return CacheManager.cacheImage(context, bmp, title);
                } catch (IOException ex) {
                    return null;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
