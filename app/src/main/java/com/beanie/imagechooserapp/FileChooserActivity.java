package com.beanie.imagechooserapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenFile;
import com.kbeanie.imagechooser.api.FileChooserListener;
import com.kbeanie.imagechooser.api.FileChooserManager;

/**
 * Created by kbibek on 14/11/14.
 */
public class FileChooserActivity extends Activity implements FileChooserListener {
    private final static String TAG = "FileChooserActivity";
    private AdView adView;
    private FileChooserManager fm;
    private TextView textViewFileDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        adView = (AdView) findViewById(R.id.adView);

        textViewFileDetails = (TextView) findViewById(R.id.fileDetails);

        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(Config.TEST_DEVICE_ID_1)
                .addTestDevice(Config.TEST_DEVICE_ID_2)
                .addTestDevice(Config.TEST_GALAXY_NEXUS);
        AdRequest request = builder.build();
        adView.loadAd(request);
    }

    public void pickFile(View view) {
        fm = new FileChooserManager(this);
        fm.setFileChooserListener(this);
        try {
            fm.choose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChooserType.REQUEST_PICK_FILE && resultCode == RESULT_OK) {
            if (fm == null) {
                fm = new FileChooserManager(this);
            }
            fm.submit(requestCode, data);
        }
    }

    @Override
    public void onFileChosen(final ChosenFile file) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                populateFileDetails(file);
            }
        });
    }

    @Override
    public void onError(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileChooserActivity.this, reason, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateFileDetails(ChosenFile file) {
        StringBuffer text = new StringBuffer();
        text.append("File name: " + file.getFileName() + "\n");
        text.append("File path: " + file.getFilePath() + "\n");
        text.append("Mime type:" + file.getMimeType() + "\n");
        text.append("File size:" + file.getFileSize() + "\n");
        textViewFileDetails.setText(text.toString());
    }
}
