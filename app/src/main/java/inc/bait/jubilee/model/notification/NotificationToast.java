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

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;


/**
 * Created by octopus on 10/26/16.
 */
class NotificationToast {
    private Toast toast;



    @SuppressLint("InflateParams")
    NotificationToast(Context context,
                      String message) {

        toast = new Toast(context);
        toast.setText(message);
    }
    void setDuration(@Nullable Duration duration) {
        if (duration == null) {
            toast.setDuration(Toast.LENGTH_SHORT);
            return;
        }
        switch (duration.getLength()) {
            case Duration.SHO: {
                toast.setDuration(Toast.LENGTH_SHORT);
                break;
            }
            case Duration.LON: {
                toast.setDuration(Toast.LENGTH_LONG);
                break;
            }
        }
    }
    void show() {
        toast.show();

    }
}
