
package com.beanie.imagechooser.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import com.beanie.imagechooser.api.ChosenImage;
import com.beanie.imagechooser.api.config.Config;

public class ImageProcessorThread extends Thread {
    private final static String TAG = "ImageProcessorThread";

    private ImageProcessorListener listener;

    private String filePath;

    private int THUMBNAIL_BIG = 1;

    private int THUMBNAIL_SMALL = 2;

    public ImageProcessorThread(String filePath) {
        this.filePath = filePath;
    }

    public void setListener(ImageProcessorListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        processImage();
    }

    private void processImage() {
        if (filePath == null || TextUtils.isEmpty(filePath)) {
            if (listener != null) {
                listener.onError("Coulnd't process a null file");
            }
        } else if (filePath.startsWith("http")) {
            downloadAndProcess(filePath);
        } else {
            process();
        }
    }

    private void downloadAndProcess(String url) {

        process();
    }

    private void process() {

        String[] thumbnails = createThumbnails();
        processingDone(this.filePath, thumbnails[0], thumbnails[1]);
    }

    private void processingDone(String original, String thumbnail, String thunbnailSmall) {
        if (listener != null) {
            ChosenImage image = new ChosenImage();
            image.setFilePathOriginal(original);
            image.setFileThumbnail(thumbnail);
            image.setFileThumbnailSmall(thunbnailSmall);
            listener.onProcessedImage(image);
        }
    }

    private String[] createThumbnails() {
        String[] images = new String[2];
        images[0] = getThumnailPath();
        images[1] = getThumbnailSmallPath();
        return images;
    }

    private String getThumnailPath() {
        if (Config.DEBUG) {
            Log.i(TAG, "Compressing ... THUMBNAIL");
        }
        return compressAndSave(THUMBNAIL_BIG);
    }

    private String getThumbnailSmallPath() {
        if (Config.DEBUG) {
            Log.i(TAG, "Compressing ... THUMBNAIL SMALL");
        }
        return compressAndSave(THUMBNAIL_SMALL);
    }

    private String compressAndSave(int scale) {
        try {
            ExifInterface exif = new ExifInterface(filePath);
            String width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String length = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            if (Config.DEBUG) {
                Log.i(TAG, "Before: " + width + "x" + length);
            }

            int w = Integer.parseInt(width);
            int l = Integer.parseInt(length);

            int what = w > l ? w : l;

            Options options = new Options();
            if (what > 1500) {
                options.inSampleSize = scale * 4;
            } else if (what > 1000 && what <= 1500) {
                options.inSampleSize = scale * 3;
            } else if (what > 400 && what <= 1000) {
                options.inSampleSize = scale * 2;
            } else {
                options.inSampleSize = scale;
            }
            if (Config.DEBUG) {
                Log.i(TAG, "Scale: " + (what / options.inSampleSize));
            }
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            File original = new File(filePath);
            File file = new File(
                    (original.getParent() + File.separator + original.getName()).replace(".",
                            "_fact_" + scale + "."));
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            if (Config.DEBUG) {
                ExifInterface exifAfter = new ExifInterface(file.getAbsolutePath());
                String widthAfter = exifAfter.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                String lengthAfter = exifAfter.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                if (Config.DEBUG) {
                    Log.i(TAG, "After: " + widthAfter + "x" + lengthAfter);
                }
            }
            stream.flush();
            stream.close();
            return file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
