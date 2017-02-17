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

package inc.bait.jubilee.model.notification;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;

import inc.bait.jubilee.R;


/**
 * Created by yoctopus on 11/13/16.
 */
public class NotificationDialog extends AlertDialog {

    private String title, message;
    private DialogButton positive, negative, neutral;
    private CancelListener cancelListener;

    NotificationDialog(Context context,
                       String title,
                       String message,
                       DialogButton positive,
                       DialogButton negative,
                       DialogButton neutral) {
        super(context);
        this.title = title;
        this.message = message;
        this.positive = positive;
        this.negative = negative;
        this.neutral = neutral;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
    }

    public CancelListener getCancelListener() {
        return cancelListener;
    }

    void setCancelListener(CancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public NotificationDialog(Context context,
                              @StringRes int title,
                              @StringRes int message,
                              DialogButton positive,
                              DialogButton negative,
                              DialogButton neutral) {
        this(context,
                context.getResources().getString(title),
                context.getResources().getString(message),
                positive,
                negative,
                neutral);
    }

    @Override
    public void show() {
        Builder builder = new Builder(getContext(),
                R.style.NotificationDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        if (positive != null) {
            builder.setPositiveButton(positive.getBtnText(),
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface,
                                            int i) {
                            positive.getListener().onClick(null);
                        }
                    });
        }
        if (negative != null) {
            builder.setNegativeButton(negative.getBtnText(),
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface,
                                            int i) {
                            negative.getListener().onClick(null);
                        }
                    });
        }
        if (neutral != null) {
            builder.setNeutralButton(neutral.getBtnText(),
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface,
                                            int i) {
                            neutral.getListener().onClick(null);
                        }
                    });
        }
        if (cancelListener != null) {
            builder.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    cancelListener.doAction(dialogInterface);
                }
            });
        }
        builder.show();

    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);
        title = null;
        message = null;
        positive = null;
        negative = null;
        neutral = null;
        cancelListener = null;
    }

    public static class DialogButton {
        private String btnText;
        private ButtonListener listener;
        public DialogButton(String btnText,
                            ButtonListener listener) {
            this.btnText = btnText;
            this.listener = listener;
        }

        String getBtnText() {
            return btnText;
        }

        public void setBtnText(String btnText) {
            this.btnText = btnText;
        }

        public ButtonListener getListener() {
            return listener;
        }

        public void setListener(ButtonListener listener) {
            this.listener = listener;
        }

        public interface ButtonListener {
            void onClick(View v);
        }
    }
    public interface CancelListener {
        void doAction(DialogInterface dialogInterface);
    }
}
