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

package inc.bait.jubilee.model.persistence.pref;

/**
 * Created by yoctopus on 11/14/16.
 */
public enum Pref {
    PHONE("account_phone"),
    COUNTY("county"),
    ACCOUNT("account"),
    SHUFFLE("shuffle"),
    REQUEST_LOCATION_PREF("request_loc"),
    PASSWORD("account_password"),
    IS_LOGGED_IN("isLoggedIn"),
    NOTIFICATION_BAR(
            "notification_bar"),
    ACCOUNT_IMAGE_URL("image_url"),
    PREFS_NAME(
            "_Prefs");
    private String name;
    Pref(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
