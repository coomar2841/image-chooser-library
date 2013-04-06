
package com.beanie.imagechooser.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;

import com.beanie.imagechooser.api.ChosenVideo;
import com.beanie.imagechooser.api.FileUtils;

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
            manageDiretoryCache(MAX_DIRECTORY_SIZE, MAX_THRESHOLD_DAYS, "mp4");
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
            processPicasaMedia(filePath, ".mp4");
        } else {
            process();
        }
    }

    @Override
    protected void process() throws IOException {
        super.process();
        if (shouldCreateThumnails) {
            String[] thumbnails = createThumbnails(createThumbnailOfVideo());
            processingDone(this.filePath, thumbnails[0], thumbnails[1]);
        } else {
            processingDone(this.filePath, this.filePath, this.filePath);
        }
    }

    private String createThumbnailOfVideo() throws IOException {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
        String thumbnailPath = FileUtils.getDirectory(foldername) + File.separator
                + Calendar.getInstance().getTimeInMillis() + ".jpg";
        File file = new File(thumbnailPath);
        FileOutputStream stream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        stream.flush();
        return thumbnailPath;
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
