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
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.appmodel.Account;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.error.InvalidPreference;
import inc.bait.jubilee.model.helper.ApiHelper;
import inc.bait.jubilee.model.helper.util.BitmapUtil;
import inc.bait.jubilee.model.helper.util.LogUtil;
import inc.bait.jubilee.model.notification.Notification;
import inc.bait.jubilee.model.persistence.SessionManager;
import inc.bait.jubilee.model.persistence.net.NetData;
import inc.bait.jubilee.model.persistence.net.model.RegistrationData;
import inc.bait.jubilee.model.persistence.pref.Pref;
import inc.bait.jubilee.model.persistence.pref.Preference;
import inc.bait.jubilee.model.transaction.RegisterTransaction;
import inc.bait.jubilee.model.view.animator.custom.ButtonAnimator;

public class RegistrationActivity extends JubileeActivity {
    private final int GALLERY_REQUEST_CODE = 3;
    private final int STORAGE_REQUEST_CODE = 7;

    private String phone_email,
            password;
    private String TAG =
            LogUtil.makeLogTag(
                    RegistrationActivity.class);
    private Runnable openGalleryRunnable =
            new Runnable() {
        @Override
        public void run() {
            LogUtil.d(TAG,
                    "run: opening gallery");
            ApiHelper.isReadStorageAllowed(
                    RegistrationActivity.this,
                    new ApiHelper.CallBack() {
                        @Override
                        public void onSuccess() {
                            ApiHelper.isWritStorageAllowed(
                                    RegistrationActivity.this,
                                    new ApiHelper.CallBack() {
                                        @Override
                                        public void onSuccess() {
                                            Intent imageIntent =
                                                    new Intent();
                                            imageIntent.setType(
                                                    "image/*");
                                            imageIntent.setAction(
                                                    Intent.ACTION_GET_CONTENT);
                                            startActivityForResult(
                                                    Intent.createChooser(
                                                            imageIntent,
                                                            "Select Receipt"),
                                                    GALLERY_REQUEST_CODE);
                                        }

                                        @Override
                                        public void onFailure() {
                                            if (Build.VERSION.SDK_INT >=
                                                    Build.VERSION_CODES.M) {
                                                requestPermissions(
                                                        new String[]{
                                                                Manifest.permission
                                                                .WRITE_EXTERNAL_STORAGE},
                                                        STORAGE_REQUEST_CODE);
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onFailure() {
                            if (Build.VERSION.SDK_INT >=
                                    Build.VERSION_CODES.M) {
                                requestPermissions(
                                        new String[]{
                                                Manifest.permission
                                        .READ_EXTERNAL_STORAGE},
                                        STORAGE_REQUEST_CODE);
                            }
                        }
                    });

        }
    };
    TextView loginTextView;
    View registrationProgressView;
    View registrationForm;
    EditText firstNameEditText;
    EditText secondNameEditText;
    EditText passwordEditText;
    EditText password2EditText;
    Button registerButton;
    AutoCompleteTextView phoneEditText;
    CircularImageView profileImageView;
    ImageView profile_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        registrationForm = getView(R.id.register_form);
        registrationProgressView = getView(R.id.register_progress);
        loginTextView = (TextView) getView(R.id.login_link);
        registerButton = (Button) getView(R.id.register_button);
        password2EditText = (EditText) getView(R.id.password2EditText);
        passwordEditText = (EditText) getView(R.id.passwordEditText);
        secondNameEditText = (EditText) getView(R.id.secondNameEditText);
        firstNameEditText = (EditText) getView(R.id.firstNameEditText);
        profile_action = (ImageView) getView(R.id.profileAction);
        profileImageView = (CircularImageView) getView(R.id.profilePicture);
        phoneEditText = (AutoCompleteTextView) getView(R.id.phoneEditText);
        setHasBackButton(true);
        setShouldBeOnline();
    }

    private void loadImage(String path) {
        LogUtil.d(TAG,
                path);
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

    private String getPath(Uri uri) {
        String[] projection = {
                MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri,
                projection,
                null,
                null,
                null);
        if (cursor == null) {
            return null;
        }
        int column_index =
                cursor.getColumnIndexOrThrow(
                MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(
                column_index);
        try {
            cursor.close();
        } catch (Throwable t) {
            LogUtil.w(TAG,
                    "fail to close cursor",
                    t);
        }
        LogUtil.i(TAG,
                "getPath: " +
                        s);
        Preference imagePref =
                new Preference(Pref.ACCOUNT_IMAGE_URL,
                        s);
        try {
            getPreferences().savePreference(imagePref);
        } catch (InvalidPreference invalidPreference) {
            invalidPreference.printStackTrace();
        }
        return s;
    }

    private void openGallery() {
        LogUtil.i(TAG,
                "openGallery: ");
        getHandler().post(
                openGalleryRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Runnable runnable =
                new Runnable() {
            @Override
            public void run() {
                loginTextView.setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(
                                        getIntent(LoginActivity.class));
                                finish();
                            }
                        });
                passwordEditText.setOnEditorActionListener(
                        new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView textView,
                                                          int id,
                                                          KeyEvent keyEvent) {
                                if (id == R.id.register_button ||
                                        id == EditorInfo.IME_NULL) {
                                    attemptLogin();
                                    return true;
                                }
                                return false;
                            }
                        });
                registerButton.setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                attemptLogin();
                            }
                        });
                profile_action.setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ButtonAnimator(v)
                                        .addAnimatorListener(
                                                new inc.bait.jubilee.model
                                                        .view.animator.Animator
                                                        .AnimatorListener() {
                                            @Override
                                            public void onStartAnimator(
                                                    inc.bait.jubilee.model
                                                            .view.animator
                                                            .Animator animator) {
                                            }

                                            @Override
                                            public void onRepeatAnimator(
                                                    inc.bait.jubilee.model
                                                            .view.animator
                                                            .Animator animator) {
                                            }

                                            @Override
                                            public void onStopAnimator(
                                                    inc.bait.jubilee.model
                                                            .view.animator
                                                            .Animator animator) {
                                                requestProfilePicture();
                                            }
                                        })
                                        .animate();
                            }
                        });


            }
        };
        getHandler().post(runnable);
    }



    private void requestProfilePicture() {
        openGallery();
    }

    private void attemptLogin() {
        Runnable runnable =
                new Runnable() {
            @Override
            public void run() {
                // Reset errors.
                firstNameEditText.setError(
                        null);
                secondNameEditText.setError(
                        null);
                phoneEditText.setError(
                        null);
                passwordEditText.setError(
                        null);
                password2EditText.setError(
                        null);
                // Store values at the time of
                // the loginTextView attempt.
                String f_name =
                        firstNameEditText.getText().toString();
                String s_name =
                        secondNameEditText.getText().toString();
                String phone =
                        phoneEditText.getText().toString();
                String password =
                        passwordEditText.getText().toString();
                String password2 =
                        password2EditText.getText().toString();

                boolean cancel = false;
                View focusView = null;
                // Check for a valid password,
                // if the user entered one.
                if (!TextUtils.isEmpty(password) &&
                        !isPasswordValid(password)) {
                    passwordEditText.setError(
                            getString(R.string.error_invalid_password));
                    focusView = passwordEditText;
                    cancel = true;
                }

                // Check for a valid phone address.
                if (TextUtils.isEmpty(phone)) {
                    phoneEditText.setError(
                            getString(R.string.error_field_required));
                    focusView = phoneEditText;
                    cancel = true;
                } else if (!isPhoneValid(phone)) {
                    phoneEditText.setError(
                            getString(R.string.error_invalid_phone));
                    focusView = phoneEditText;
                    cancel = true;
                } else if (TextUtils.isEmpty(password2) ||
                        !password.equals(password2)) {
                    password2EditText.setError("Password mismatch, " +
                            "please try again");
                    focusView = password2EditText;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                    shakeView(focusView);
                } else {
                    showProgress(true);
                    if (phone.startsWith("+")) {
                        phone = phone.substring(1);
                    }
                    RegistrationActivity.this.password =
                            password;
                    RegistrationActivity.this.phone_email =
                            phone;
                    Account account =
                            new Account(phone,
                                    password);
                    account.setF_name(f_name);
                    account.setS_name(s_name);
                    Bitmap profile =
                            ((BitmapDrawable) profileImageView.getDrawable())
                                    .getBitmap();
                    if (isValidBitmap(profile)) {
                        account.setProfilePicture(profile);
                    }
                    Transaction tx = new
                            RegisterTransaction(
                            getApp(),
                            account);
                    tx.setOnCompleteListener(
                            RegistrationActivity.this);
                    tx.execute(2000);
                }
            }
        };
        getHandler().post(runnable);

    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        LogUtil.i(TAG,
                "onActivityResult: ");
        ResultsRunnable resultsRunnable = new ResultsRunnable(
                data,
                requestCode,
                resultCode);
        getHandler().post(resultsRunnable);
    }

    private boolean isValidBitmap(Bitmap bitmap) {
        Bitmap jubilee = BitmapUtil.getBitmap(this,
                R.drawable.default_profile_ps);
        return BitmapUtil.getBytes(bitmap) !=
                BitmapUtil.getBytes(jubilee);
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
        // ViewPropertyAnimator APIs, which allow
        // for very easy animations. If
        // available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime =
                    getResources().getInteger(
                            android.R.integer.config_shortAnimTime);

            registrationForm.setVisibility(show ?
                    View.GONE :
                    View.VISIBLE);
            registrationForm.animate().setDuration(
                    shortAnimTime).alpha(
                    show ?
                            0 :
                            1).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            registrationForm.setVisibility(show ?
                                    View.GONE :
                                    View.VISIBLE);
                        }
                    });

            registrationProgressView.setVisibility(show ?
                    View.VISIBLE :
                    View.GONE);
            registrationProgressView.animate().setDuration(
                    shortAnimTime).alpha(
                    show ?
                            1 :
                            0).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            registrationProgressView.setVisibility(show ?
                                    View.VISIBLE :
                                    View.GONE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs
            // are not available, so simply show
            // and hide the relevant UI components.
            registrationProgressView.setVisibility(show ?
                    View.VISIBLE :
                    View.GONE);
            registrationForm.setVisibility(show ?
                    View.GONE :
                    View.VISIBLE);
        }
    }

    @Override
    public void OnComplete(int id,
                           NetData data) {
        switch (id) {
            case Transaction.NetTransaction.REGISTER_ACCOUNT: {
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
                                    .addTitle("Registration Failed")
                                    .addMessage(data1.getMessage())
                                    .notify(Notification.DIALOG);
                            showProgress(false);
                            break;
                        }
                    }
                }
            }
        }
        super.OnComplete(id,
                data);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST_CODE: {
                if (grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                    break;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
    }

    private class ResultsRunnable implements Runnable {
        private Intent data;
        private int requestCode;
        private int resultCode;

        ResultsRunnable(Intent data,
                        int requestCode,
                        int resultCode) {
            LogUtil.i(TAG,
                    "ResultsRunnable: ");
            this.data = data;
            this.requestCode = requestCode;
            this.resultCode = resultCode;
        }

        @Override
        public void run() {
            switch (requestCode) {

                case GALLERY_REQUEST_CODE: {
                    LogUtil.i(TAG,
                            "run: " +
                                    requestCode);
                    if (resultCode == RESULT_OK) {
                        loadImage(getPath(
                                data.getData()));
                    }
                    break;
                }

            }
        }
    }
}

