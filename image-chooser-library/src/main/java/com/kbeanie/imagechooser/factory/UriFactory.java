package com.kbeanie.imagechooser.factory;

import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.kbeanie.imagechooser.api.FileUtils;

import java.io.File;

/**
 * Created by vervik on 9/27/15.
 */
public class UriFactory {

    static String TAG = UriFactory.class.getSimpleName();

    /**
     * If set, it will be the temp URI where the camera app should save the captured image / video to
     * <p/>
     * intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
     */
    private String filePathOriginal;


    private UriFactory() {
        // private
    }


    public void setFilePathOriginal(String filePathOriginal) {
        Log.d(TAG, "File path set. Is: " + filePathOriginal);
        this.filePathOriginal = filePathOriginal;
    }

    public String getFilePathOriginal(String foldername, String extension) {
        if (filePathOriginal != null) {
            Log.d(TAG, "File path set. We return: " + filePathOriginal);
            return filePathOriginal;
        }

        return FileUtils.getDirectory(foldername)
                + File.separator + DateFactory.getInstance().getTimeInMillis()
                + "." + extension;
    }

    public void reset() {
        Log.d(TAG, "We reset capture URI");
        this.filePathOriginal = null;
    }


    private static UriFactory instance;

    public static UriFactory getInstance() {
        if (instance == null) {
            instance = new UriFactory();
        }
        return instance;
    }

    /*
    filePathOriginal = FileUtils.getDirectory(foldername)
                    + File.separator + DateFactory.getInstance().getTimeInMillis() //Calendar.getInstance().getTimeInMillis()
                    + ".jpg";
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(filePathOriginal)));
     */
}
