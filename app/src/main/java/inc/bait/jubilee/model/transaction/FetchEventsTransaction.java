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
import inc.bait.jubilee.model.persistence.net.model.EventsData;


public class FetchEventsTransaction extends
        Transaction.NetTransaction<EventsData> {

    public FetchEventsTransaction(Application application) {
        super(application,
                FETCH_EVENTS);
    }


    @Override
    public OnUpdateListener<EventsData,
            Integer> getOnUpdateListener() {
        return new OnUpdateListener<EventsData,
                Integer>() {
            @Override
            public Integer[] OnUpdate(EventsData... data) {
                return null;
            }
        };
    }

    @Override
    public CallBacks<EventsData, Integer> getCallBacks() {
        return new CallBacks<EventsData, Integer>() {
            @Override
            public void OnStart() {

            }

            @Override
            public EventsData OnExecute() {
                EventsData netData =
                        new EventsData("Events");
                netData.setEvents(getNetBridge().getEvents());
                return netData;
            }

            @Override
            public void OnProgress(Integer... integer) {

            }

            @Override
            public void OnEnd(EventsData data) {

            }
        };
    }
}
