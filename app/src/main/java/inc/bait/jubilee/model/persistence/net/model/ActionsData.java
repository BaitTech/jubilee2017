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

import inc.bait.jubilee.model.appmodel.Action;
import inc.bait.jubilee.model.persistence.net.NetData;

/**
 * Created by yoctopus on 1/28/17.
 */

public class ActionsData extends NetData {
    private ArrayList<Action> actions;
    public ActionsData(String dataName) {
        super(dataName);

        actions = new ArrayList<>();
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }
}
