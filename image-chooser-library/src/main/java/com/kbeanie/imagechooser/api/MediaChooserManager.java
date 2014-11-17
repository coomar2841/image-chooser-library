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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.kbeanie.imagechooser.api.config.Config;
import com.kbeanie.imagechooser.threads.ImageProcessorListener;
import com.kbeanie.imagechooser.threads.ImageProcessorThread;
import com.kbeanie.imagechooser.threads.MediaProcessorThread;
import com.kbeanie.imagechooser.threads.VideoProcessorListener;
import com.kbeanie.imagechooser.threads.VideoProcessorThread;

/**
 * Easy Media Chooser Library for Android Apps. Forget about coding workarounds
 * for different devices, OSes and folders.
 * 
 * @author Beanie
 */
public class MediaChooserManager extends BChooser implements
		ImageProcessorListener, VideoProcessorListener {
	private final static String TAG = "MediaChooserManager";

	private final static String DIRECTORY = "bimagechooser";

	private MediaChooserListener listener;

	/**
	 * Simplest constructor. Specify the type
	 * {@link ChooserType.REQUEST_PICK_PICTURE} or
	 * {@link ChooserType.REQUEST_CAPTURE_PICTURE}
	 * 
	 * @param activity
	 * @param type
	 */
	public MediaChooserManager(Activity activity, int type) {
		super(activity, type, DIRECTORY, true);
	}

	public MediaChooserManager(Fragment fragment, int type) {
		super(fragment, type, DIRECTORY, true);
	}

	public MediaChooserManager(android.app.Fragment fragment, int type) {
		super(fragment, type, DIRECTORY, true);
	}

	/**
	 * Specify the type {@link ChooserType.REQUEST_PICK_PICTURE} or
	 * {@link ChooserType.REQUEST_CAPTURE_PICTURE}
	 * <p>
	 * Optionally, you can control where the exported images with their
	 * thumbnails would be stored.
	 * </p>
	 * 
	 * @param activity
	 * @param type
	 * @param foldername
	 */
	public MediaChooserManager(Activity activity, int type, String foldername) {
		super(activity, type, foldername, true);
	}

	public MediaChooserManager(Fragment fragment, int type, String foldername) {
		super(fragment, type, foldername, true);
	}

	public MediaChooserManager(android.app.Fragment fragment, int type,
			String foldername) {
		super(fragment, type, foldername, true);
	}

	/**
	 * Specify the type {@link ChooserType.REQUEST_PICK_PICTURE} or
	 * {@link ChooserType.REQUEST_CAPTURE_PICTURE}
	 * <p>
	 * Optionally, you can set whether you need thumbnail generation or not. If
	 * not, you would get the original image for the thumbnails as well
	 * </p>
	 * 
	 * @param activity
	 * @param type
	 * @param shouldCreateThumbnails
	 */
	public MediaChooserManager(Activity activity, int type,
			boolean shouldCreateThumbnails) {
		super(activity, type, DIRECTORY, shouldCreateThumbnails);
	}

	public MediaChooserManager(Fragment fragment, int type,
			boolean shouldCreateThumbnails) {
		super(fragment, type, DIRECTORY, shouldCreateThumbnails);
	}

	public MediaChooserManager(android.app.Fragment fragment, int type,
			boolean shouldCreateThumbnails) {
		super(fragment, type, DIRECTORY, shouldCreateThumbnails);
	}

	/**
	 * Specify the type {@link ChooserType.REQUEST_PICK_PICTURE} or
	 * {@link ChooserType.REQUEST_CAPTURE_PICTURE}
	 * <p>
	 * Specify your own foldername and whether you want the generated thumbnails
	 * or not
	 * </p>
	 * 
	 * @param activity
	 * @param type
	 * @param foldername
	 * @param shouldCreateThumbnails
	 */
	public MediaChooserManager(Activity activity, int type, String foldername,
			boolean shouldCreateThumbnails) {
		super(activity, type, foldername, shouldCreateThumbnails);
	}

	public MediaChooserManager(Fragment fragment, int type, String foldername,
			boolean shouldCreateThumbnails) {
		super(fragment, type, foldername, shouldCreateThumbnails);
	}

	public MediaChooserManager(android.app.Fragment fragment, int type,
			String foldername, boolean shouldCreateThumbnails) {
		super(fragment, type, foldername, shouldCreateThumbnails);
	}

	/**
	 * Set a listener, to get callbacks when the medias and the thumbnails are
	 * processed
	 * 
	 * @param listener
	 */
	public void setMediaChooserListener(MediaChooserListener listener) {
		this.listener = listener;
	}

	@Override
	public String choose() throws Exception {
		String path = null;
		if (listener == null) {
			throw new IllegalArgumentException(
					"MediaChooserListener cannot be null. Forgot to set MediaChooserListener???");
		}
		switch (type) {
		case ChooserType.REQUEST_PICK_PICTURE_OR_VIDEO:
			chooseMedia();
			break;
		default:
			throw new IllegalArgumentException(
					"This chooser type is unappropriate with MediaChooserManager: "
							+ type);
		}
		return path;
	}

	private void chooseMedia() throws Exception {
		checkDirectory();
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			if (extras != null) {
				intent.putExtras(extras);
			}
			intent.setType("video/*, images/*");
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			throw new Exception("Activity not found");
		}
	}

	@Override
	public void submit(int requestCode, Intent data) {
		if (requestCode != type) {
			onError("onActivityResult requestCode is different from the type the chooser was initialized with.");
		} else {
			switch (requestCode) {
			case ChooserType.REQUEST_PICK_PICTURE_OR_VIDEO:
				processImageFromGallery(data);
				break;
			}
		}
	}

	@SuppressLint("NewApi")
	private void processImageFromGallery(Intent data) {
		if (data != null && data.getDataString() != null) {
			String uri = data.getData().toString();
			sanitizeURI(uri);
			if (filePathOriginal == null || TextUtils.isEmpty(filePathOriginal)) {
				onError("File path was null");
			} else {
				if (Config.DEBUG) {
					Log.i(TAG, "File: " + filePathOriginal);
				}
				String path = filePathOriginal;
				MediaProcessorThread thread = null;

				if (!wasVideoSelected(data)) {
					thread = new ImageProcessorThread(path, foldername,
							shouldCreateThumbnails);
					((ImageProcessorThread) thread).setListener(this);
				} else {
					thread = new VideoProcessorThread(path, foldername,
							shouldCreateThumbnails);
					((VideoProcessorThread) thread).setListener(this);
				}

				if (activity != null) {
					thread.setContext(activity.getApplicationContext());
				} else if (fragment != null) {
					thread.setContext(fragment.getActivity()
							.getApplicationContext());
				} else if (appFragment != null) {
					thread.setContext(appFragment.getActivity()
							.getApplicationContext());
				}
				thread.start();
			}
		} else {
			onError("Image Uri was null!");
		}
	}

	@Override
	public void onProcessedImage(ChosenImage image) {
		if (listener != null) {
			listener.onImageChosen(image);
		}
	}

	@Override
	public void onProcessedVideo(ChosenVideo video) {
		if (listener != null) {
			listener.onVideoChosen(video);
		}
	}

	@Override
	public void onError(String reason) {
		if (listener != null) {
			listener.onError(reason);
		}
	}
}
