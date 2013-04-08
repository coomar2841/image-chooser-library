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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;

public abstract class BChooser {
	protected Activity activity;

	protected int type;

	protected String foldername;

	protected boolean shouldCreateThumbnails;

	protected String filePathOriginal;

	public BChooser(Activity activity, int type, String foldername,
			boolean shouldCreateThumbnails) {
		this.activity = activity;
		this.type = type;
		this.foldername = foldername;
		this.shouldCreateThumbnails = shouldCreateThumbnails;
	}

	/**
	 * Call this method, to start the chooser, i.e, The camera app or the
	 * gallery depending upon the type
	 * 
	 * @throws IllegalArgumentException
	 */
	public abstract void choose() throws IllegalArgumentException;

	/**
	 * Call this method to process the result from within your onActivityResult
	 * method. You don't need to do any processing at all. Just pass in the
	 * request code and the data, and everything else will be taken care of.
	 * 
	 * @param requestCode
	 * @param data
	 */
	public abstract void submit(int requestCode, Intent data);

	protected void checkDirectory() {
		File directory = null;
		directory = new File(FileUtils.getDirectory(foldername));
		if (!directory.exists()) {
			directory.mkdir();
		}
	}

	protected void startActivity(Intent intent) {
		activity.startActivityForResult(intent, type);
	}

	protected String getAbsoluteImagePathFromUri(Uri imageUri) {
		String[] proj = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME };

		if (imageUri.toString().startsWith(
				"content://com.android.gallery3d.provider")) {
			imageUri = Uri.parse(imageUri.toString().replace(
					"com.android.gallery3d", "com.google.android.gallery3d"));
		}
		Cursor cursor = activity.getContentResolver().query(imageUri, proj,
				null, null, null);

		cursor.moveToFirst();

		String filePath = "";
		if (imageUri.toString().startsWith(
				"content://com.google.android.gallery3d")) {
			filePath = imageUri.toString();
		} else {
			filePath = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaColumns.DATA));
		}
		cursor.close();

		return filePath;
	}
}
