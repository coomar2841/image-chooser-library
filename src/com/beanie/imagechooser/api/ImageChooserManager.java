
package com.beanie.imagechooser.api;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.beanie.imagechooser.threads.ImageProcessorListener;
import com.beanie.imagechooser.threads.ImageProcessorThread;

public class ImageChooserManager implements ImageProcessorListener {
    public final static String KEY_FILE_ORIGINAL = "key_file_original";

    public final static String KEY_THUMB_BIG = "key_thumb_big";

    public final static String KEY_THUMB_SMALL = "key_thumb_small";

    private final static String MY_DIR = "bimagechooser";

    private String filePathOriginal;

    private ImageChooserListener listener;

    private Activity activity;

    private int type;

    public ImageChooserManager(Activity activity, int type) {
        this.activity = activity;
        this.type = type;
    }

    public void setImageChooserListener(ImageChooserListener listener) {
        this.listener = listener;
    }

    public void choose() throws IllegalAccessException {
        if (listener == null) {
            throw new IllegalArgumentException(
                    "ImageChooserListener cannot be null. Forgot to set ImageChooserListener???");
        }
        switch (type) {
            case ChooserType.REQUEST_CHOOSE_IMAGE:
                choosePicture();
                break;
            case ChooserType.REQUEST_TAKE_PICTURE:
                takePicture();
                break;
        }
    }

    private void choosePicture() {
        checkDirectory();
        Intent intent = new Intent();
        startActivity(intent);
    }

    private void takePicture() {
        checkDirectory();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePathOriginal)));
        startActivity(intent);
    }

    private void startActivity(Intent intent) {
        activity.startActivityForResult(intent, type);
    }

    private void checkDirectory() {
        File directory = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + MY_DIR);
        } else {
            directory = new File(Environment.getDataDirectory() + File.separator
                    + activity.getPackageName() + File.separator + MY_DIR);
        }

        if (!directory.exists()) {
            directory.mkdir();
        }

        filePathOriginal = new File(directory.getAbsolutePath() + File.separator
                + Calendar.getInstance().getTimeInMillis() + ".jpg").getAbsolutePath();
    }

    public void submit(int requestCode, Intent data) {
        switch (type) {
            case ChooserType.REQUEST_CHOOSE_IMAGE:
                processImageFromGallery(data);
                break;
            case ChooserType.REQUEST_TAKE_PICTURE:
                processCameraImage();
                break;
        }
    }

    private void processImageFromGallery(Intent data) {

    }

    private void processCameraImage() {
        String path = filePathOriginal;
        ImageProcessorThread thread = new ImageProcessorThread(path);
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
