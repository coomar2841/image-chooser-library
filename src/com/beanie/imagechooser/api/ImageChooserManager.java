
package com.beanie.imagechooser.api;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.beanie.imagechooser.api.config.Config;
import com.beanie.imagechooser.threads.ImageProcessorListener;
import com.beanie.imagechooser.threads.ImageProcessorThread;

public class ImageChooserManager implements ImageProcessorListener {
    private final static String TAG = "ImageChooserManager";

    public final static String KEY_FILE_ORIGINAL = "key_file_original";

    public final static String KEY_THUMB_BIG = "key_thumb_big";

    public final static String KEY_THUMB_SMALL = "key_thumb_small";

    public final static String MY_DIR = "bimagechooser";

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
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivity(intent);
    }

    private void takePicture() {
        checkDirectory();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(getDirectory() + File.separator
                        + Calendar.getInstance().getTimeInMillis() + ".jpg")));
        startActivity(intent);
    }

    private void startActivity(Intent intent) {
        activity.startActivityForResult(intent, type);
    }

    private void checkDirectory() {
        File directory = null;

        directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + MY_DIR);

        if (!directory.exists()) {
            directory.mkdir();
        }
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
        if (data != null && data.getDataString() != null) {
            if (data.getDataString().startsWith("content:")) {
                filePathOriginal = getAbsoluteImagePathFromUri(Uri.parse(data.getDataString()));
            }
            if (data.getDataString().startsWith("file://")) {
                filePathOriginal = data.getDataString().substring(7);
            }
            if (filePathOriginal == null || TextUtils.isEmpty(filePathOriginal)) {
                onError("File path was null");
            } else {
                if (Config.DEBUG) {
                    Log.i(TAG, "File: " + filePathOriginal);
                }
                String path = filePathOriginal;
                ImageProcessorThread thread = new ImageProcessorThread(path);
                thread.setListener(this);
                thread.start();
            }
        }

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

    private String getAbsoluteImagePathFromUri(Uri imageUri) {
        String[] proj = {
            MediaStore.Images.Media.DATA
        };
        Cursor cursor = activity.getContentResolver().query(imageUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String filePath = cursor.getString(column_index);

        cursor.close();

        return filePath;
    }

    public static String getDirectory() {
        File directory = null;
        directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + MY_DIR);
        return directory.getAbsolutePath();
    }
}
