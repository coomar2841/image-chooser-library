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

import java.io.IOException;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.kbeanie.imagechooser.BuildConfig;
import com.kbeanie.imagechooser.api.ChosenImage;

public class ImageProcessorThread extends MediaProcessorThread {

	private final static String TAG = "ImageProcessorThread";

	private ImageProcessorListener listener;

	public ImageProcessorThread(String filePath, String foldername,
			boolean shouldCreateThumbnails) {
		super(filePath, foldername, shouldCreateThumbnails);
		setMediaExtension("jpg");
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
			manageDiretoryCache("jpg");
			processImage();
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

	private void processImage() throws Exception {

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Processing Image File: " + filePath);
		}

		// Picasa on Android >= 3.0
		if (filePath != null && filePath.startsWith("content:")) {
			filePath = getAbsoluteImagePathFromUri(Uri.parse(filePath));
		}
		if (filePath == null || TextUtils.isEmpty(filePath)) {
			if (listener != null) {
				listener.onError("Couldn't process a null file");
			}
		} else if (filePath.startsWith("http")) {
			downloadAndProcess(filePath);
		} else if (filePath
				.startsWith("content://com.google.android.gallery3d")
				|| filePath
						.startsWith("content://com.microsoft.skydrive.content")) {
			processPicasaMedia(filePath, ".jpg");
		} else if (filePath
				.startsWith("content://com.google.android.apps.photos.content")
				|| filePath
						.startsWith("content://com.android.providers.media.documents")
				|| filePath
						.startsWith("content://com.google.android.apps.docs.storage")) {
			processGooglePhotosMedia(filePath, ".jpg");
		} else {
			process();
		}
	}

	@Override
	protected void process() throws Exception {
		super.process();
		if (shouldCreateThumnails) {
			String[] thumbnails = createThumbnails(this.filePath);
			processingDone(this.filePath, thumbnails[0], thumbnails[1]);
		} else {
			processingDone(this.filePath, this.filePath, this.filePath);
		}
	}

	@Override
	protected void processingDone(String original, String thumbnail,
			String thunbnailSmall) {
		if (listener != null) {
			ChosenImage image = new ChosenImage();
			image.setFilePathOriginal(original);
			image.setFileThumbnail(thumbnail);
			image.setFileThumbnailSmall(thunbnailSmall);
			listener.onProcessedImage(image);
		}
	}
}
