/*******************************************************************************
 * Copyright 2013 Kumar Bibek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    
 * http://www.apache.org/licenses/LICENSE-2.0
 * 	
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.beanie.imagechooser.threads;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.beanie.imagechooser.api.ChosenImage;
import com.beanie.imagechooser.api.FileUtils;
import com.beanie.imagechooser.api.config.Config;

public class ImageProcessorThread extends Thread {
    private final static String TAG = "ImageProcessorThread";

    private ImageProcessorListener listener;

    private final static int MAX_DIRECTORY_SIZE = 5 * 1024 * 1024;

    private final static int MAX_THRESHOLD_DAYS = (int) (0.5 * 24 * 60 * 60 * 1000);

    private String filePath;

    private int THUMBNAIL_BIG = 1;

    private int THUMBNAIL_SMALL = 2;

    private Context context;

    private String foldername;

    private boolean shouldCreateThumnails;

    public ImageProcessorThread(String filePath, String foldername, boolean shouldCreateThumbnails) {
        this.filePath = filePath;
        this.foldername = foldername;
        this.shouldCreateThumnails = shouldCreateThumbnails;
    }

    public void setListener(ImageProcessorListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            manageDiretoryCache();
            processImage();
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        }
    }

    private void processImage() throws IOException {
        if (filePath == null || TextUtils.isEmpty(filePath)) {
            if (listener != null) {
                listener.onError("Coulnd't process a null file");
            }
        } else if (filePath.startsWith("http")) {
            downloadAndProcess(filePath);
        } else if (filePath.startsWith("content://com.google.android.gallery3d")) {
            processPicasaImage(filePath);
        } else {
            process();
        }
    }

    private void downloadAndProcess(String url) throws IOException {
        filePath = downloadImage(url);
        process();
    }

    private void process() throws IOException {
        if (!filePath.contains(foldername)) {
            copyFileToDir();
        }
        if (shouldCreateThumnails) {
            String[] thumbnails = createThumbnails();
            processingDone(this.filePath, thumbnails[0], thumbnails[1]);
        } else {
            processingDone(this.filePath, this.filePath, this.filePath);
        }
    }

    private void copyFileToDir() throws IOException {
        try {
            File file;
            file = new File(Uri.parse(filePath).getPath());
            File copyTo = new File(FileUtils.getDirectory(foldername) + File.separator
                    + file.getName());
            FileInputStream streamIn = new FileInputStream(file);
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(copyTo));
            byte[] buf = new byte[2048];
            int len;
            while ((len = streamIn.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            streamIn.close();
            outStream.close();
            filePath = copyTo.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
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

    public String downloadImage(String url) {
        String localFilePath = "";
        HttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            InputStream stream = response.getEntity().getContent();

            localFilePath = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + ".jpg";
            File localFile = new File(localFilePath);

            // stream to write to file
            FileOutputStream fileOutputStream = new FileOutputStream(localFile);

            // buffer to hold bytes we read from the input stream
            byte[] buffer = new byte[1024];

            // read from input stream, write to file
            int len;
            while ((len = stream.read(buffer)) > 0)
                fileOutputStream.write(buffer, 0, len);
            fileOutputStream.flush();
            fileOutputStream.close();
            stream.close();

            if (Config.DEBUG) {
                Log.i(TAG, "Image saved: " + localFilePath.toString());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localFilePath;
    }

    private void processPicasaImage(String filePath) throws IOException {
        if (Config.DEBUG) {
            Log.i(TAG, "Picasa Started");
        }
        String imageUri = filePath;
        try {
            Bitmap tempBitmap = BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(Uri.parse(imageUri)));

            this.filePath = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + ".jpg";

            FileOutputStream stream = new FileOutputStream(this.filePath);
            tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            tempBitmap.recycle();

            process();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        if (Config.DEBUG) {
            Log.i(TAG, "Picasa Done");
        }
    }

    private void manageDiretoryCache() {
        File directory = null;
        directory = new File(FileUtils.getDirectory(foldername));
        File[] files = directory.listFiles();
        long count = 0;
        for (File file : files) {
            count = count + file.length();
        }
        if (Config.DEBUG) {
            Log.i(TAG, "Directory size: " + count);
        }

        if (count > MAX_DIRECTORY_SIZE) {
            final long today = Calendar.getInstance().getTimeInMillis();
            FileFilter filter = new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    if (today - pathname.lastModified() > MAX_THRESHOLD_DAYS) {
                        return false;
                    } else {
                        return true;
                    }
                }
            };

            File[] filterFiles = directory.listFiles(filter);
            for (File file : filterFiles) {
                file.delete();
            }
        }
    }
}
