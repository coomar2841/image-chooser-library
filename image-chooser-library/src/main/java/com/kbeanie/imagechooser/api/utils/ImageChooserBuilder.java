package com.kbeanie.imagechooser.api.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.kbeanie.imagechooser.api.ChooserType;

public class ImageChooserBuilder extends Builder {
	private OnClickListener listener;

	private Context context;

	private String title;

	private String titleGalleryOption;

	private String titleTakePictureOption;

	@SuppressLint("NewApi")
	public ImageChooserBuilder(Context context, int theme,
			OnClickListener listener) {
		super(context, theme);
		this.listener = listener;
		this.context = context;
		init();
	}

	public ImageChooserBuilder(Context context, OnClickListener listener) {
		super(context);
		this.listener = listener;
		this.context = context;
		init();
	}

	public void setDialogTitle(String title) {
		this.title = title;
	}

	public void setDialogTitle(int resId) {
		this.title = context.getString(resId);
	}

	public void setTitleGalleryOption(String titleGalleryOption) {
		this.titleGalleryOption = titleGalleryOption;
	}

	public void setTitleGalleryOption(int resId) {
		this.titleGalleryOption = context.getString(resId);
	}

	public void setTitleTakePictureOption(String titleTakePictureOption) {
		this.titleTakePictureOption = titleTakePictureOption;
	}

	public void setTitleTakePictureOption(int resId) {
		this.titleTakePictureOption = context.getString(resId);
	}

	private void init() {
		title = "Choose an option";
		titleGalleryOption = "Choose from Gallery";
		titleTakePictureOption = "Take a picture";
	}

	@Override
	public AlertDialog show() {
		setTitle(title);
		CharSequence[] titles = { titleGalleryOption, titleTakePictureOption };
		setItems(titles, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					listener.onClick(dialog, ChooserType.REQUEST_PICK_PICTURE);
				} else if (which == 1) {
					listener.onClick(dialog,
							ChooserType.REQUEST_CAPTURE_PICTURE);
				}
			}
		});
		return super.show();
	}

}
