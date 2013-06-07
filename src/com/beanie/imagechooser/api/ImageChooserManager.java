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
import android.net.Uri;
import android.provider.MediaStore;
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
public class ImageChooserManager extends BChooser implements ImageProcessorListener {
    private final static String TAG = "ImageChooserManager";

    private final static String DIRECTORY = "bimagechooser";

    private ImageChooserListener listener;

    /**
     * Simplest constructor. Specify the type
     * {@link ChooserType.REQUEST_PICK_PICTURE} or
     * {@link ChooserType.REQUEST_CAPTURE_PICTURE}
     * 
     * @param activity
     * @param type
     */
    public ImageChooserManager(Activity activity, int type) {
        super(activity, type, DIRECTORY, true);
    }

    /**
     * Specify the type {@link ChooserType.REQUEST_PICK_PICTURE} or
     * {@link ChooserType.REQUEST_CAPTURE_PICTURE}
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
        super(activity, type, foldername, true);
    }

    /**
     * Specify the type {@link ChooserType.REQUEST_PICK_PICTURE} or
     * {@link ChooserType.REQUEST_CAPTURE_PICTURE}
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
        super(activity, type, DIRECTORY, shouldCreateThumbnails);
    }

    /**
     * Specify the type {@link ChooserType.REQUEST_PICK_PICTURE} or
     * {@link ChooserType.REQUEST_CAPTURE_PICTURE}
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
        super(activity, type, foldername, shouldCreateThumbnails);
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

    @Override
    public String choose() throws IllegalArgumentException {
        String path = null;
        if (listener == null) {
            throw new IllegalArgumentException(
                    "ImageChooserListener cannot be null. Forgot to set ImageChooserListener???");
        }
        switch (type) {
            case ChooserType.REQUEST_PICK_PICTURE:
                choosePicture();
                break;
            case ChooserType.REQUEST_CAPTURE_PICTURE:
                path = takePicture();
                break;
            default:
                throw new IllegalArgumentException("Cannot choose a video in ImageChooserManager");
        }
        return path;
    }

    private void choosePicture() {
        checkDirectory();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivity(intent);
    }

    private String takePicture() {
        checkDirectory();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        filePathOriginal = FileUtils.getDirectory(foldername) + File.separator
                + Calendar.getInstance().getTimeInMillis() + ".jpg";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePathOriginal)));
        startActivity(intent);
        return filePathOriginal;
    }

    @Override
    public void submit(int requestCode, Intent data) {
        switch (type) {
            case ChooserType.REQUEST_PICK_PICTURE:
                processImageFromGallery(data);
                break;
            case ChooserType.REQUEST_CAPTURE_PICTURE:
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
            // Picasa on Android >= 3.0
            if (uri.startsWith("content:")) {
                filePathOriginal = getAbsoluteImagePathFromUri(Uri.parse(data.getDataString()));
            }
            // Picasa on Android < 3.0
            if (uri.matches("https?://\\w+\\.googleusercontent\\.com/.+")) {
                filePathOriginal = uri;
            }
            // Local storage
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
}
