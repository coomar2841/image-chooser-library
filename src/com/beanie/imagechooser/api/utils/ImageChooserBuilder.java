
package com.beanie.imagechooser.api.utils;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.beanie.imagechooser.R;
import com.beanie.imagechooser.api.ChooserType;

public class ImageChooserBuilder extends Builder {
    private OnClickListener listener;

    public ImageChooserBuilder(Context context, int theme, OnClickListener listener) {
        super(context, theme);
        this.listener = listener;
        init();
    }

    public ImageChooserBuilder(Context context, OnClickListener listener) {
        super(context);
        this.listener = listener;
        init();
    }

    private void init() {
        setTitle(R.string.lab_choose_option);
        CharSequence[] titles = {
                getContext().getString(R.string.lab_choose_from_gallery),
                getContext().getString(R.string.lab_take_picture)
        };
        setItems(titles, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    listener.onClick(dialog, ChooserType.REQUEST_PICK_PICTURE);
                } else if (which == 1) {
                    listener.onClick(dialog, ChooserType.REQUEST_CAPTURE_PICTURE);
                }
            }
        });
    }

}
