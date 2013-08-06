
package com.beanie.imagechooser.api.utils;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.beanie.imagechooser.R;
import com.beanie.imagechooser.api.ChooserType;

public class VideoChooserBuilder extends Builder {
    private OnClickListener listener;

    private Context context;

    public VideoChooserBuilder(Context context, int theme, OnClickListener listener) {
        super(context, theme);
        this.listener = listener;
        this.context = context;
        init();
    }

    public VideoChooserBuilder(Context context, OnClickListener listener) {
        super(context);
        this.listener = listener;
        this.context = context;
        init();
    }

    private void init() {
        setTitle(R.string.lab_choose_option);
        CharSequence[] titles = {
                context.getString(R.string.lab_choose_from_gallery),
                context.getString(R.string.lab_capture_video)
        };
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
    }

}
