
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
import android.util.Log;

import com.beanie.imagechooser.api.FileUtils;
import com.beanie.imagechooser.api.config.Config;

public abstract class MediaProcessorThread extends Thread {
    private final static String TAG = "MediaProcessorThread";

    private final static int THUMBNAIL_BIG = 1;

    private final static int THUMBNAIL_SMALL = 2;

    protected String filePath;

    protected Context context;

    protected String foldername;

    protected boolean shouldCreateThumnails;

    public MediaProcessorThread(String filePath, String foldername, boolean shouldCreateThumbnails) {
        this.filePath = filePath;
        this.foldername = foldername;
        this.shouldCreateThumnails = shouldCreateThumbnails;
    }

    protected void downloadAndProcess(String url) throws IOException {
        filePath = downloadFile(url);
        process();
    }

    protected void process() throws IOException {
        if (!filePath.contains(foldername)) {
            copyFileToDir();
        }
        if (shouldCreateThumnails) {
            String[] thumbnails = createThumbnails(this.filePath);
            processingDone(this.filePath, thumbnails[0], thumbnails[1]);
        } else {
            processingDone(this.filePath, this.filePath, this.filePath);
        }
    }

    private String[] createThumbnails(String image) {
        String[] images = new String[2];
        images[0] = getThumnailPath(image);
        images[1] = getThumbnailSmallPath(image);
        return images;
    }

    private String getThumnailPath(String file) {
        if (Config.DEBUG) {
            Log.i(TAG, "Compressing ... THUMBNAIL");
        }
        return compressAndSave(file, THUMBNAIL_BIG);
    }

    private String getThumbnailSmallPath(String file) {
        if (Config.DEBUG) {
            Log.i(TAG, "Compressing ... THUMBNAIL SMALL");
        }
        return compressAndSave(file, THUMBNAIL_SMALL);
    }

    private String compressAndSave(String fileImage, int scale) {
        try {
            ExifInterface exif = new ExifInterface(fileImage);
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
            Bitmap bitmap = BitmapFactory.decodeFile(fileImage, options);
            File original = new File(fileImage);
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

    protected String downloadFile(String url) {
        String localFilePath = "";
        HttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            InputStream stream = response.getEntity().getContent();

            localFilePath = FileUtils.getDirectory(foldername) + File.separator
                    + Calendar.getInstance().getTimeInMillis() + ".jpg";
            File localFile = new File(localFilePath);

            FileOutputStream fileOutputStream = new FileOutputStream(localFile);

            byte[] buffer = new byte[1024];
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

    protected void manageDiretoryCache(final int maxDirectorySize, final int maxThresholdDays) {
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

        if (count > maxDirectorySize) {
            final long today = Calendar.getInstance().getTimeInMillis();
            FileFilter filter = new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    if (today - pathname.lastModified() > maxThresholdDays) {
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

    protected abstract void processingDone(String file, String thumbnail, String thumbnailSmall);
}
