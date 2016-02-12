package com.kbeanie.imagechooser.api;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by kbibek on 2/11/16.
 */
public class IntentUtils {

    public static Intent getIntentForMultipleSelection(Intent data) {
        Intent intent = new Intent();
        String action = data.getAction();
        if (action.equals(Intent.ACTION_SEND_MULTIPLE)) {
            ArrayList<Uri> uris = (ArrayList<Uri>) data.getExtras().get(Intent.EXTRA_STREAM);
            intent.putParcelableArrayListExtra("uris", uris);
        } else if (action.equals(Intent.ACTION_SEND)) {
            Uri uri = (Uri) data.getExtras().get(Intent.EXTRA_STREAM);
            intent.setData(uri);
        }
        return intent;
    }
}
