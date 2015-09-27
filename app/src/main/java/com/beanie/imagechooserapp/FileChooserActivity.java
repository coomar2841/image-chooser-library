package com.beanie.imagechooserapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
public class FileChooserActivity extends BasicActivity implements FileChooserListener {
    private final static String TAG = "FileChooserActivity";
    private FileChooserManager fm;
    private TextView textViewFileDetails;
    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        textViewFileDetails = (TextView) findViewById(R.id.fileDetails);
        pBar = (ProgressBar) findViewById(R.id.pBar);
        pBar.setVisibility(View.INVISIBLE);

        setupAds();
    }

    public void pickFile(View view) {
        fm = new FileChooserManager(this);
        fm.setFileChooserListener(this);
        try {
            pBar.setVisibility(View.VISIBLE);
            fm.choose();
        } catch (Exception e) {
            pBar.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChooserType.REQUEST_PICK_FILE && resultCode == RESULT_OK) {
            if (fm == null) {
                fm = new FileChooserManager(this);
                fm.setFileChooserListener(this);
            }
            Log.i(TAG, "Probable file size: " + fm.queryProbableFileSize(data.getData(), this));
            fm.submit(requestCode, data);
        }
    }

    @Override
    public void onFileChosen(final ChosenFile file) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pBar.setVisibility(View.INVISIBLE);
                populateFileDetails(file);
            }
        });
    }

    @Override
    public void onError(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pBar.setVisibility(View.INVISIBLE);
                Toast.makeText(FileChooserActivity.this, reason, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateFileDetails(ChosenFile file) {
        StringBuffer text = new StringBuffer();
        text.append("File name: " + file.getFileName() + "\n\n");
        text.append("File path: " + file.getFilePath() + "\n\n");
        text.append("Mime type: " + file.getMimeType() + "\n\n");
        text.append("File extn: " + file.getExtension() + "\n\n");
        text.append("File size: " + file.getFileSize() / 1024 + "KB");
        textViewFileDetails.setText(text.toString());
    }
}
