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
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.beanie.imagechooser.api.config.Config;
import com.beanie.imagechooser.threads.VideoProcessorListener;
import com.beanie.imagechooser.threads.VideoProcessorThread;

/**
 * Easy Image Chooser Library for Android Apps. Forget about coding workarounds
 * for different devices, OSes and folders.
 * 
 * @author Beanie
 */
public class VideoChooserManager extends BChooser implements VideoProcessorListener {
    private final static String TAG = "VideoChooserManager";

    private final static String DIRECTORY = "bvideochooser";

    private VideoChooserListener listener;

    /**
     * Simplest constructor. Specify the type
     * {@link ChooserType.REQUEST_CHOOSE_IMAGE} or
     * {@link ChooserType.REQUEST_TAKE_PICTURE}
     * 
     * @param activity
     * @param type
     */
    public VideoChooserManager(Activity activity, int type) {
        super(activity, type, DIRECTORY, true);
    }

    public VideoChooserManager(Fragment fragment, int type) {
        super(fragment, type, DIRECTORY, true);
    }

    public VideoChooserManager(android.app.Fragment fragment, int type) {
        super(fragment, type, DIRECTORY, true);
    }

    public VideoChooserManager(Activity activity, int type, String foldername) {
        super(activity, type, foldername, true);
    }

    public VideoChooserManager(Fragment fragment, int type, String foldername) {
        super(fragment, type, foldername, true);
    }

    public VideoChooserManager(android.app.Fragment fragment, int type, String foldername) {
        super(fragment, type, foldername, true);
    }

    public VideoChooserManager(Activity activity, int type, boolean shouldCreateThumbnails) {
        super(activity, type, DIRECTORY, shouldCreateThumbnails);
    }

    public VideoChooserManager(Fragment fragment, int type, boolean shouldCreateThumbnails) {
        super(fragment, type, DIRECTORY, shouldCreateThumbnails);
    }

    public VideoChooserManager(android.app.Fragment fragment, int type,
            boolean shouldCreateThumbnails) {
        super(fragment, type, DIRECTORY, shouldCreateThumbnails);
    }

    public VideoChooserManager(Activity activity, int type, String foldername,
            boolean shouldCreateThumbnails) {
        super(activity, type, foldername, shouldCreateThumbnails);
    }

    public VideoChooserManager(Fragment fragment, int type, String foldername,
            boolean shouldCreateThumbnails) {
        super(fragment, type, foldername, shouldCreateThumbnails);
    }

    public VideoChooserManager(android.app.Fragment fragment, int type, String foldername,
            boolean shouldCreateThumbnails) {
        super(fragment, type, foldername, shouldCreateThumbnails);
    }

    /**
     * Set a listener, to get callbacks when the videos and the thumbnails are
     * processed
     * 
     * @param listener
     */
    public void setVideoChooserListener(VideoChooserListener listener) {
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
            case ChooserType.REQUEST_CAPTURE_VIDEO:
                path = captureVideo();
                break;
            case ChooserType.REQUEST_PICK_VIDEO:
                pickVideo();
                break;
            default:
                throw new IllegalArgumentException("Cannot choose an image in VideoChooserManager");
        }
        return path;
    }

    private String captureVideo() {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk >= Build.VERSION_CODES.GINGERBREAD && sdk <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            return captureVideoPatchedMethodForGingerbread();
        } else {
            return captureVideoCurrent();
        }
    }

    private String captureVideoCurrent() {
        checkDirectory();
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        filePathOriginal = FileUtils.getDirectory(foldername) + File.separator
                + Calendar.getInstance().getTimeInMillis() + ".mp4";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePathOriginal)));
        startActivity(intent);
        return filePathOriginal;
    }

    private String captureVideoPatchedMethodForGingerbread() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivity(intent);
        return null;
    }

    private void pickVideo() {
        checkDirectory();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivity(intent);
    }

    @Override
    public void submit(int requestCode, Intent data) {
        switch (type) {
            case ChooserType.REQUEST_PICK_VIDEO:
                processVideoFromGallery(data);
                break;
            case ChooserType.REQUEST_CAPTURE_VIDEO:
                processCameraVideo(data);
                break;
        }
    }

    private void processVideoFromGallery(Intent data) {
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
                VideoProcessorThread thread = new VideoProcessorThread(path, foldername,
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
        }
    }

    private void processCameraVideo(Intent intent) {
        String path = null;
        int sdk = Build.VERSION.SDK_INT;
        if (sdk >= Build.VERSION_CODES.GINGERBREAD && sdk <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            path = intent.getDataString();
        } else {
            path = filePathOriginal;
        }
        VideoProcessorThread thread = new VideoProcessorThread(path, foldername,
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

    @Override
    public void onProcessedVideo(ChosenVideo video) {
        if (listener != null) {
            listener.onVideoChosen(video);
        }
    }

    @Override
    public void onError(String reason) {
        if (listener != null) {
            listener.onError(reason);
        }
    }
}
