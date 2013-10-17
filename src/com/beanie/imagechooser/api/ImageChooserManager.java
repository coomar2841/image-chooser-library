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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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

    public ImageChooserManager(Fragment fragment, int type) {
        super(fragment, type, DIRECTORY, true);
    }

    public ImageChooserManager(android.app.Fragment fragment, int type) {
        super(fragment, type, DIRECTORY, true);
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

    public ImageChooserManager(Fragment fragment, int type, String foldername) {
        super(fragment, type, foldername, true);
    }

    public ImageChooserManager(android.app.Fragment fragment, int type, String foldername) {
        super(fragment, type, foldername, true);
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

    public ImageChooserManager(Fragment fragment, int type, boolean shouldCreateThumbnails) {
        super(fragment, type, DIRECTORY, shouldCreateThumbnails);
    }

    public ImageChooserManager(android.app.Fragment fragment, int type,
            boolean shouldCreateThumbnails) {
        super(fragment, type, DIRECTORY, shouldCreateThumbnails);
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

    public ImageChooserManager(Fragment fragment, int type, String foldername,
            boolean shouldCreateThumbnails) {
        super(fragment, type, foldername, shouldCreateThumbnails);
    }

    public ImageChooserManager(android.app.Fragment fragment, int type, String foldername,
            boolean shouldCreateThumbnails) {
        super(fragment, type, foldername, shouldCreateThumbnails);
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
    public String choose() throws Exception {
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

    private void choosePicture() throws Exception {
        checkDirectory();
        try {
			Intent intent = new Intent(Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            throw new Exception("Activity not found");
        }
    }

    private String takePicture() throws Exception {
        checkDirectory();
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            filePathOriginal = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + ".jpg";
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePathOriginal)));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            throw new Exception("Activity not found");
        }
        return filePathOriginal;
    }

    @Override
    public void submit(int requestCode, Intent data) {
        if (requestCode != type) {
            onError("onActivityResult requestCode is different from the type the chooser was initialized with.");
        } else {
            switch (requestCode) {
                case ChooserType.REQUEST_PICK_PICTURE:
                    processImageFromGallery(data);
                    break;
                case ChooserType.REQUEST_CAPTURE_PICTURE:
                    processCameraImage();
                    break;
            }
        }
    }

    @SuppressLint("NewApi")
	public void processImageUri(String _uri) {
        if (_uri != null) {
            if (Config.DEBUG) {
                Log.i(TAG, "Got : " + _uri);
            }
            // Picasa on Android >= 3.0
            if (_uri.startsWith("content:")) {
                filePathOriginal = getAbsoluteImagePathFromUri(Uri.parse(_uri));
            }
            // Picasa on Android < 3.0
            if (_uri.matches("https?://\\w+\\.googleusercontent\\.com/.+")) {
                filePathOriginal = _uri;
            }
            // Local storage
            if (_uri.startsWith("file://")) {
                filePathOriginal = _uri.substring(7);
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
                if (activity != null) {
                    thread.setContext(activity.getApplicationContext());
                } else if (fragment != null) {
                    thread.setContext(fragment.getActivity().getApplicationContext());
                } else if (appFragment != null) {
                    thread.setContext(appFragment.getActivity().getApplicationContext());
                }
                thread.start();
            }
        } else {
            onError("Image Uri was null!");
        }
    }

    private void processImageFromGallery(Intent data) {
        if (data != null && data.getDataString() != null) {
            String uri = data.getData().toString();
            processImageUri(uri);
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
