
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
import com.beanie.imagechooser.threads.VideoProcessorListener;
import com.beanie.imagechooser.threads.VideoProcessorThread;

public class VideoChooserManager extends BChooser implements VideoProcessorListener {
    private final static String TAG = "VideoChooserManager";

    private final static String DIRECTORY = "bvideochooser";

    private VideoChooserListener listener;

    public VideoChooserManager(Activity activity, int type) {
        super(activity, type, DIRECTORY, true);
    }

    public VideoChooserManager(Activity activity, int type, String foldername) {
        super(activity, type, foldername, true);
    }

    public VideoChooserManager(Activity activity, int type, boolean shouldCreateThumbnails) {
        super(activity, type, DIRECTORY, shouldCreateThumbnails);
    }

    public VideoChooserManager(Activity activity, int type, String foldername,
            boolean shouldCreateThumbnails) {
        super(activity, type, foldername, shouldCreateThumbnails);
    }

    public void setVideoChooserListener(VideoChooserListener listener) {
        this.listener = listener;
    }

    @Override
    public void choose() throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException(
                    "ImageChooserListener cannot be null. Forgot to set ImageChooserListener???");
        }
        switch (type) {
            case ChooserType.REQUEST_CAPTURE_VIDEO:
                captureVideo();
                break;
            case ChooserType.REQUEST_PICK_VIDEO:
                pickVideo();
                break;
            default:
                throw new IllegalArgumentException("Cannot choose an image in VideoChooserManager");
        }
    }

    private void captureVideo() {
        checkDirectory();
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        filePathOriginal = FileUtils.getDirectory(foldername) + File.separator
                + Calendar.getInstance().getTimeInMillis() + ".mp4";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePathOriginal)));
        startActivity(intent);
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
                VideoProcessorThread thread = new VideoProcessorThread(path, foldername,
                        shouldCreateThumbnails);
                thread.setListener(this);
                thread.setContext(activity.getApplicationContext());
                thread.start();
            }
        }
    }

    private void processCameraVideo(Intent intent) {
        String path = filePathOriginal;
        VideoProcessorThread thread = new VideoProcessorThread(path, foldername,
                shouldCreateThumbnails);
        thread.setListener(this);
        thread.start();
    }

    @Override
    public void onProcessedVideo(ChosenVideo video) {
        if (listener != null) {
            listener.onChosenVideo(video);
        }
    }

    @Override
    public void onError(String reason) {
        if (listener != null) {
            listener.onError(reason);
        }
    }
}
