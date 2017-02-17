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

package inc.bait.jubilee.model.persistence.net;

/**
 * Created by octopus on 10/23/16.
 */
public enum Address {
    JUBILEE(""),
    JUBILEE_EULA("eula.html"),
    JUBILEE_PRIVACY_POLICY("privacy_policy.html"),
    JUBILEE_CREDITS("credits.html"),
    JUBILEE_WEBSITE("https://jubileepamoja.co.ke", 1),
    JUBILEE_LOGIN("request.php"),
    JUBILEE_REGISTER("request.php"),
    JUBILEE_ACTIONS("getactions.php"),
    JUBILEE_EVENTS("getevents.php"),
    JUBILEE_ADD_EVENT_VIEWS("addeventview.php"),
    JUBILEE_UPDATE_EVENT_VIEWS("updateeventviews.php"),
    JUBILEE_UPDATE_USER_POINTS("updateuserpoints.php"),
    JUBILEE_NEWS("getnews.php"),
    JUBILEE_NEWS_IMAGES("images/news/"),
    JUBILEE_ACTION_IMAGES("images/actions/"),
    JUBILEE_EVENT_IMAGES("images/events/");
    String url;
    Address(String url) {
        this.url = ServerAddress.getUrl().concat(url);
    }
    Address(String url, int a) {
        this.url = url;
    }
    @Override
    public String toString() {
        return url ;
    }
}
