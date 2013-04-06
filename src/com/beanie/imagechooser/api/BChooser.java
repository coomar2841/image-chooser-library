
package com.beanie.imagechooser.api;

import java.io.File;

import android.app.Activity;
import android.content.Intent;

public abstract class BChooser {
    protected Activity activity;

    protected int type;

    protected String foldername;

    protected boolean shouldCreateThumbnails;

    protected String filePathOriginal;

    public BChooser(Activity activity, int type, String foldername, boolean shouldCreateThumbnails) {
        this.activity = activity;
        this.type = type;
        this.foldername = foldername;
        this.shouldCreateThumbnails = shouldCreateThumbnails;
    }

    public abstract void choose() throws IllegalArgumentException;
    public abstract void submit(int requestCode, Intent data);

    protected void checkDirectory() {
        File directory = null;
        directory = new File(FileUtils.getDirectory(foldername));
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    protected void startActivity(Intent intent) {
        activity.startActivityForResult(intent, type);
    }
}
