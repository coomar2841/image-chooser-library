package com.beanie.imagechooserapp.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.beanie.imagechooserapp.R;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

@SuppressLint("NewApi")
public class ImageChooserFragment extends Fragment implements
		ImageChooserListener {
	private ImageChooserManager imageChooserManager;

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
		return view;
	}

	private void chooseImage() {
		int chooserType = ChooserType.REQUEST_PICK_PICTURE;
		imageChooserManager = new ImageChooserManager(this,
				ChooserType.REQUEST_PICK_PICTURE, "myfolder", true);
		imageChooserManager.setImageChooserListener(this);
		try {
			String filePath = imageChooserManager.choose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("On Activity Result", requestCode + "");
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onImageChosen(ChosenImage image) {

	}

	@Override
	public void onError(String reason) {

	}
}
