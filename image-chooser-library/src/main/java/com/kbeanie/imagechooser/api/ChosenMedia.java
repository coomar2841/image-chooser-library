
package com.kbeanie.imagechooser.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

public abstract class ChosenMedia {

    protected SoftReference<Bitmap> getBitmap(String path) {
        SoftReference<Bitmap> bitmap = null;
        try {
            bitmap = new SoftReference<Bitmap>(BitmapFactory.decodeStream(new FileInputStream(
                    new File(path))));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public String getFileExtension(String path) {
        return FileUtils.getFileExtension(path);
    }
    
    protected String getWidth(String path){
        String width = "";
        try {
            ExifInterface exif = new ExifInterface(path);
            width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            if (width.equals("0")) {
                width = Integer.toString(getBitmap(path).get().getWidth());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return width;
    }
    
    protected String getHeight(String path){
        String height = "";
        try {
            ExifInterface exif = new ExifInterface(path);
            height = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            if (height.equals("0")) {
                height = Integer.toString(getBitmap(path).get().getHeight());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return height;
    }

    public abstract String getMediaHeight();
    
    public abstract String getMediaWidth();   
}
