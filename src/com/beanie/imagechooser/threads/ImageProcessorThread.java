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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.beanie.imagechooser.api.ChosenImage;
import com.beanie.imagechooser.api.FileUtils;
import com.beanie.imagechooser.api.config.Config;

public class ImageProcessorThread extends MediaProcessorThread {

    private final static String TAG = "ImageProcessorThread";

    private ImageProcessorListener listener;

    private final static int MAX_DIRECTORY_SIZE = 5 * 1024 * 1024;

    private final static int MAX_THRESHOLD_DAYS = (int) (0.5 * 24 * 60 * 60 * 1000);

    public ImageProcessorThread(String filePath, String foldername, boolean shouldCreateThumbnails) {
        super(filePath, foldername, shouldCreateThumbnails);
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
            manageDiretoryCache(MAX_DIRECTORY_SIZE, MAX_THRESHOLD_DAYS);
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
                listener.onError("Couldn't process a null file");
            }
        } else if (filePath.startsWith("http")) {
            downloadAndProcess(filePath);
        } else if (filePath.startsWith("content://com.google.android.gallery3d")) {
            processPicasaImage(filePath);
        } else {
            process();
        }
    }
    
    @Override
    protected void process() throws IOException {
        super.process();
        if (shouldCreateThumnails) {
            String[] thumbnails = createThumbnails(this.filePath);
            processingDone(this.filePath, thumbnails[0], thumbnails[1]);
        } else {
            processingDone(this.filePath, this.filePath, this.filePath);
        }
    }

    @Override
    protected void processingDone(String original, String thumbnail, String thunbnailSmall) {
        if (listener != null) {
            ChosenImage image = new ChosenImage();
            image.setFilePathOriginal(original);
            image.setFileThumbnail(thumbnail);
            image.setFileThumbnailSmall(thunbnailSmall);
            listener.onProcessedImage(image);
        }
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
}
