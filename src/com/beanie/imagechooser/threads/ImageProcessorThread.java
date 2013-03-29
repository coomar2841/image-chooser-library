
package com.beanie.imagechooser.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.beanie.imagechooser.api.ChosenImage;

public class ImageProcessorThread extends Thread {
    private ImageProcessorListener listener;

    private String filePath;

    private String directory;

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
        return compressAndSave(4);
    }

    private String getThumbnailSmallPath() {
        return compressAndSave(16);
    }

    private String compressAndSave(int factor) {
        try {
            ExifInterface exif = new ExifInterface(filePath);
            String width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String length = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);

            int w = Integer.parseInt(width);
            int l = Integer.parseInt(length);

            if (w > 200 || l > 200) {
                l = 200 * l / w;
                w = 200;
                Options options = new Options();
                if (w > 1500) {
                    options.inSampleSize = factor / 4;
                } else {
                    options.inSampleSize = factor;
                }
                options.outHeight = l;
                options.outWidth = w;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                File original = new File(filePath);
                File file = new File(
                        (original.getParent() + File.separator + original.getName()).replace(".",
                                "fact_" + factor + "."));
                FileOutputStream stream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                return file.getAbsolutePath();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
