/**
 * ****************************************************************************
 * Copyright 2013 Kumar Bibek
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */

package com.kbeanie.imagechooser.threads;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import com.kbeanie.imagechooser.BuildConfig;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenVideo;
import com.kbeanie.imagechooser.api.FileUtils;
import com.kbeanie.imagechooser.exceptions.ChooserException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

import static com.kbeanie.imagechooser.helpers.StreamHelper.close;
import static com.kbeanie.imagechooser.helpers.StreamHelper.flush;
import static com.kbeanie.imagechooser.helpers.StreamHelper.verifyStream;

public abstract class MediaProcessorThread extends Thread {
    private final static String TAG = MediaProcessorThread.class.getSimpleName();

    // 500 MB Cache size
    protected final static int MAX_DIRECTORY_SIZE = 500 * 1024 * 1024;
    // Number of days to preserve 10 days
    protected final static int MAX_THRESHOLD_DAYS = (int) (10 * 24 * 60 * 60 * 1000);

    private final static int THUMBNAIL_BIG = 1;

    private final static int THUMBNAIL_SMALL = 2;
    protected String[] filePaths = new String[0];

    protected String filePath;

    protected Context context;

    protected String foldername;

    protected boolean shouldCreateThumnails;

    protected String mediaExtension;

    protected boolean clearOldFiles = false;


    public MediaProcessorThread(String filePath, String foldername,
                                boolean shouldCreateThumbnails) {
        this.filePath = filePath;
        this.foldername = foldername;
        this.shouldCreateThumnails = shouldCreateThumbnails;
    }

    public MediaProcessorThread(String[] filePaths, String foldername, boolean shouldCreateThumnails) {
        this.filePaths = filePaths;
        this.foldername = foldername;
        this.shouldCreateThumnails = shouldCreateThumnails;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setMediaExtension(String extension) {
        this.mediaExtension = extension;
    }

    public void clearOldFiles(boolean clearOldFiles) {
        this.clearOldFiles = clearOldFiles;
    }

    protected void downloadAndProcess(String url) throws ChooserException {
        filePath = downloadFile(url);
        process();
    }

    protected ChosenVideo downloadAndProcessVideo(String url) throws ChooserException {
        String localFile = downloadFile(url);
        ChosenVideo video = processVideo(localFile);
        return video;
    }

    protected ChosenImage downloadAndProcessNew(String url) throws ChooserException {
        String downloadedFilePath = downloadFile(url);
        ChosenImage image = processImage(downloadedFilePath);
        return image;
    }

    protected void process() throws ChooserException {
        if (!filePath.contains(foldername)) {
            copyFileToDir();
        }
    }

    protected ChosenVideo processVideo(String filePath) throws ChooserException {
        ChosenVideo video = new ChosenVideo();
        if (!filePath.contains(foldername)) {
            String localFilePath = copyFileToDir(filePath);
            video.setVideoFilePath(localFilePath);
        } else {
            video.setVideoFilePath(filePath);
        }
        return video;
    }

    protected ChosenImage processImage(String filePath) throws ChooserException {
        ChosenImage image = new ChosenImage();
        if (!filePath.contains(foldername)) {
            String localFilePath = copyFileToDir(filePath);
            image.setFilePathOriginal(localFilePath);
        } else {
            image.setFilePathOriginal(filePath);
        }
        return image;
    }

    protected String[] createThumbnails(String image) throws ChooserException {
        String[] images = new String[2];
        images[0] = getThumbnailPath(image);
        images[1] = getThumbnailSmallPath(image);
        return images;
    }

    private String getThumbnailPath(String file) throws ChooserException {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Compressing ... THUMBNAIL");
        }
        return compressAndSaveImage(file, THUMBNAIL_BIG);
    }

