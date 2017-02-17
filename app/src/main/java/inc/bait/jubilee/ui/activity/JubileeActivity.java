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

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import inc.bait.jubilee.JubileeApp;
import inc.bait.jubilee.model.appmodel.Account;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.helper.util.LogUtil;
import inc.bait.jubilee.model.notification.Notification;
import inc.bait.jubilee.model.notification.NotificationDialog;
import inc.bait.jubilee.model.persistence.net.Address;
import inc.bait.jubilee.model.persistence.net.NetData;
import inc.bait.jubilee.model.persistence.pref.AppPreferences;
import inc.bait.jubilee.model.view.animator.Anim;
import inc.bait.jubilee.model.view.animator.AnimDuration;
import inc.bait.jubilee.model.view.animator.Animator;

/**
 * Created by yoctopus on 1/23/17.
 */

public class JubileeActivity extends AppCompatActivity
        implements Transaction.OnCompleteListener<NetData> {
    private String TAG =
            LogUtil.makeLogTag(JubileeActivity.class);
    private JubileeApp app;
    private boolean hasBackButton = false;
    private boolean shouldBeOnline = false;
    private Notification notification;
    private Animator animator;
    private Handler handler;
    private AppPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        notification = new Notification(this);
        app = (JubileeApp) getApplication();
        preferences = new AppPreferences(this);
    }



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (isHasBackButton()) {
            ActionBar actionBar =
                    getSupportActionBar();
            if (actionBar != null) {
                actionBar
                        .setDisplayHomeAsUpEnabled(
                                true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isShouldBeOnline()) {
            if (!isOnline()) {
                getNotification().addTitle("Network Request")
                        .addMessage("Please enable network to continue")
                        .addPositiveButton(
                                new NotificationDialog.DialogButton("Ok",
                                        new NotificationDialog.DialogButton
                                                .ButtonListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent =
                                                        new Intent(
                                                                Settings
                                                                        .ACTION_DATA_ROAMING_SETTINGS);
                                                startActivity(
                                                        intent);
                                            }
                                        }))
                        .notify(Notification.DIALOG);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.i(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo info =
                manager.getActiveNetworkInfo();
        return info != null &&
                info.isConnected();
    }
    public void shakeView(@NonNull View view) {
        animator = new Animator(view,
                Anim.Attention.shake(),
                AnimDuration.small());
        animator.animate();
    }

    public View getView(@IdRes int id) {
        return findViewById(id);
    }

    public void shakeView(@IdRes int id) {
        shakeView(getView(id));
    }

    public Intent getIntent(@NonNull Class activity) {
        return new Intent(this,
                activity);
    }


    @Override
    public void OnComplete(int id,
                           NetData data) {
    }

    public boolean isShouldBeOnline() {
        return shouldBeOnline;
    }

    public void setShouldBeOnline() {
        this.shouldBeOnline = true;
    }

    public JubileeApp getApp() {
        return app;
    }

    public Account getAccount() {
        return app.getAccount();
    }

    public void setAccount(Account account) {
        app.setAccount(account);
    }

    public Address getjubileeWebAddress() {
        return Address.JUBILEE_WEBSITE;
    }

    public AppPreferences getPreferences() {
        return preferences;
    }

    public boolean isHasBackButton() {
        return hasBackButton;
    }

    public void setHasBackButton(boolean hasBackButton) {
        this.hasBackButton = hasBackButton;
    }

    public Animator getAnimator() {
        return animator;
    }

    public Handler getHandler() {
        return handler;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
