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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.appmodel.Account;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.notification.Notification;
import inc.bait.jubilee.model.persistence.SessionManager;
import inc.bait.jubilee.model.persistence.net.NetData;
import inc.bait.jubilee.model.persistence.net.model.RegistrationData;
import inc.bait.jubilee.model.transaction.LoginTransaction;


public class LoginActivity extends JubileeActivity {

    TextView registerTextView;

    AutoCompleteTextView phoneEditText;

    EditText passwordEditText;

    Button loginButton;

    View loginForm;

    View loginProgress;
    private String phone_email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerTextView = (TextView) getView(R.id.register_link);
        phoneEditText = (AutoCompleteTextView) getView(R.id.phoneEditText);
        passwordEditText = (EditText) getView(R.id.passwordEditText);
        loginButton = (Button) getView(R.id.login_button);
        loginForm = getView(R.id.login_form);
        loginProgress = getView(R.id.login_progress);

        setHasBackButton(true);
        setShouldBeOnline();

    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode,
                resultCode,
                data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        registerTextView.setOnClickListener(
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(
                                                getIntent(RegistrationActivity.class));
                                        finish();
                                    }
                                });
                        passwordEditText.setOnEditorActionListener(
                                new TextView.OnEditorActionListener() {
                                    @Override
                                    public boolean onEditorAction(TextView textView,
                                                                  int id,
                                                                  KeyEvent keyEvent) {
                                        if (id == R.id.login_button ||
                                                id == EditorInfo.IME_NULL) {
                                            attemptLogin();
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                        loginButton.setOnClickListener(
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        attemptLogin();
                                    }
                                });

                    }
                };
        getHandler().post(runnable);
    }

    private void attemptLogin() {
        // Reset errors.
        phoneEditText.setError(null);
        passwordEditText.setError(null);
        // Store values at the time of the loginTextView attempt.
        String phone = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password,
        // if the user entered one.
        if (!TextUtils.isEmpty(password) &&
                !isPasswordValid(password)) {
            passwordEditText.setError(getString(
                    R.string.error_invalid_password));
            shakeView(passwordEditText);
            focusView = passwordEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError(getString(
                    R.string.error_field_required));
            shakeView(phoneEditText);
            focusView = phoneEditText;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            phoneEditText.setError(getString(
                    R.string.error_invalid_phone));
            shakeView(phoneEditText);
            focusView = phoneEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't
            // attempt loginTextView and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner,
            // and kick off a background task to
            // perform the user loginTextView attempt.
            showProgress(true);
            if (phone.startsWith("+")) {
                phone = phone.substring(1);
            }
            this.phone_email = phone;
            this.password = password;

            Account account =
                    new Account(phone,
                            password);
            login(account);

        }
    }

    private void login(Account account) {
        Transaction tx =
                new LoginTransaction(
                        getApp(),
                        account);
        tx.setOnCompleteListener(this);
        tx.execute();
    }

    private boolean isPhoneValid(String phone) {
        return Patterns.EMAIL_ADDRESS.matcher(phone).matches() ||
                Patterns.PHONE.matcher(phone).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the
        // ViewPropertyAnimator APIs,
        // which allow
        // for very easy animations.
        // If available, use these APIs
        // to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            loginForm.setVisibility(show ?
                    View.GONE :
                    View.VISIBLE);
            loginForm.animate().setDuration(
                    shortAnimTime).alpha(
                    show ?
                            0 :
                            1).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(
                                Animator animation) {
                            loginForm.setVisibility(show ?
                                    View.GONE :
                                    View.VISIBLE);
                        }
                    });

            loginProgress.setVisibility(show ?
                    View.VISIBLE :
                    View.GONE);
            loginProgress.animate().setDuration(
                    shortAnimTime).alpha(
                    show ?
                            1 :
                            0).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loginProgress.setVisibility(show ?
                                    View.VISIBLE :
                                    View.GONE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator
            // APIs are not available, so simply show
            // and hide the relevant UI components.
            loginProgress.setVisibility(show ?
                    View.VISIBLE :
                    View.GONE);
            loginProgress.setVisibility(show ?
                    View.GONE :
                    View.VISIBLE);
        }
    }

    @Override
    public void OnComplete(int id,
                           NetData data) {
        switch (id) {
            case Transaction.NetTransaction.LOGIN_ACCOUNT: {
                if (data != null) {
                    if (data instanceof RegistrationData) {
                        switch (data.getDataName()) {
                            case RegistrationData.SUCCESS: {
                                RegistrationData data1
                                        = (RegistrationData) data;
                                setAccount(data1.getAccount());
                                Account account =
                                        new Account(phone_email,
                                                password);
                                new SessionManager(this)
                                        .saveAccount(account);
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
                                        .notify(Notification.DIALOG);
                                showProgress(false);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}

