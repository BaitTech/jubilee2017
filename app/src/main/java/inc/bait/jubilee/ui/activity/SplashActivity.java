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

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ImageView;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.appmodel.Account;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.helper.util.BitmapUtil;
import inc.bait.jubilee.model.notification.Notification;
import inc.bait.jubilee.model.notification.NotificationDialog;
import inc.bait.jubilee.model.persistence.SessionManager;
import inc.bait.jubilee.model.persistence.net.NetData;
import inc.bait.jubilee.model.persistence.net.model.RegistrationData;
import inc.bait.jubilee.model.transaction.LoginTransaction;


public class SplashActivity extends JubileeActivity {


    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        background = (ImageView) getView(R.id.backgroundImage);
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        BitmapUtil.setBitmapFromAssets(
                                SplashActivity.this,
                                "uhuru_kenyatta_william_ruto.jpg",
                                background);
                    }
                };
        getHandler().post(runnable);
        setShouldBeOnline();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SessionManager sessionManager =
                        new SessionManager(
                                SplashActivity.this);
                boolean logged_in = sessionManager.isLoggedIn();
                if (logged_in) {
                    Account account =
                            sessionManager.getAccount();
                    Transaction tx =
                            new LoginTransaction(
                                    getApp(),
                                    account);
                    tx.setOnCompleteListener(
                            SplashActivity.this);
                    tx.execute();

                } else {
                    startActivity(
                            getIntent(WelcomeActivity.class));
                    finish();
                }
            }
        };
        getHandler().postDelayed(runnable,
                2000);
    }

    @Override
    public void OnComplete(int id,
                           NetData data) {
        super.OnComplete(id,
                data);
        switch (id) {
            case Transaction.NetTransaction
                    .LOGIN_ACCOUNT: {
                if (data != null) {
                    if (data instanceof RegistrationData) {
                        switch (data.getDataName()) {
                            case RegistrationData.SUCCESS: {
                                RegistrationData data1
                                        = (RegistrationData) data;
                                setAccount(data1.getAccount());
                                startActivity(
                                        getIntent(MainActivity.class));
                                finish();
                                break;
                            }
                            case RegistrationData.ERROR: {
                                RegistrationData data1 =
                                        (RegistrationData) data;
                                new Notification(this)
                                        .addTitle("Login Failed")
                                        .addMessage(data1.getMessage())
                                        .addCancelListener(
                                                new NotificationDialog
                                                        .CancelListener() {
                                                    @Override
                                                    public void doAction(
                                                            DialogInterface
                                                                    dialogInterface) {
                                                        startActivity(
                                                                getIntent(
                                                                        LoginActivity.class));


                                                    }
                                                })
                                        .notify(Notification.DIALOG);

                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
