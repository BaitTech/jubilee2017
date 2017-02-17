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

package inc.bait.jubilee.model.transaction;

import android.app.Application;

import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.persistence.net.model.NewsData;

public class FetchNewsTransaction extends
        Transaction.NetTransaction<NewsData> {
    public FetchNewsTransaction(Application application) {
        super(application,
                FETCH_NEWS);
    }


    @Override
    public OnUpdateListener<NewsData,
            Integer> getOnUpdateListener() {
        return null;
    }

    @Override
    public CallBacks<NewsData,
            Integer> getCallBacks() {
        return new CallBacks<NewsData,
                Integer>() {
            @Override
            public void OnStart() {

            }

            @Override
            public NewsData OnExecute() {
                NewsData newsData =
                        new NewsData("news");
                newsData.setNewses(getNetBridge().getNews());
                return newsData;
            }

            @Override
            public void OnProgress(Integer... integer) {

            }

            @Override
            public void OnEnd(NewsData data) {

            }
        };
    }
}
