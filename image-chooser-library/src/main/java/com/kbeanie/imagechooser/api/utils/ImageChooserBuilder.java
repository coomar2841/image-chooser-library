package com.kbeanie.imagechooser.api.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.support.annotation.NonNull;

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
        setupDefaultData();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ImageChooserBuilder(Context context, OnClickListener listener) {
		super(context);
		this.listener = listener;
		this.context = context;
        setupDefaultData();
	}

	public Builder setDialogTitle(String title) {
		this.title = title;
        return this;
	}

	public Builder setDialogTitle(int resId) {
		this.title = context.getString(resId);
        return this;
	}

	public Builder setTitleGalleryOption(String titleGalleryOption) {
		this.titleGalleryOption = titleGalleryOption;
        return this;
	}

	public Builder setTitleGalleryOption(int resId) {
		this.titleGalleryOption = context.getString(resId);
        return this;
	}

	public Builder setTitleTakePictureOption(String titleTakePictureOption) {
		this.titleTakePictureOption = titleTakePictureOption;
        return this;
	}

	public Builder setTitleTakePictureOption(int resId) {
		this.titleTakePictureOption = context.getString(resId);
        return this;
	}

    private void setupDefaultData(){
        title = "Choose an option";
        titleGalleryOption = "Choose from Gallery";
        titleTakePictureOption = "Take a picture";
    }

    @NonNull
    @Override
    public AlertDialog create() {
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
        AlertDialog d = super.create();
        return d;
    }
}
