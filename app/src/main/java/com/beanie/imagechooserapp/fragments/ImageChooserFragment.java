package com.beanie.imagechooserapp.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beanie.imagechooserapp.R;
import com.crashlytics.android.Crashlytics;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import java.io.File;

@SuppressLint("NewApi")
public class ImageChooserFragment extends Fragment implements
        ImageChooserListener {
    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String mediaPath;

    private ImageView imageViewThumbnail;

    private ImageView imageViewThumbSmall;

    private TextView textViewFile;

    private ProgressBar pbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_image_chooser, null);
        Button buttonChooseImage = (Button) view
                .findViewById(R.id.buttonChooseImage);
        buttonChooseImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        Button buttonTakePicture = (Button) view.findViewById(R.id.buttonTakePicture);
        buttonTakePicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        imageViewThumbnail = (ImageView) view.findViewById(R.id.imageViewThumb);
        imageViewThumbSmall = (ImageView) view.findViewById(R.id.imageViewThumbSmall);
        textViewFile = (TextView) view.findViewById(R.id.textViewFile);

        pbar = (ProgressBar) view.findViewById(R.id.progressBar);
        pbar.setVisibility(View.GONE);

        return view;
    }

    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("media_path")) {
                mediaPath = savedInstanceState.getString("media_path");
            }
            if (savedInstanceState.containsKey("chooser_type")) {
                chooserType = savedInstanceState.getInt("chooser_type");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("On Activity Result", requestCode + "");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (imageChooserManager == null) {
                imageChooserManager = new ImageChooserManager(this, requestCode, true);
                imageChooserManager.setImageChooserListener(this);
                imageChooserManager.reinitialize(mediaPath);
            }
            imageChooserManager.submit(requestCode, data);
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                pbar.setVisibility(View.GONE);
                if (image != null) {
                    textViewFile.setText(image.getFilePathOriginal());
                    imageViewThumbnail.setImageURI(Uri.parse(new File(image
                            .getFileThumbnail()).toString()));
                    imageViewThumbSmall.setImageURI(Uri.parse(new File(image
                            .getFileThumbnailSmall()).toString()));
                }
            }
        });
    }

    @Override
    public void onError(final String reason) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                pbar.setVisibility(View.GONE);
                Toast.makeText(ImageChooserFragment.this.getActivity(), reason,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onImagesChosen(ChosenImages images) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (chooserType != 0) {
            outState.putInt("chooser_type", chooserType);
        }
        if (mediaPath != null) {
            outState.putString("media_path", mediaPath);
        }
    }
}
