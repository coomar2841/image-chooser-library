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

package com.kbeanie.imagechooser.api;

import android.content.Context;

import java.io.File;

public class FileUtils {

    public static String getDirectory(Context context, String foldername) {
        File directory = getExternalCacheDir(context, foldername);
        if (directory == null) {
            directory = getInternalCacheDir(context, foldername);
        }
        return directory.getAbsolutePath();
    }

    public static String getFileExtension(String filename) {
        String extension = "";
        try {
            extension = filename.substring(filename.lastIndexOf(".") + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return extension;
    }

    private static File getExternalCacheDir(Context context, String foldername) {
        // Get the directory for the app's private pictures directory.
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            return null;
        }
        File cache = new File(cacheDir, foldername);
        if (!cache.exists()) {
            if (!cache.mkdirs()) {
                cache = null;
            }
        }
        return cache;
    }

    private static File getInternalCacheDir(Context context, String foldername) {
        File cache = new File(context.getCacheDir(), foldername);
        if (!cache.exists()) {
            if (!cache.mkdirs()) {
                cache = null;
            }
        }
        return cache;
    }


}