    private String getThumbnailSmallPath(String file) throws ChooserException {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Compressing ... THUMBNAIL SMALL");
        }
        return compressAndSaveImage(file, THUMBNAIL_SMALL);
    }

    private String compressAndSaveImage(String fileImage, int scale) throws ChooserException {

        FileOutputStream stream = null;
        BufferedInputStream bstream = null;
        Bitmap bitmap = null;
        try {
            Options optionsForGettingDimensions = new Options();
            optionsForGettingDimensions.inJustDecodeBounds = true;
            BufferedInputStream boundsOnlyStream = new BufferedInputStream(new FileInputStream(fileImage));
            bitmap = BitmapFactory.decodeStream(boundsOnlyStream, null, optionsForGettingDimensions);
            if (bitmap != null) {
                bitmap.recycle();
            }
            if (boundsOnlyStream != null) {
                boundsOnlyStream.close();
            }
            int w, l;
            w = optionsForGettingDimensions.outWidth;
            l = optionsForGettingDimensions.outHeight;

            ExifInterface exif = new ExifInterface(fileImage);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int rotate = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = -90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            int what = w > l ? w : l;

            Options options = new Options();
            if (what > 3000) {
                options.inSampleSize = scale * 6;
            } else if (what > 2000 && what <= 3000) {
                options.inSampleSize = scale * 5;
            } else if (what > 1500 && what <= 2000) {
                options.inSampleSize = scale * 4;
            } else if (what > 1000 && what <= 1500) {
                options.inSampleSize = scale * 3;
            } else if (what > 400 && what <= 1000) {
                options.inSampleSize = scale * 2;
            } else {
                options.inSampleSize = scale;
            }

            options.inJustDecodeBounds = false;
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Scale: " + (what / options.inSampleSize));
                Log.i(TAG, "Rotate: " + rotate);
            }
            // TODO: Sometime the decode File Returns null for some images
            // For such cases, thumbnails can't be created.
            // Thumbnails will link to the original file
            BufferedInputStream scaledInputStream = new BufferedInputStream(new FileInputStream(fileImage));
            bitmap = BitmapFactory.decodeStream(scaledInputStream, null, options);
//            verifyBitmap(fileImage, bitmap);
            scaledInputStream.close();
            if (bitmap == null) {
                return fileImage;
            }
            File original = new File(fileImage);
            File file = new File(
                    (original.getParent() + File.separator + original.getName()
                            .replace(".", "_fact_" + scale + ".")));
            stream = new FileOutputStream(file);
            if (rotate != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, false);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            return file.getAbsolutePath();

        } catch (IOException e) {
            return fileImage;
//            throw new ChooserException(e);
        } catch (Exception e) {
            return fileImage;
        } finally {
            close(bstream);
            flush(stream);
            close(stream);
        }
    }

    private String copyFileToDir(String filePath) throws ChooserException {
        BufferedOutputStream outStream = null;
        BufferedInputStream bStream = null;
        try {
            File file;
            file = new File(Uri.parse(filePath).getPath());
            File copyTo = new File(FileUtils.getDirectory(foldername) + File.separator + file.getName());
            bStream = new BufferedInputStream(new FileInputStream(file));
            outStream = new BufferedOutputStream(new FileOutputStream(copyTo));
            byte[] buf = new byte[2048];
            int len;
            while ((len = bStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }

            filePath = copyTo.getAbsolutePath();
            return filePath;
        } catch (IOException e) {
            throw new ChooserException(e);
        } finally {
            flush(outStream);
            close(bStream);
            close(outStream);
        }
    }

    private void copyFileToDir() throws ChooserException {
        BufferedOutputStream outStream = null;
        BufferedInputStream bStream = null;
        try {
            File file;
            file = new File(Uri.parse(filePath).getPath());
            File copyTo = new File(FileUtils.getDirectory(foldername) + File.separator + file.getName());
            bStream = new BufferedInputStream(new FileInputStream(file));
            outStream = new BufferedOutputStream(new FileOutputStream(copyTo));
            byte[] buf = new byte[2048];
            int len;
            while ((len = bStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }

            filePath = copyTo.getAbsolutePath();
        } catch (IOException e) {
            throw new ChooserException(e);
        } finally {
            flush(outStream);
            close(bStream);
            close(outStream);
        }
    }


    protected String downloadFile(String url) {
        String localFilePath = "";
        try {
            URL u = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedInputStream bStream = new BufferedInputStream(stream);

            localFilePath = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + "."
                    + mediaExtension;
            File localFile = new File(localFilePath);

            FileOutputStream fileOutputStream = new FileOutputStream(localFile);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = bStream.read(buffer)) > 0)
                fileOutputStream.write(buffer, 0, len);
            fileOutputStream.flush();
            fileOutputStream.close();
            bStream.close();

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Image saved: " + localFilePath.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localFilePath;
    }

    protected void manageDirectoryCache(final String extension) {
        if (!clearOldFiles) {
            return;
        }
        File directory;
        directory = new File(FileUtils.getDirectory(foldername));
        File[] files = directory.listFiles();
        long count = 0;
        if (files == null) {
            return;
        }
        for (File file : files) {
            count = count + file.length();
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Directory size: " + count);
        }

        if (count > MAX_DIRECTORY_SIZE) {
            final long today = Calendar.getInstance().getTimeInMillis();
            FileFilter filter = new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    if (today - pathname.lastModified() > MAX_THRESHOLD_DAYS
                            && pathname
                            .getAbsolutePath()
                            .toUpperCase(Locale.ENGLISH)
                            .endsWith(
                                    extension
                                            .toUpperCase(Locale.ENGLISH))) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };

            File[] filterFiles = directory.listFiles(filter);
            int deletedFileCount = 0;
            for (File file : filterFiles) {
                deletedFileCount++;
                file.delete();
            }
            Log.i(TAG, "Deleted " + deletedFileCount + " files");
        }
    }

    protected abstract void processingDone(String file, String thumbnail,
                                           String thumbnailSmall);

    protected void processPicasaMedia(String path, String extension) throws ChooserException {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Started");
        }

        BufferedOutputStream outStream = null;
        BufferedInputStream bStream = null;

        try {
            InputStream inputStream = context.getContentResolver()
                    .openInputStream(Uri.parse(path));

            bStream = new BufferedInputStream(inputStream);

            verifyStream(path, bStream);

            filePath = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + extension;

            outStream = new BufferedOutputStream(new FileOutputStream(filePath));
            byte[] buf = new byte[2048];
            int len;
            while ((len = bStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            flush(outStream);
            process();
        } catch (IOException e) {
            throw new ChooserException(e);
        } finally {
            close(bStream);
            close(outStream);
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Done");
        }
    }

    protected ChosenVideo processPicasaMediaNewVideo(String path, String extension) throws ChooserException {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Started");
        }

        ChosenVideo video = null;
        BufferedOutputStream outStream = null;
        BufferedInputStream bStream = null;
        String outFile;
        try {
            InputStream inputStream = context.getContentResolver()
                    .openInputStream(Uri.parse(path));

            bStream = new BufferedInputStream(inputStream);

            verifyStream(path, bStream);

            outFile = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + extension;

            outStream = new BufferedOutputStream(new FileOutputStream(outFile));
            byte[] buf = new byte[2048];
            int len;
            while ((len = bStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            flush(outStream);
            video = processVideo(outFile);
        } catch (IOException e) {
            throw new ChooserException(e);
        } finally {
            close(bStream);
            close(outStream);
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Done");
        }
        return video;
    }

    protected ChosenImage processPicasaMediaNew(String path, String extension) throws ChooserException {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Started");
        }
        String outFile;
        ChosenImage image;

        BufferedOutputStream outStream = null;
        BufferedInputStream bStream = null;

        try {
            InputStream inputStream = context.getContentResolver()
                    .openInputStream(Uri.parse(path));

            bStream = new BufferedInputStream(inputStream);

            verifyStream(path, bStream);

            outFile = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + extension;

            outStream = new BufferedOutputStream(new FileOutputStream(outFile));
            byte[] buf = new byte[2048];
            int len;
            while ((len = bStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            flush(outStream);
            image = processImage(path);
        } catch (IOException e) {
            throw new ChooserException(e);
        } finally {
            close(bStream);
            close(outStream);
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Done");
        }

        return image;
    }

    protected void processGooglePhotosMedia(String path, String extension)
            throws ChooserException {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Google photos Started");
            Log.i(TAG, "URI: " + path);
            Log.i(TAG, "Extension: " + extension);
        }
        String retrievedExtension = checkExtension(Uri.parse(path));
        if (retrievedExtension != null
                && !TextUtils.isEmpty(retrievedExtension)) {
            extension = "." + retrievedExtension;
        }

        BufferedInputStream inputStream = null;
        BufferedOutputStream outStream = null;

        try {

            filePath = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + extension;
            ParcelFileDescriptor parcelFileDescriptor = context
                    .getContentResolver().openFileDescriptor(Uri.parse(path),
                            "r");

            verifyStream(path, parcelFileDescriptor);

            FileDescriptor fileDescriptor = parcelFileDescriptor
                    .getFileDescriptor();

            inputStream = new BufferedInputStream(new FileInputStream(fileDescriptor));

            BufferedInputStream reader = new BufferedInputStream(inputStream);

            outStream = new BufferedOutputStream(
                    new FileOutputStream(filePath));
            byte[] buf = new byte[2048];
            int len;
            while ((len = reader.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            flush(outStream);
            process();
        } catch (IOException e) {
            throw new ChooserException(e);
        } finally {
            flush(outStream);
            close(outStream);
            close(inputStream);
        }


        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Done");
        }
    }

    protected ChosenVideo processGooglePhotosMediaNewVideo(String path, String extension)
            throws ChooserException {
        ChosenVideo video;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Google photos Started");
            Log.i(TAG, "URI: " + path);
            Log.i(TAG, "Extension: " + extension);
        }
        String retrievedExtension = checkExtension(Uri.parse(path));
        if (retrievedExtension != null
                && !TextUtils.isEmpty(retrievedExtension)) {
            extension = "." + retrievedExtension;
        }

        BufferedInputStream inputStream = null;
        BufferedOutputStream outStream = null;
        String localPath;
        try {

            localPath = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + extension;
            ParcelFileDescriptor parcelFileDescriptor = context
                    .getContentResolver().openFileDescriptor(Uri.parse(path),
                            "r");

            verifyStream(path, parcelFileDescriptor);

            FileDescriptor fileDescriptor = parcelFileDescriptor
                    .getFileDescriptor();

            inputStream = new BufferedInputStream(new FileInputStream(fileDescriptor));

            BufferedInputStream reader = new BufferedInputStream(inputStream);

            outStream = new BufferedOutputStream(
                    new FileOutputStream(localPath));
            byte[] buf = new byte[2048];
            int len;
            while ((len = reader.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            flush(outStream);
            video  = processVideo(localPath);
        } catch (IOException e) {
            throw new ChooserException(e);
        } finally {
            flush(outStream);
            close(outStream);
            close(inputStream);
        }


        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Done");
        }
        return video;
    }

    protected ChosenImage processGooglePhotosMediaNew(String path, String extension)
            throws ChooserException {
        String outFile;
        ChosenImage image;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Google photos Started");
            Log.i(TAG, "URI: " + path);
            Log.i(TAG, "Extension: " + extension);
        }
        String retrievedExtension = checkExtension(Uri.parse(path));
        if (retrievedExtension != null
                && !TextUtils.isEmpty(retrievedExtension)) {
            extension = "." + retrievedExtension;
        }

        BufferedInputStream inputStream = null;
        BufferedOutputStream outStream = null;

        try {

            outFile = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + extension;
            ParcelFileDescriptor parcelFileDescriptor = context
                    .getContentResolver().openFileDescriptor(Uri.parse(path),
                            "r");

            verifyStream(path, parcelFileDescriptor);

            FileDescriptor fileDescriptor = parcelFileDescriptor
                    .getFileDescriptor();

            inputStream = new BufferedInputStream(new FileInputStream(fileDescriptor));

            BufferedInputStream reader = new BufferedInputStream(inputStream);

            outStream = new BufferedOutputStream(
                    new FileOutputStream(outFile));
            byte[] buf = new byte[2048];
            int len;
            while ((len = reader.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            flush(outStream);
            image = processImage(outFile);
        } catch (IOException e) {
            throw new ChooserException(e);
        } finally {
            flush(outStream);
            close(outStream);
            close(inputStream);
        }


        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Picasa Done");
        }

        return image;
    }

    public String checkExtension(Uri uri) {

        String extension = "";

        // The query, since it only applies to a single document, will only
        // return
        // one row. There's no need to filter, sort, or select fields, since we
        // want
        // all fields for one document.
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy
            // for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file
                // name.
                String displayName = cursor.getString(cursor
                        .getColumnIndex(OpenableColumns.DISPLAY_NAME));
                int position = displayName.indexOf(".");
                extension = displayName.substring(position + 1);
                Log.i(TAG, "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null. But since
                // an
                // int can't be null in Java, the behavior is
                // implementation-specific,
                // which is just a fancy term for "unpredictable". So as
                // a rule, check if it's null before assigning to an int. This
                // will
                // happen often: The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but
                    // cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
        return extension;
    }

    protected void processContentProviderMedia(String path, String extension)
            throws ChooserException {
        checkExtension(Uri.parse(path));

        InputStream inputStream = null;
        BufferedOutputStream outStream = null;
        BufferedInputStream bStream = null;

        try {
            inputStream = context.getContentResolver()
                    .openInputStream(Uri.parse(path));
            bStream = new BufferedInputStream(inputStream);
            verifyStream(path, bStream);

            filePath = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + extension;

            outStream = new BufferedOutputStream(new FileOutputStream(filePath));
            byte[] buf = new byte[2048];
            int len;
            while ((len = bStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }

            process();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new ChooserException(e);
        } finally {
            close(bStream);
            close(outStream);
        }
    }

    protected ChosenVideo processContentProviderMediaNewVideo(String path, String extension)
            throws ChooserException {
        checkExtension(Uri.parse(path));
        ChosenVideo video = null;
        InputStream inputStream = null;
        BufferedOutputStream outStream = null;
        BufferedInputStream bStream = null;
        String outFile;
        try {
            inputStream = context.getContentResolver()
                    .openInputStream(Uri.parse(path));
            bStream = new BufferedInputStream(inputStream);
            verifyStream(path, bStream);

            outFile = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + extension;

            outStream = new BufferedOutputStream(new FileOutputStream(outFile));
            byte[] buf = new byte[2048];
            int len;
            while ((len = bStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }

            video = processVideo(path);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new ChooserException(e);
        } finally {
            close(bStream);
            close(outStream);
        }
        return video;
    }

    @SuppressLint("NewApi")
    protected String getAbsoluteImagePathFromUri(Uri imageUri) {
        String[] proj = {MediaColumns.DATA, MediaColumns.DISPLAY_NAME};

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Image Uri: " + imageUri.toString());
        }

        if (imageUri.toString().startsWith(
                "content://com.android.gallery3d.provider")) {
            imageUri = Uri.parse(imageUri.toString().replace(
                    "com.android.gallery3d", "com.google.android.gallery3d"));
        }

        String filePath = "";
        String imageUriString = imageUri.toString();
        if (imageUriString.startsWith("content://com.google.android.gallery3d")
                || imageUriString
                .startsWith("content://com.google.android.apps.photos.content")
                || imageUriString
                .startsWith("content://com.android.providers.media.documents")
                || imageUriString
                .startsWith("content://com.google.android.apps.docs.storage")
                || imageUriString
                .startsWith("content://com.microsoft.skydrive.content.external")
                || imageUriString
                .startsWith("content://com.android.externalstorage.documents")
                || imageUriString
                .startsWith("content://com.android.internalstorage.documents") ||
                imageUriString.startsWith("content://")) {
            filePath = imageUri.toString();
        } else {
            Cursor cursor = context.getContentResolver().query(imageUri, proj,
                    null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaColumns.DATA));
            cursor.close();
        }

        if (filePath == null && isDownloadsDocument(imageUri)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                filePath = getPath(context, imageUri);
        }
        return filePath;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
