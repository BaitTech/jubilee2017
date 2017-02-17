/*
 *
 *  * ﻿Copyright 2017 Bait Inc
 *  * Licensed under the Apache License, Version 2.0 Jubilee 2017;
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package inc.bait.jubilee.ui.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.helper.ApiHelper;


public class SettingsActivity extends AppCompatPreferenceActivity {

    private final int LOC_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setupActionBar();
    }
    private CheckBoxPreference loc;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        android.preference.Preference account = findPreference("account");
        account.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(SettingsActivity.this,
                        ProfileActivity.class);
                intent.putExtra(ProfileActivity.UPDATE,
                        true);
                startActivity(intent);
                return false;
            }
        });
        loc = (CheckBoxPreference)
                findPreference("request_loc");
        loc.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (loc.isChecked()) {
                    ApiHelper.isLocationAllowed(SettingsActivity.this,
                            new ApiHelper.CallBack() {
                                @Override
                                public void onSuccess() {
                                    loc.setChecked(true);
                                }

                                @Override
                                public void onFailure() {
                                    if (Build.VERSION.SDK_INT >=
                                            Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{
                                                Manifest.permission
                                                        .ACCESS_FINE_LOCATION,
                                                Manifest.permission
                                                        .ACCESS_COARSE_LOCATION},
                                                LOC_REQUEST);
                                    }
                                }
                            });
                }
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOC_REQUEST: {
                if (grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    loc.setChecked(true);
                    break;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
