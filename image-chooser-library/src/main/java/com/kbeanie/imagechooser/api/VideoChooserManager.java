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
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.kbeanie.imagechooser.BuildConfig;
import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.kbeanie.imagechooser.threads.ImageProcessorThread;
import com.kbeanie.imagechooser.threads.VideoProcessorListener;
import com.kbeanie.imagechooser.threads.VideoProcessorThread;

import java.util.ArrayList;

/**
 * Easy Image Chooser Library for Android Apps. Forget about coding workarounds
 * for different devices, OSes and folders.
 *
 * @author Beanie
 */
public class VideoChooserManager extends BChooser implements
        VideoProcessorListener {

    private final static String TAG = VideoChooserManager.class.getSimpleName();

    private VideoChooserListener listener;

    /**
     * Simplest constructor. Specify the type
     * {@link ChooserType}
     *
     * @param activity
     * @param type
     */
    public VideoChooserManager(Activity activity, int type) {
        super(activity, type, true);
    }

    public VideoChooserManager(Fragment fragment, int type) {
        super(fragment, type, true);
    }

    public VideoChooserManager(android.app.Fragment fragment, int type) {
        super(fragment, type, true);
    }

    @Deprecated
    public VideoChooserManager(Activity activity, int type, String foldername) {
        super(activity, type, foldername, true);
    }

    @Deprecated
    public VideoChooserManager(Fragment fragment, int type, String foldername) {
        super(fragment, type, foldername, true);
    }

    @Deprecated
    public VideoChooserManager(android.app.Fragment fragment, int type,
                               String foldername) {
        super(fragment, type, foldername, true);
    }

    public VideoChooserManager(Activity activity, int type,
                               boolean shouldCreateThumbnails) {
        super(activity, type, shouldCreateThumbnails);
    }

    public VideoChooserManager(Fragment fragment, int type,
                               boolean shouldCreateThumbnails) {
        super(fragment, type, shouldCreateThumbnails);
    }

    public VideoChooserManager(android.app.Fragment fragment, int type,
                               boolean shouldCreateThumbnails) {
        super(fragment, type, shouldCreateThumbnails);
    }

    @Deprecated
    public VideoChooserManager(Activity activity, int type, String foldername,
                               boolean shouldCreateThumbnails) {
        super(activity, type, foldername, shouldCreateThumbnails);
    }

    @Deprecated
    public VideoChooserManager(Fragment fragment, int type, String foldername,
                               boolean shouldCreateThumbnails) {
        super(fragment, type, foldername, shouldCreateThumbnails);
    }

    @Deprecated
    public VideoChooserManager(android.app.Fragment fragment, int type,
                               String foldername, boolean shouldCreateThumbnails) {
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
    public String choose() throws ChooserException {
        String path = null;
        if (listener == null) {
            throw new ChooserException(
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
                throw new ChooserException(
                        "Cannot choose an image in VideoChooserManager");
        }
        return path;
    }

    private String captureVideo() throws ChooserException {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk >= Build.VERSION_CODES.GINGERBREAD
                && sdk <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            return captureVideoPatchedMethodForGingerbread();
        } else {
            return captureVideoCurrent();
        }
    }

    private String captureVideoCurrent() throws ChooserException {
        checkDirectory();
        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            filePathOriginal = buildFilePathOriginal(foldername, "mp4");
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

    private String captureVideoPatchedMethodForGingerbread() throws ChooserException {
        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (extras != null) {
                intent.putExtras(extras);
            }
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            throw new ChooserException(e);
        }
        return null;
    }

    private void pickVideo() throws ChooserException {
        checkDirectory();
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            if (extras != null) {
                intent.putExtras(extras);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("video/*");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            throw new ChooserException(e);
        }
    }

    @Override
    public void submit(int requestCode, Intent data) {
        try {
            switch (type) {
                case ChooserType.REQUEST_PICK_VIDEO:
                    processVideoFromGallery(data);
                    break;
                case ChooserType.REQUEST_CAPTURE_VIDEO:
                    processCameraVideo(data);
                    break;
            }
        } catch (Exception e) {
            onError(e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    private void processVideoFromGallery(Intent data) {
        if (data != null && data.getDataString() != null && data.getClipData() == null) {
            String uri = data.getData().toString();
            sanitizeURI(uri);
            if (filePathOriginal == null || TextUtils.isEmpty(filePathOriginal)) {
                onError("File path was null");
            } else {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "File: " + filePathOriginal);
                }
                String path = filePathOriginal;
                VideoProcessorThread thread = new VideoProcessorThread(path,
                        foldername, shouldCreateThumbnails);
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
            VideoProcessorThread thread = new VideoProcessorThread(filePaths, foldername, shouldCreateThumbnails);
            thread.clearOldFiles(clearOldFiles);
            thread.setListener(this);
            thread.setContext(getContext());
            thread.start();
//        } else if () {
        } else {
            onError("Image Uri was null!");
        }
    }

    @SuppressLint("NewApi")
    private void processCameraVideo(Intent intent) {
        String path;
        int sdk = Build.VERSION.SDK_INT;
        if (sdk >= Build.VERSION_CODES.GINGERBREAD
                && sdk <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            path = intent.getDataString();
        } else {
            path = filePathOriginal;
        }
        VideoProcessorThread thread = new VideoProcessorThread(path,
                foldername, shouldCreateThumbnails);
        thread.clearOldFiles(clearOldFiles);
        thread.setListener(this);
        thread.setContext(getContext());
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

    @Override
    public void onProcessedVideos(ChosenVideos videos) {
        if (listener != null) {
            listener.onVideosChosen(videos);
        }
    }
}
