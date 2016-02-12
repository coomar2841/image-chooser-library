/**
 * ****************************************************************************
 * Copyright 2013 Kumar Bibek
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */

package com.kbeanie.imagechooser.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.kbeanie.imagechooser.BuildConfig;
import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.kbeanie.imagechooser.threads.ImageProcessorListener;
import com.kbeanie.imagechooser.threads.ImageProcessorThread;

import java.util.ArrayList;

/**
 * Easy Image Chooser Library for Android Apps. Forget about coding workarounds
 * for different devices, OSes and folders.
 *
 * @author Beanie
 */
public class ImageChooserManager extends BChooser implements
        ImageProcessorListener {

    private final static String TAG = ImageChooserManager.class.getSimpleName();

    private ImageChooserListener listener;

    /**
     * Simplest constructor. Specify the type
     * {@link ChooserType}
     *
     * @param activity
     * @param type
     */
    public ImageChooserManager(Activity activity, int type) {
        super(activity, type, true);
    }

    /**
     * Simple constructor for using with a fragment from the support library
     *
     * @param fragment
     * @param type
     */
    public ImageChooserManager(Fragment fragment, int type) {
        super(fragment, type, true);
    }

    /**
     * Simple constructor for using with a fragment
     *
     * @param fragment
     * @param type
     */
    public ImageChooserManager(android.app.Fragment fragment, int type) {
        super(fragment, type, true);
    }

    /**
     * Specify the type {@link ChooserType}
     * <p>
     * Optionally, you can control where the exported images with their
     * thumbnails would be stored.
     * </p>
     *
     * @param activity
     * @param type
     * @param folderName
     * @deprecated Use BChooserPreferences to set your desired folder name
     */
    @Deprecated
    public ImageChooserManager(Activity activity, int type, String folderName) {
        super(activity, type, folderName, true);
    }

    /**
     * @param fragment
     * @param type
     * @param folderName
     * @deprecated Use BChooserPreferences to set your desired folder name
     */
    @Deprecated
    public ImageChooserManager(Fragment fragment, int type, String folderName) {
        super(fragment, type, folderName, true);
    }

    /**
     * @param fragment
     * @param type
     * @param folderName
     * @deprecated Use BChooserPreferences to set your desired folder name
     */
    @Deprecated
    public ImageChooserManager(android.app.Fragment fragment, int type,
                               String folderName) {
        super(fragment, type, folderName, true);
    }

    /**
     * Specify the type {@link ChooserType}
     * <p>
     * Optionally, you can set whether you need thumbnail generation or not. If
     * not, you would get the original image for the thumbnails as well
     * </p>
     *
     * @param activity
     * @param type
     * @param shouldCreateThumbnails
     */
    public ImageChooserManager(Activity activity, int type,
                               boolean shouldCreateThumbnails) {
        super(activity, type, shouldCreateThumbnails);
    }

    public ImageChooserManager(Fragment fragment, int type,
                               boolean shouldCreateThumbnails) {
        super(fragment, type, shouldCreateThumbnails);
    }

    public ImageChooserManager(android.app.Fragment fragment, int type,
                               boolean shouldCreateThumbnails) {
        super(fragment, type, shouldCreateThumbnails);
    }

    /**
     * Specify the type {@link ChooserType}
     * <p>
     * Specify your own foldername and whether you want the generated thumbnails
     * or not
     * </p>
     *
     * @param activity
     * @param type
     * @param foldername
     * @param shouldCreateThumbnails
     * @deprecated Use BChooserPreferences to set your desired folder name
     */
    @Deprecated
    public ImageChooserManager(Activity activity, int type, String foldername,
                               boolean shouldCreateThumbnails) {
        super(activity, type, foldername, shouldCreateThumbnails);
    }

    /**
     * @param fragment
     * @param type
     * @param foldername
     * @param shouldCreateThumbnails
     * @deprecated Use BChooserPreferences to set your desired folder name
     */
    @Deprecated
    public ImageChooserManager(Fragment fragment, int type, String foldername,
                               boolean shouldCreateThumbnails) {
        super(fragment, type, foldername, shouldCreateThumbnails);
    }

    /**
     * @param fragment
     * @param type
     * @param foldername
     * @param shouldCreateThumbnails
     * @deprecated Use BChooserPreferences to set your desired folder name
     */
    @Deprecated
    public ImageChooserManager(android.app.Fragment fragment, int type,
                               String foldername, boolean shouldCreateThumbnails) {
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
    public String choose() throws ChooserException {
        String path = null;
        if (listener == null) {
            throw new ChooserException(
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
                throw new ChooserException(
                        "Cannot choose a video in ImageChooserManager");
        }
        return path;
    }

    private void choosePicture() throws ChooserException {
        checkDirectory();
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            if (extras != null) {
                intent.putExtras(extras);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            throw new ChooserException(e);
        }
    }

    private String takePicture() throws ChooserException {
        checkDirectory();
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            filePathOriginal = buildFilePathOriginal(foldername, "jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, buildCaptureUri(filePathOriginal));
            if (extras != null) {
                intent.putExtras(extras);
            }
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            throw new ChooserException(e);
        }
        return filePathOriginal;
    }

    @Override
    public void submit(int requestCode, Intent data) {
        try {
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
        } catch (Exception e) {
            onError(e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    private void processImageFromGallery(Intent data) {
        if (data != null && data.getDataString() != null) {
            String uri = data.getData().toString();
            sanitizeURI(uri);

            if (filePathOriginal == null || TextUtils.isEmpty(filePathOriginal)) {
                onError("File path was null");
            } else {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "File: " + filePathOriginal);
                }
                String path = filePathOriginal;
                ImageProcessorThread thread = new ImageProcessorThread(path,
                        foldername, shouldCreateThumbnails);
                thread.clearOldFiles(clearOldFiles);
                thread.setListener(this);
                thread.setContext(getContext());
                thread.start();
            }
        } else if (data.getClipData() != null || data.hasExtra("uris")) {
            // Multiple Images
            String[] filePaths;
            if (data.hasExtra("uris")) {
                ArrayList<Uri> uris = data.getParcelableArrayListExtra("uris");
                filePaths = new String[uris.size()];
                for (int i = 0; i < uris.size(); i++) {
                    filePaths[i] = uris.get(i).toString();
                }
            } else {
                ClipData clipData = data.getClipData();
                int count = clipData.getItemCount();
                filePaths = new String[count];
                for (int i = 0; i < count; i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Log.i(TAG, "processImageFromGallery: Item: " + item.getUri());
                    filePaths[i] = item.getUri().toString();
                }
            }
            ImageProcessorThread thread = new ImageProcessorThread(filePaths, foldername, shouldCreateThumbnails);
            thread.clearOldFiles(clearOldFiles);
            thread.setListener(this);
            thread.setContext(getContext());
            thread.start();
//        } else if () {
        } else {
            onError("Image Uri was null!");
        }
    }

    private void processCameraImage() {
        String path = filePathOriginal;
        ImageProcessorThread thread = new ImageProcessorThread(path,
                foldername, shouldCreateThumbnails);
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

    @Override
    public void onProcessedImages(ChosenImages images) {
        if (listener != null) {
            listener.onImagesChosen(images);
        }
    }
}
