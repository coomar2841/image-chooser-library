
package com.beanie.imagechooser.api;

import android.app.Activity;
import android.content.Intent;

public class VideoChooserManager extends BChooser {
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
    public void choose() throws IllegalAccessException {
        
    }

    @Override
    public void submit(int requestCode, Intent data) {
        // TODO Auto-generated method stub
        
    }
}
