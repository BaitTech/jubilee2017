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

import android.app.PendingIntent;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.StringRes;

import inc.bait.jubilee.model.helper.util.LogUtil;

/**
 * Created by octopus on 7/16/16.
 */
public class Notification {
    public static final int NOTIFICATION_BAR = 0;
    public static final int DIALOG = 1;
    public static final int PROGRESS_DIALOG = 2;
    public static final int TOAST = 3;
    public static final int SOUND = 4;
    public static final int VIBRATE = 5;
    public static final int SNACKBAR = 6;
    private static int id;
    private NotificationType notificationType;
    private Duration duration;

    static {
        id = 0;
    }
    private String TAG =
            LogUtil.makeLogTag(Notification.class);

    private Context context;
    private String title;
    private String message;
    private String audioFileName;
    private NotificationDialog.DialogButton positive;
    private NotificationDialog.DialogButton negative;
    private NotificationDialog.DialogButton neutral;
    private NotificationDialog.DialogButton snackBarButton;
    private NotificationDialog.CancelListener cancelListener;
    private PendingIntent pendingIntent;

    private NotificationDialog notificationDialog;


    public Notification(Context context) {
        this();
        LogUtil.i(TAG, "Notification: ");
        this.setContext(context);
    }

    public Notification() {
        id++;
    }
    public Notification addTitle(String title) {
        this.setTitle(title);
        return this;
    }
    public Notification addTitle(@StringRes int id) {
        return addTitle(getContext()
                .getResources().getString(id));
    }
    public Notification addMessage(String message) {
        this.setMessage(message);
        return this;
    }
    public Notification addMessage(@StringRes int id) {
        return addMessage(getContext()
                .getResources().getString(id));
    }
    public Notification addPendingIntent(
            PendingIntent pendingIntent) {
        this.setPendingIntent(pendingIntent);
        return this;
    }
    public Notification addPositiveButton(
            NotificationDialog.DialogButton positive) {
        this.setPositive(positive);
        return this;
    }

    public Notification addNegativeButton(
            NotificationDialog.DialogButton negative) {
        this.setNegative(negative);
        return this;
    }
    public Notification addNeutralButton(
            NotificationDialog.DialogButton neutral) {
        this.setNeutral(neutral);
        return this;
    }
    public Notification addSnackBarButton(NotificationDialog.DialogButton snackBarButton) {
        this.snackBarButton = snackBarButton;
        return this;
    }
    public Notification addSoundSource(String audioFileName) {
        this.audioFileName = audioFileName;
        return this;
    }
    public Notification addCancelListener(
            NotificationDialog.CancelListener listener) {
        this.cancelListener = listener;
        return this;
    }
    public Notification addType(NotificationType notificationType) {
        this.setNotificationType(notificationType);
        return this;
    }
    public Notification addDuration(
            Duration duration) {
        this.setDuration(duration);
        return this;
    }

    public void setNotificationBundle(String message) {
        this.setMessage(message);
    }

    private void setNotificationBundle(String title,
                                       String message) {
        this.setTitle(title);
        setNotificationBundle(message);
    }

    public void setNotificationBundle(String message,
                                      PendingIntent pendingIntent) {
        setNotificationBundle(message);
        this.setPendingIntent(pendingIntent);
    }

    public void setNotificationBundle(String title,
                                      String message,
                                      NotificationDialog.DialogButton positive,
                                      NotificationDialog.DialogButton negative) {
        setNotificationBundle(title,
                message);
        this.setPositive(positive);
        this.setNegative(negative);
    }
    public void setNotificationBundle(String title,
                                      String message,
                                      NotificationDialog.DialogButton positive,
                                      NotificationDialog.DialogButton negative,
                                      NotificationDialog.DialogButton neutral) {
        setNotificationBundle(title,
                message,
                positive,
                negative);
        setNeutral(neutral);
    }
    public Notification setCancelable(boolean cancelable) {
        if (notificationDialog != null) {
            notificationDialog.setCancelable(cancelable);
        }
        return this;
    }


    public void notify(final int type) {
        LogUtil.i(TAG,
                "notify: "+
                        type);
        switch (type) {
            case NOTIFICATION_BAR: {
                showNotification();
                break;
            }
            case DIALOG: {
                displayDialog();
                break;
            }

            case TOAST: {
                showToast();
                break;
            }
            case SOUND: {
                soundTone();
                break;
            }
            case VIBRATE: {
                vibrate();
                break;
            }
        }
    }

    private void displayDialog() {
        LogUtil.i(TAG,
                "displayDialog: ");
         notificationDialog =
                 new NotificationDialog(getContext(),
                getTitle(),
                getMessage(),
                getPositive(),
                getNegative(),
                getNeutral());
        if (cancelListener != null) {
            notificationDialog.setCancelListener(cancelListener);
        }
        notificationDialog.show();
    }



    public void dismiss() {

        if (notificationDialog != null) {
            notificationDialog.dismiss();
        }
    }


    private void showToast() {
        NotificationToast toast =
                new NotificationToast(getContext(),
                getMessage());

        toast.setDuration(getDuration());
        toast.show();
    }

    private void soundTone() {
        AudioManager meng =
                (AudioManager) getContext().getSystemService(
                        Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume(
                AudioManager.STREAM_NOTIFICATION);
        if (volume != 0) {
               MediaPlayer mediaPlayer = MediaPlayer.create(getContext(),
                        Uri.parse(audioFileName));
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }

    }
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getContext()
                .getSystemService(
                        Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(500);
        }
    }
    private void vibrate(Duration duration) {
        int length;
        switch (duration.getLength()) {
            case Duration.LON : {
                length = 1000;
                break;
            }
            case Duration.SHO: {
                length = 500;
                break;
            }
            default: {
                length = 500;
            }
        }
        Vibrator vibrator = (Vibrator) getContext()
                .getSystemService(
                        Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(length);
        }
    }

    private void showNotification() {
        NotificationBar.notify(getContext(),
                getMessage(),
                getPendingIntent());
    }

    private NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    private Duration getDuration() {
        return duration;
    }

    public void setDuration(
            Duration duration) {
        this.duration = duration;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationDialog.DialogButton getPositive() {
        return positive;
    }

    public void setPositive(
            NotificationDialog.DialogButton positive) {
        this.positive = positive;
    }

    public NotificationDialog.DialogButton getNegative() {
        return negative;
    }

    public void setNegative(
            NotificationDialog.DialogButton negative) {
        this.negative = negative;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }



    public NotificationDialog.DialogButton getNeutral() {
        return neutral;
    }

    public void setNeutral(NotificationDialog.DialogButton neutral) {
        this.neutral = neutral;
    }
}
