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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.appmodel.Account;
import inc.bait.jubilee.model.helper.util.LogUtil;
import inc.bait.jubilee.model.persistence.pref.Pref;
import inc.bait.jubilee.model.persistence.pref.Preference;

public class ProfileActivity extends JubileeActivity {
    private String TAG =
            LogUtil.makeLogTag(ProfileActivity.class);
    public static final String UPDATE = "update";
    private boolean update = false;

    CircularImageView profileImageView;

    ImageView profileAction;

    TextView firstNameTextView;

    TextView secondNameTextView;

    TextView email_phoneTextView;

    TextView county_TextView;

    TextView points_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileImageView = (CircularImageView) getView(R.id.profilePicture);
        profileAction = (ImageView) getView(R.id.profileAction);
        firstNameTextView = (TextView) getView(R.id.f_name_textView);
        secondNameTextView = (TextView) getView(R.id.s_name_textView);
        email_phoneTextView = (TextView) getView(R.id.email_phone_textView);
        county_TextView = (TextView) getView(R.id.county_textview);
        points_textView = (TextView) getView(R.id.pointsTextView);
        setHasBackButton(true);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setTitle("My Account");
        Intent intent = getIntent();
        if (intent.hasExtra(UPDATE)) {
            update  =true;
            setTitle("Update Profile");
        }
        if (update) {
            setShouldBeOnline();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_update_menu,
                menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:{
                updateAccount();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateAccount() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAccount();
        Runnable runnable =
                new Runnable() {
            @Override
            public void run() {
                loadImage();
            }
        };
        getHandler().post(runnable);
    }

    private void loadAccount() {
        Account account = getAccount();
        if (!TextUtils.isEmpty(account.getF_name())) {
            firstNameTextView.setText(account.getF_name());
        }
        if (!TextUtils.isEmpty(account.getS_name())) {
            secondNameTextView.setText(account.getS_name());
        }
        if (!TextUtils.isEmpty(account.getPhone())) {
            email_phoneTextView.setText(account.getPhone());
        }
        Preference county = getPreferences().getPreference(Pref.COUNTY,
                "county");
        if (!TextUtils.isEmpty(account.getCounty())) {
            String txt;
            if (account.getCounty().equals("null")) {
                txt = county.getData() + " County";
            }
            else {
                txt = account.getCounty() + " County";
            }
            county_TextView.setText(txt);
        }
        if (account.getActionPoints() != null) {
            points_textView.setText(account.getActionPoints().toString());
        }

    }
    private void loadImage() {
        Preference imageUrl =
                getPreferences().getPreference(
                        Pref.ACCOUNT_IMAGE_URL,
                "");
        String path = (String) imageUrl.getData();
        if (TextUtils.isEmpty(path)) {
            return;
        }
        ImageLoader loader =
                ImageLoader.getInstance();
        ImageLoaderConfiguration configuration =
                ImageLoaderConfiguration.createDefault(
                        this);
        loader.init(
                configuration);
        path = "file://" + path;
        LogUtil.d(TAG,
                path);
        loader.displayImage(path,
                profileImageView,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri,
                                                 View view) {
                        ProgressBar bar = new ProgressBar(
                                ProfileActivity.this);
                        bar.setLayoutParams(view.getLayoutParams());
                        bar.invalidate();
                        bar.draw(new Canvas());
                    }

                    @Override
                    public void onLoadingFailed(String imageUri,
                                                View view,
                                                FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri,
                                                  View view,
                                                  Bitmap loadedImage) {
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri,
                                                   View view) {
                    }
                });
    }
}
