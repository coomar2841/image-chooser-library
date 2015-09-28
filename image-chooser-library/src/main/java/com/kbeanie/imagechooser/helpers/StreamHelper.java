package com.kbeanie.imagechooser.helpers;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.kbeanie.imagechooser.exceptions.ChooserException;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by vervik on 9/27/15.
 */
public class StreamHelper {

    static final String TAG = StreamHelper.class.getSimpleName();


    public static void closeSilent(Closeable stream) {
        try {
            close(stream);
        } catch (ChooserException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void close(Closeable stream) throws ChooserException {
        if(stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                throw new ChooserException(e);
            }
        }
    }

    public static void flush(OutputStream stream) throws ChooserException {
        if(stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                throw new ChooserException(e);
            }
        }
    }

    public static void verifyCursor(Uri uri, Cursor cursor) throws ChooserException {
        if(cursor == null) {
            throw new ChooserException("Didnt not get cursor in return for = " + uri);
        }
    }

    public static void verifyStream(String path, ParcelFileDescriptor descriptor) throws ChooserException {
        if(descriptor == null) {
            throw new ChooserException("Could not read file descriptor from file at path = " + path);
        }
    }

    public static void verifyStream(String path, InputStream is) throws ChooserException {
        if(is == null) {
            throw new ChooserException("Could not open stream to read path = " + path);
        }
    }

    public static void verifyBitmap(String path, Bitmap bitmap) throws ChooserException {
        if(bitmap == null) {
            throw new ChooserException("Could not read bitmap from this path = " + path);
        }
    }

    public static boolean isNonNull(Bitmap bitmap) {
        if(bitmap != null) {
            return true;
        }
        Log.w(TAG, "Bitmap is null. No good.");
        return false;
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while (-1 != (n = input.read(buffer))) {
            byteArrayOutputStream.write(buffer, 0, n);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
