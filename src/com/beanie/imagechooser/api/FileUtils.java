
package com.beanie.imagechooser.api;

import java.io.File;

import android.os.Environment;

public class FileUtils {
    public static String getDirectory(String foldername) {
        File directory = null;
        directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + foldername);
        return directory.getAbsolutePath();
    }

}
