/*******************************************************************************
 * Copyright 2013 Kumar Bibek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    
 * http://www.apache.org/licenses/LICENSE-2.0
 * 	
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.beanie.imagechooser.api;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;

import com.beanie.imagechooser.api.config.Config;
import com.beanie.imagechooser.threads.ImageProcessorListener;
import com.beanie.imagechooser.threads.ImageProcessorThread;

/**
 * Easy Image Chooser Library for Android Apps. Forget about coding workarounds
 * for different devices, OSes and folders.
 * 
 * @author Beanie
 */
public class ImageChooserManager implements ImageProcessorListener {
    private final static String TAG = "ImageChooserManager";

    private final static String DIRECTORY = "bimagechooser";

    private String filePathOriginal;

    private ImageChooserListener listener;

    private Activity activity;

    private int type;

    private String foldername;

    private boolean shouldCreateThumbnails;

    /**
     * Simplest constructor. Specify the type
     * {@link ChooserType.REQUEST_CHOOSE_IMAGE} or
     * {@link ChooserType.REQUEST_TAKE_PICTURE}
     * 
     * @param activity
     * @param type
     */
    public ImageChooserManager(Activity activity, int type) {
        this.activity = activity;
        this.type = type;
        this.foldername = DIRECTORY;
        this.shouldCreateThumbnails = true;
    }

    /**
     * Specify the type {@link ChooserType.REQUEST_CHOOSE_IMAGE} or
     * {@link ChooserType.REQUEST_TAKE_PICTURE}
     * <p>
     * Optionally, you can control where the exported images with their
     * thumbnails would be stored.
     * </p>
     * 
     * @param activity
     * @param type
     * @param foldername
     */
    public ImageChooserManager(Activity activity, int type, String foldername) {
        this.activity = activity;
        this.type = type;
        this.foldername = foldername;
        this.shouldCreateThumbnails = true;
    }

    /**
     * Specify the type {@link ChooserType.REQUEST_CHOOSE_IMAGE} or
     * {@link ChooserType.REQUEST_TAKE_PICTURE}
     * <p>
     * Optionally, you can set whether you need thumbnail generation or not. If
     * not, you would get the original image for the thumbnails as well
     * </p>
     * 
     * @param activity
     * @param type
     * @param shouldCreateThumbnails
     */
    public ImageChooserManager(Activity activity, int type, boolean shouldCreateThumbnails) {
        this.activity = activity;
        this.type = type;
        this.foldername = DIRECTORY;
        this.shouldCreateThumbnails = shouldCreateThumbnails;
    }

    /**
     * Specify the type {@link ChooserType.REQUEST_CHOOSE_IMAGE} or
     * {@link ChooserType.REQUEST_TAKE_PICTURE}
     * <p>
     * Specify your own foldername and whether you want the generated thumbnails
     * or not
     * </p>
     * 
     * @param activity
     * @param type
     * @param foldername
     * @param shouldCreateThumbnails
     */
    public ImageChooserManager(Activity activity, int type, String foldername,
            boolean shouldCreateThumbnails) {
        this.activity = activity;
        this.type = type;
        this.foldername = foldername;
        this.shouldCreateThumbnails = shouldCreateThumbnails;
    }

    /**
     * Set a listener, to get callbacks when the images and the thumbnails are
     * processed
     * 
     * @param listener
     */
    public void setImageChooserListener(ImageChooserListener listener) {
        this.listener = listener;
    }

    /**
     * Call this method, to start the chooser, i.e, The camera app or the
     * gallery depending upon the type
     * 
     * @throws IllegalAccessException
     */
    public void choose() throws IllegalAccessException {
        if (listener == null) {
            throw new IllegalArgumentException(
                    "ImageChooserListener cannot be null. Forgot to set ImageChooserListener???");
        }
        switch (type) {
            case ChooserType.REQUEST_CHOOSE_IMAGE:
                choosePicture();
                break;
            case ChooserType.REQUEST_TAKE_PICTURE:
                takePicture();
                break;
        }
    }

    private void choosePicture() {
        checkDirectory();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivity(intent);
    }

    private void takePicture() {
        checkDirectory();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(FileUtils.getDirectory(foldername) + File.separator
                        + Calendar.getInstance().getTimeInMillis() + ".jpg")));
        startActivity(intent);
    }

    private void startActivity(Intent intent) {
        activity.startActivityForResult(intent, type);
    }

    private void checkDirectory() {
        File directory = null;
        directory = new File(FileUtils.getDirectory(foldername));
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    /**
     * Call this method to process the result from within your onActivityResult
     * method. You don't need to do any processing at all. Just pass in the
     * request code and the data, and everything else will be taken care of.
     * 
     * @param requestCode
     * @param data
     */
    public void submit(int requestCode, Intent data) {
        switch (type) {
            case ChooserType.REQUEST_CHOOSE_IMAGE:
                processImageFromGallery(data);
                break;
            case ChooserType.REQUEST_TAKE_PICTURE:
                processCameraImage();
                break;
        }
    }

    private void processImageFromGallery(Intent data) {
        if (data != null && data.getDataString() != null) {
            String uri = data.getData().toString();
            if (Config.DEBUG) {
                Log.i(TAG, "Got : " + uri);
            }
            if (uri.startsWith("content:")) {
                filePathOriginal = getAbsoluteImagePathFromUri(Uri.parse(data.getDataString()));
            }
            if (uri.startsWith("file://")) {
                filePathOriginal = data.getDataString().substring(7);
            }
            if (filePathOriginal == null || TextUtils.isEmpty(filePathOriginal)) {
                onError("File path was null");
            } else {
                if (Config.DEBUG) {
                    Log.i(TAG, "File: " + filePathOriginal);
                }
                String path = filePathOriginal;
                ImageProcessorThread thread = new ImageProcessorThread(path, foldername,
                        shouldCreateThumbnails);
                thread.setListener(this);
                thread.setContext(activity.getApplicationContext());
                thread.start();
            }
        }

    }

    private void processCameraImage() {
        String path = filePathOriginal;
        ImageProcessorThread thread = new ImageProcessorThread(path, foldername,
                shouldCreateThumbnails);
        thread.setListener(this);
        thread.start();
    }

    @Override
    public void onProcessedImage(ChosenImage image) {
        if (listener != null) {
            listener.onImageChosen(image);
        }
    }

    @Override
    public void onError(String reason) {
        if (listener != null) {
            listener.onError(reason);
        }
    }

    private String getAbsoluteImagePathFromUri(Uri imageUri) {
        String[] proj = {
                MediaColumns.DATA, MediaColumns.DISPLAY_NAME
        };

        if (imageUri.toString().startsWith("content://com.android.gallery3d.provider")) {
            imageUri = Uri.parse(imageUri.toString().replace("com.android.gallery3d",
                    "com.google.android.gallery3d"));
        }
        Cursor cursor = activity.getContentResolver().query(imageUri, proj, null, null, null);

        cursor.moveToFirst();

        String filePath = "";
        if (imageUri.toString().startsWith("content://com.google.android.gallery3d")) {
            filePath = imageUri.toString();
        } else {
            filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.DATA));
        }
        cursor.close();

        return filePath;
    }
}
