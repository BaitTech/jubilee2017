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

package inc.bait.jubilee.model.persistence.net.model;

import java.util.ArrayList;

import inc.bait.jubilee.model.appmodel.Event;
import inc.bait.jubilee.model.persistence.net.NetData;

/**
 * Created by yoctopus on 1/28/17.
 */

public class EventsData extends NetData {
    private ArrayList<Event> events;
    public EventsData(String dataName) {
        super(dataName);
        events = new ArrayList<>();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
