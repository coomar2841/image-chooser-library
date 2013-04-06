
package com.beanie.imagechooser.threads;

import java.io.IOException;

import android.content.Context;
import android.text.TextUtils;

import com.beanie.imagechooser.api.ChosenVideo;

public class VideoProcessorThread extends MediaProcessorThread {
    private final static String TAG = "VideoProcessorThread";

    private VideoProcessorListener listener;

    private final static int MAX_DIRECTORY_SIZE = 5 * 1024 * 1024;

    private final static int MAX_THRESHOLD_DAYS = (int) (0.5 * 24 * 60 * 60 * 1000);

    public VideoProcessorThread(String filePath, String foldername, boolean shouldCreateThumbnails) {
        super(filePath, foldername, shouldCreateThumbnails);
    }

    public void setListener(VideoProcessorListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            manageDiretoryCache(MAX_DIRECTORY_SIZE, MAX_THRESHOLD_DAYS);
            processVideo();
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        }
    }

    private void processVideo() throws IOException {
        if (filePath == null || TextUtils.isEmpty(filePath)) {
            if (listener != null) {
                listener.onError("Coulnd't process a null file");
            }
        } else if (filePath.startsWith("http")) {
            downloadAndProcess(filePath);
        } else if (filePath.startsWith("content://com.google.android.gallery3d")) {
            // processPicasaImage(filePath);
        } else {
            process();
        }
    }

    @Override
    protected void processingDone(String original, String thumbnail, String thunbnailSmall) {
        if (listener != null) {
            ChosenVideo video = new ChosenVideo();
            video.setVideoFilePath(original);
            video.setThumbnailPath(thumbnail);
            video.setThumbnailSmallPath(thunbnailSmall);
            listener.onProcessedVideo(video);
        }
    }
}
