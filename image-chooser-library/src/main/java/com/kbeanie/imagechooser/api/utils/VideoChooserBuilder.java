package com.kbeanie.imagechooser.api.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.annotation.NonNull;

import com.kbeanie.imagechooser.api.ChooserType;

public class VideoChooserBuilder extends Builder {
	private OnClickListener listener;

	private Context context;

	private String title;

	private String titleGalleryOption;

	private String titleCaptureVideoOption;

	@SuppressLint("NewApi")
	public VideoChooserBuilder(Context context, int theme,
			OnClickListener listener) {
		super(context, theme);
		this.listener = listener;
		this.context = context;
        setupDefaultData();
	}

	public VideoChooserBuilder(Context context, OnClickListener listener) {
		super(context);
		this.listener = listener;
		this.context = context;
        setupDefaultData();
	}

	public Builder setDialogTitle(int resId) {
		this.title = context.getString(resId);
        return this;
	}

	public Builder setDialogTitle(String title) {
		this.title = title;
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

	public Builder setTitleCaptureVideoOption(String titleCaptureVideoOption) {
		this.titleCaptureVideoOption = titleCaptureVideoOption;
        return this;
	}

	public Builder setTitleCaptureVideoOption(int resId) {
		this.titleCaptureVideoOption = context.getString(resId);
        return this;
	}

    private void setupDefaultData(){
        title = "Choose an option";
        titleGalleryOption = "Choose from Gallery";
        titleCaptureVideoOption = "Capture Video";
    }

    @NonNull
    @Override
    public AlertDialog create() {
        setTitle(title);
        CharSequence[] titles = { titleGalleryOption, titleCaptureVideoOption };
        setItems(titles, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    listener.onClick(dialog, ChooserType.REQUEST_PICK_VIDEO);
                } else if (which == 1) {
                    listener.onClick(dialog, ChooserType.REQUEST_CAPTURE_VIDEO);
                }
            }
        });

        AlertDialog d = super.create();
        return d;
    }
}
