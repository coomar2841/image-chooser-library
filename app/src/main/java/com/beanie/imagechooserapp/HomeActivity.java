/*******************************************************************************
 * Copyright 2013 Kumar Bibek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.beanie.imagechooserapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kbeanie.imagechooser.api.BChooser;
import com.kbeanie.imagechooser.api.BChooserPreferences;

import io.fabric.sdk.android.Fabric;

public class HomeActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_home);
        setupAds();

        // One time call to setup the folder to be used for all files
        BChooserPreferences preferences = new BChooserPreferences(getApplicationContext());
        preferences.setFolderName("ICL");
    }

    public void gotoImageChooser(View view) {
        Intent intent = new Intent(this, ImageChooserActivity.class);
        startActivity(intent);
    }

    public void gotoVideoChooser(View view) {
        Intent intent = new Intent(this, VideoChooserActivity.class);
        startActivity(intent);
    }

    public void gotoMediaChooser(View view) {
        Intent intent = new Intent(this, MediaChooserActivity.class);
        startActivity(intent);
    }

    public void gotoFileChooser(View view) {
        Intent intent = new Intent(this, FileChooserActivity.class);
        startActivity(intent);
    }
}
