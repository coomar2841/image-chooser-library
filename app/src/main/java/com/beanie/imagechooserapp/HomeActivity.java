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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class HomeActivity extends Activity {
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        adView = (AdView) findViewById(R.id.adView);

        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(Config.TEST_DEVICE_ID_1)
                .addTestDevice(Config.TEST_DEVICE_ID_2)
                .addTestDevice(Config.TEST_GALAXY_NEXUS);
        AdRequest request = builder.build();
        adView.loadAd(request);
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

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
