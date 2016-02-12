/**
 * ****************************************************************************
 * Copyright 2013 Kumar Bibek
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */

package com.beanie.imagechooserapp;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenFile;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.FileChooserListener;
import com.kbeanie.imagechooser.api.FileChooserManager;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.api.IntentUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageChooserActivity extends BasicActivity implements
        ImageChooserListener {

    private final static String TAG = "ICA";

    private ImageView imageViewThumbnail;

    private ImageView imageViewThumbSmall;

    private TextView textViewFile;

    private ImageChooserManager imageChooserManager;

    private ProgressBar pbar;

    private String filePath;

    private int chooserType;

    private boolean isActivityResultOver = false;

    private String originalFilePath;
    private String thumbnailFilePath;
    private String thumbnailSmallFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Activity Created");
        setContentView(R.layout.activity_image_chooser);

        Button buttonTakePicture = (Button) findViewById(R.id.buttonTakePicture);
        buttonTakePicture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        Button buttonChooseImage = (Button) findViewById(R.id.buttonChooseImage);
        buttonChooseImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        imageViewThumbnail = (ImageView) findViewById(R.id.imageViewThumb);
        imageViewThumbSmall = (ImageView) findViewById(R.id.imageViewThumbSmall);
        textViewFile = (TextView) findViewById(R.id.textViewFile);

        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setVisibility(View.GONE);

        setupAds();

        checkForSharedImage(getIntent());
    }

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();
        try {
            pbar.setVisibility(View.VISIBLE);
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            pbar.setVisibility(View.VISIBLE);
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "OnActivityResult");
        Log.i(TAG, "File Path : " + filePath);
        Log.i(TAG, "Chooser Type: " + chooserType);
        if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        } else {
            pbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "Chosen Image: O - " + image.getFilePathOriginal());
                Log.i(TAG, "Chosen Image: T - " + image.getFileThumbnail());
                Log.i(TAG, "Chosen Image: Ts - " + image.getFileThumbnailSmall());
                isActivityResultOver = true;
                originalFilePath = image.getFilePathOriginal();
                thumbnailFilePath = image.getFileThumbnail();
                thumbnailSmallFilePath = image.getFileThumbnailSmall();
                pbar.setVisibility(View.GONE);
                if (image != null) {
                    Log.i(TAG, "Chosen Image: Is not null");
                    textViewFile.setText(image.getFilePathOriginal());
                    loadImage(imageViewThumbnail, image.getFileThumbnail());
                    loadImage(imageViewThumbSmall, image.getFileThumbnailSmall());
                } else {
                    Log.i(TAG, "Chosen Image: Is null");
                }
            }
        });
    }

    @Override
    public void onImagesChosen(final ChosenImages images) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "On Images Chosen: " + images.size());
                onImageChosen(images.getImage(0));
            }
        });
    }

    private void loadImage(ImageView iv, final String path) {
        Picasso.with(ImageChooserActivity.this)
                .load(Uri.fromFile(new File(path)))
                .fit()
                .centerInside()
                .into(iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "Picasso Success Loading Thumbnail - " + path);
                    }

                    @Override
                    public void onError() {
                        Log.i(TAG, "Picasso Error Loading Thumbnail Small - " + path);
                    }
                });
    }

    @Override
    public void onError(final String reason) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "OnError: " + reason);
                pbar.setVisibility(View.GONE);
                Toast.makeText(ImageChooserActivity.this, reason,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // Should be called if for some reason the ImageChooserManager is null (Due
    // to destroying of activity for low memory situations)
    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "Saving Stuff");
        Log.i(TAG, "File Path: " + filePath);
        Log.i(TAG, "Chooser Type: " + chooserType);
        outState.putBoolean("activity_result_over", isActivityResultOver);
        outState.putInt("chooser_type", chooserType);
        outState.putString("media_path", filePath);
        outState.putString("orig", originalFilePath);
        outState.putString("thumb", thumbnailFilePath);
        outState.putString("thumbs", thumbnailSmallFilePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("chooser_type")) {
                chooserType = savedInstanceState.getInt("chooser_type");
            }
            if (savedInstanceState.containsKey("media_path")) {
                filePath = savedInstanceState.getString("media_path");
            }
            if (savedInstanceState.containsKey("activity_result_over")) {
                isActivityResultOver = savedInstanceState.getBoolean("activity_result_over");
                originalFilePath = savedInstanceState.getString("orig");
                thumbnailFilePath = savedInstanceState.getString("thumb");
                thumbnailSmallFilePath = savedInstanceState.getString("thumbs");
            }
        }
        Log.i(TAG, "Restoring Stuff");
        Log.i(TAG, "File Path: " + filePath);
        Log.i(TAG, "Chooser Type: " + chooserType);
        Log.i(TAG, "Activity Result Over: " + isActivityResultOver);
        if (isActivityResultOver) {
            populateData();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void populateData() {
        Log.i(TAG, "Populating Data");
        loadImage(imageViewThumbnail, thumbnailFilePath);
        loadImage(imageViewThumbSmall, thumbnailSmallFilePath);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Activity Destroyed");
    }

    private void checkForSharedImage(Intent intent) {
        if (intent != null) {
            if (intent.getAction() != null && intent.getType() != null && intent.getExtras() != null) {
                ImageChooserManager m = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE);
                m.setImageChooserListener(this);

                m.submit(ChooserType.REQUEST_PICK_PICTURE, IntentUtils.getIntentForMultipleSelection(intent));
            }
        }
    }
}
