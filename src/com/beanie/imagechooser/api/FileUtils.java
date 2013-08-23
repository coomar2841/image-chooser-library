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

package com.beanie.imagechooser.api;

import java.io.File;

import android.os.Environment;

public class FileUtils {
    public static String getDirectory(String foldername) {
        if (!foldername.startsWith(".")) {
            foldername = "." + foldername;
        }
        File directory = null;
        directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + foldername);
        if (!directory.exists()) {
            directory.mkdir();
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

}
