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

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class BChooser {
	protected Activity activity;

	protected Fragment fragment;

	protected android.app.Fragment appFragment;

	protected int type;

	protected String foldername;

	protected boolean shouldCreateThumbnails;

	protected String filePathOriginal;
	
	protected Bundle extras;

	public BChooser(Activity activity, int type, String foldername,
			boolean shouldCreateThumbnails) {
		this.activity = activity;
		this.type = type;
		this.foldername = foldername;
		this.shouldCreateThumbnails = shouldCreateThumbnails;
	}

	public BChooser(Fragment fragment, int type, String foldername,
			boolean shouldCreateThumbnails) {
		this.fragment = fragment;
		this.type = type;
		this.foldername = foldername;
		this.shouldCreateThumbnails = shouldCreateThumbnails;
	}

	public BChooser(android.app.Fragment fragment, int type, String foldername,
			boolean shouldCreateThumbnails) {
		this.appFragment = fragment;
		this.type = type;
		this.foldername = foldername;
		this.shouldCreateThumbnails = shouldCreateThumbnails;
	}

	/**
	 * Call this method, to start the chooser, i.e, The camera app or the
	 * gallery depending upon the type.
	 * <p>
	 * Returns the path, in case, a capture is requested. You will need to save
	 * this path, so that, in case, the ChooserManager is destoryed due to
	 * activity lifecycle, you will use this information to create the
	 * ChooserManager instance again
	 * </p>
	 * <p>
	 * In case of picking a video or image, null would be returned.
	 * </p>
	 * 
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public abstract String choose() throws IllegalArgumentException, Exception;

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
			directory.mkdirs();
		}
	}

	@SuppressLint("NewApi")
	protected void startActivity(Intent intent) {
		if (activity != null) {
			activity.startActivityForResult(intent, type);
		} else if (fragment != null) {
			fragment.startActivityForResult(intent, type);
		} else if (appFragment != null) {
			appFragment.startActivityForResult(intent, type);
		}
	}

	public void reinitialize(String path) {
		filePathOriginal = path;
	}

	// Change the URI only when the returned string contains "file:/" prefix.
	// For all the other situations the URI doesn't need to be changed
	protected void sanitizeURI(String uri) {
		filePathOriginal = uri;
		// Picasa on Android < 3.0
		if (uri.matches("https?://\\w+\\.googleusercontent\\.com/.+")) {
			filePathOriginal = uri;
		}
		// Local storage
		if (uri.startsWith("file://")) {
			filePathOriginal = uri.substring(7);
		}
	}
	
	@SuppressLint("NewApi")
    protected Context getContext() {
		if (activity != null) {
			return activity.getApplicationContext();
		} else if (fragment != null) {
			return fragment.getActivity().getApplicationContext();
		} else if (appFragment != null) {
			return appFragment.getActivity().getApplicationContext();
		}

		return null;
    }
	
	protected boolean wasVideoSelected(Intent data) {
		if (data == null)
		{
			return false;
		}
		
		if (data.getType() != null && data.getType().startsWith("video"))
		{
			return true;
		}
		
		ContentResolver cR = getContext().getContentResolver();
		String type = cR.getType(data.getData());
		if (type != null && type.startsWith("video"))
		{
			return true;
		}

		return false;
	}
	
	public void setExtras(Bundle extras){
		this.extras = extras;
	}
}
