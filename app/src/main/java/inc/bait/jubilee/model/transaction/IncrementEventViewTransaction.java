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

import inc.bait.jubilee.model.appmodel.Event;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.persistence.net.NetData;

/**
 * Created by yoctopus on 2/9/17.
 */

public class IncrementEventViewTransaction extends
        Transaction.NetTransaction<NetData> {
    private Event event;

    public IncrementEventViewTransaction(Application application,
                                         Event event) {
        super(application,
                INCREMENT_EVENT_VIEW);
        this.event = event;
    }


    @Override
    public OnUpdateListener<NetData,
            Integer> getOnUpdateListener() {
        return null;
    }

    @Override
    public CallBacks<NetData, Integer> getCallBacks() {
        return new CallBacks<NetData, Integer>() {
            @Override
            public void OnStart() {

            }

            @Override
            public NetData OnExecute() {
                getNetBridge().updateEventViews(event);
                return null;
            }

            @Override
            public void OnProgress(Integer... integer) {

            }

            @Override
            public void OnEnd(NetData data) {

            }
        };
    }
}
