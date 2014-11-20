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

package com.kbeanie.imagechooser.threads;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import com.kbeanie.imagechooser.api.ChosenFile;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.FileUtils;
import com.kbeanie.imagechooser.api.config.Config;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class FileProcessorThread extends MediaProcessorThread {

    private final static String TAG = "FileProcessorThread";

    private FileProcessorListener listener;

    private final static int MAX_DIRECTORY_SIZE = 5 * 1024 * 1024;

    private final static int MAX_THRESHOLD_DAYS = (int) (0.5 * 24 * 60 * 60 * 1000);

    private ContentResolver cr;

    private String fileDisplayName;

    private long fileSize;

    public FileProcessorThread(String filePath, String foldername,
                               boolean shouldCreateThumbnails) {
        super(filePath, foldername, shouldCreateThumbnails);
    }

    public void setListener(FileProcessorListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
        if (filePath.startsWith("content")) {
            cr = context.getContentResolver();
            Cursor cursor = null;
            String mimeType = cr.getType(Uri.parse(filePath));
            try {
                cursor = cr.query(Uri.parse(filePath), null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    fileDisplayName = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
            if (mimeType.contains("/")) {
                String[] parts = mimeType.split("/");
                setMediaExtension("." + parts[1]);
            } else {
                setMediaExtension("." + mimeType);
            }
        } else if (filePath.startsWith("file")) {
            String extension = "";

            int i = filePath.lastIndexOf('.');
            if (i > 0) {
                extension = filePath.substring(i + 1);
            }
            if (extension != null && !TextUtils.isEmpty(extension)) {
                setMediaExtension("." + extension);
            }
            if (fileDisplayName == null || !TextUtils.isEmpty(fileDisplayName)) {
                File file = new File(filePath);
                fileDisplayName = file.getName();
            }
        }
    }

    @Override
    public void run() {
        try {
            manageDiretoryCache(MAX_DIRECTORY_SIZE, MAX_THRESHOLD_DAYS, mediaExtension);
            processFile();
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        }
    }

    private void processFile() throws Exception {
        if (Config.DEBUG) {
            Log.i(TAG, "Processing File: " + filePath);
        }
        processFileDetails(filePath);
        process();
    }

    protected void processFileDetails(String path)
            throws Exception {
        if (Config.DEBUG) {
            Log.i(TAG, "File Started");
        }
        if (filePath.startsWith("content:")) {
            try {
                Uri uri = Uri.parse(filePath);
                InputStream inputStream = context.getContentResolver()
                        .openInputStream(uri);
                if (fileDisplayName == null) {
                    fileDisplayName = "" + Calendar.getInstance().getTimeInMillis() + mediaExtension;
                }
                if (!fileDisplayName.contains(".") && mediaExtension != null && mediaExtension.length() > 0) {
                    fileDisplayName = fileDisplayName + mediaExtension;
                }
                filePath = FileUtils.getDirectory(foldername) + File.separator
                        + fileDisplayName;

                BufferedOutputStream outStream = new BufferedOutputStream(
                        new FileOutputStream(filePath));
                byte[] buf = new byte[2048];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outStream.write(buf, 0, len);
                }
                inputStream.close();
                outStream.close();
                File fileForSize = new File(filePath);
                fileSize = fileForSize.length();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else if (filePath.startsWith("file:")) {
            filePath = filePath.substring(7);
        }
        if (Config.DEBUG) {
            Log.i(TAG, "File Done " + filePath);
        }
    }

    @Override
    protected void process() throws Exception {
        super.process();
        if (listener != null) {
            ChosenFile file = new ChosenFile();
            file.setFilePath(filePath);
            file.setExtension(mediaExtension);
            file.setFileName(fileDisplayName);
            file.setFileSize(fileSize);
            listener.onProcessedFile(file);
        }
    }

    @Override
    protected void processingDone(String file, String thumbnail, String thumbnailSmall) {
        // Nothing
    }
}
