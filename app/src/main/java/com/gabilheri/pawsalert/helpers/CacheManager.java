package com.gabilheri.pawsalert.helpers;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 7/4/15.
 */
public final class CacheManager {

    private static final long MAX_SIZE = 104857600L; // 100MB
    public static final long FIVE_MB = 5242880L; // 5 MB

    private CacheManager() {

    }

    public static String cacheData(Context context, byte[] data, String name) throws IOException {

        File cacheDir = context.getCacheDir();
        long size = getDirSize(cacheDir);
        long newSize = data.length + size;

        cleanIfNecessary(context, size);

        File file = new File(cacheDir, name);
        FileOutputStream os = new FileOutputStream(file);
        try {
            os.write(data);
        }
        finally {
            os.flush();
            os.close();
        }
        return file.getPath();
    }

    public static String cacheImage(Context context, Bitmap data, String name) throws IOException {
        File cacheDir = context.getCacheDir();
        long size = getDirSize(cacheDir);
        long newSize = data.getByteCount() + size;

        cleanIfNecessary(context, newSize);

        File file = new File(cacheDir, name);
        OutputStream os = new FileOutputStream(file);
        try {
            data.compress(Bitmap.CompressFormat.JPEG, 90, os);
        }
        finally {
            os.flush();
            os.close();
        }
        return file.getPath();
    }

    public static void cleanIfNecessary(Context context, long dataSize) {
        File cacheDir = context.getCacheDir();
        long size = getDirSize(cacheDir);
        long newSize = dataSize + size;
        if (newSize > MAX_SIZE) {
            cleanDir(cacheDir, newSize - MAX_SIZE);
        }
    }

    public static byte[] retrieveData(Context context, String name) throws IOException {

        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, name);

        if (!file.exists()) {
            // Data doesn't exist
            return null;
        }

        byte[] data = new byte[(int) file.length()];
        FileInputStream is = new FileInputStream(file);
        try {
            is.read(data);
        }
        finally {
            is.close();
        }

        return data;
    }

    private static void cleanDir(File dir, long bytes) {

        long bytesDeleted = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            bytesDeleted += file.length();
            file.delete();

            if (bytesDeleted >= bytes) {
                break;
            }
        }
    }

    private static long getDirSize(File dir) {

        long size = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }

        return size;
    }
}