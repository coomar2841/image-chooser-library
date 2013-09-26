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

package com.beanie.imagechooserapp;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.beanie.imagechooser.api.ChooserType;
import com.beanie.imagechooser.api.ChosenVideo;
import com.beanie.imagechooser.api.VideoChooserListener;
import com.beanie.imagechooser.api.VideoChooserManager;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class VideoChooserActivity extends Activity implements
		VideoChooserListener {
	private VideoChooserManager videoChooserManager;

	private ProgressBar pbar;

	private ImageView imageViewThumb;

	private ImageView imageViewThumbSmall;

	private VideoView videoView;

	private AdView adView;

	private String filePath;

	private int chooserType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_chooser);

		pbar = (ProgressBar) findViewById(R.id.pBar);
		pbar.setVisibility(View.GONE);

		imageViewThumb = (ImageView) findViewById(R.id.imageViewThumbnail);
		imageViewThumbSmall = (ImageView) findViewById(R.id.imageViewThumbnailSmall);

		videoView = (VideoView) findViewById(R.id.videoView);

		adView = (AdView) findViewById(R.id.adView);

		AdRequest request = new AdRequest();
		request.addTestDevice(AdRequest.TEST_EMULATOR);
		request.addTestDevice(Config.TEST_DEVICE_ID_1);
		request.addTestDevice(Config.TEST_DEVICE_ID_2);
		adView.loadAd(request);
	}

	public void captureVideo(View view) {
		chooserType = ChooserType.REQUEST_CAPTURE_VIDEO;
		videoChooserManager = new VideoChooserManager(this,
				ChooserType.REQUEST_CAPTURE_VIDEO);
		videoChooserManager.setVideoChooserListener(this);
		try {
			pbar.setVisibility(View.VISIBLE);
			filePath = videoChooserManager.choose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pickVideo(View view) {
		chooserType = ChooserType.REQUEST_PICK_VIDEO;
		videoChooserManager = new VideoChooserManager(this,
				ChooserType.REQUEST_PICK_VIDEO);
		videoChooserManager.setVideoChooserListener(this);
		try {
			pbar.setVisibility(View.VISIBLE);
			videoChooserManager.choose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onVideoChosen(final ChosenVideo video) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				pbar.setVisibility(View.GONE);
				if (video != null) {
					videoView.setVideoURI(Uri.parse(new File(video
							.getVideoFilePath()).toString()));
					videoView.start();
					imageViewThumb.setImageURI(Uri.parse(new File(video
							.getThumbnailPath()).toString()));
					imageViewThumbSmall.setImageURI(Uri.parse(new File(video
							.getThumbnailSmallPath()).toString()));
				}
			}
		});
	}

	@Override
	public void onError(final String reason) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				pbar.setVisibility(View.GONE);
				Toast.makeText(VideoChooserActivity.this, reason,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK
				&& (requestCode == ChooserType.REQUEST_CAPTURE_VIDEO || requestCode == ChooserType.REQUEST_PICK_VIDEO)) {
			if (videoChooserManager == null) {
				reinitializeVideoChooser();
			}
			videoChooserManager.submit(requestCode, data);
		} else {
			pbar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

	// Should be called if for some reason the VideoChooserManager is null (Due
	// to destroying of activity for low memory situations)
	private void reinitializeVideoChooser() {
		videoChooserManager = new VideoChooserManager(this, chooserType,
				"myfolder", true);
		videoChooserManager.setVideoChooserListener(this);
		videoChooserManager.reinitialize(filePath);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("chooser_type", chooserType);
		outState.putString("media_path", filePath);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("chooser_type")) {
				chooserType = savedInstanceState.getInt("chooser_type");
			}

			if (savedInstanceState.containsKey("media_path")) {
				filePath = savedInstanceState.getString("media_path");
			}
		}
		super.onRestoreInstanceState(savedInstanceState);
	}
}
