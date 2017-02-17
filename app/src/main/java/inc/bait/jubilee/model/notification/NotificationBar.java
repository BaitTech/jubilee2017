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

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.helper.util.BitmapUtil;
import inc.bait.jubilee.model.persistence.pref.AppPreferences;
import inc.bait.jubilee.model.persistence.pref.Pref;
import inc.bait.jubilee.model.persistence.pref.Preference;


/**
 * Helper class for showing and canceling new message
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
class NotificationBar {
    private static final String NOTIFICATION_TAG = "NewMessage";

    static void notify(final Context context,
                       final String message,
                       PendingIntent pendingIntent) {
        AppPreferences preferences = new AppPreferences(context);
        Preference notification = preferences.getPreference(
                Pref.NOTIFICATION_BAR,
                true);
        if (!(boolean) notification.getData()) {
            return;
        }
        final Bitmap picture = BitmapUtil.getBitmap(context,
                R.drawable.jubilee_icon_ps);
        final String title = "Jubilee 2017";
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(picture)
                .setSmallIcon(R.drawable.jubilee_icon_ps)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notify(context,
                builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context,
                               final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG,
                    0,
                    notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(),
                    notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG,
                    0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
