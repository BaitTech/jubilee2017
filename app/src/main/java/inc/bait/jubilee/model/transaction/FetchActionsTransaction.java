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
import inc.bait.jubilee.model.persistence.net.model.ActionsData;


public class FetchActionsTransaction extends
        Transaction.NetTransaction<ActionsData> {


    public FetchActionsTransaction(Application application) {
        super(application,
                FETCH_ACTIONS);
    }


    @Override
    public OnUpdateListener<ActionsData,
            Integer> getOnUpdateListener() {
        return new OnUpdateListener<ActionsData,
                Integer>() {
            @Override
            public Integer[] OnUpdate(ActionsData... data) {
                return null;
            }
        };
    }

    @Override
    public CallBacks<ActionsData,
            Integer> getCallBacks() {
        return new CallBacks<ActionsData,
                Integer>() {
            @Override
            public void OnStart() {

            }

            @Override
            public ActionsData OnExecute() {
                ActionsData netData =
                        new ActionsData("Actions");
                netData.setActions(getNetBridge().getActions());
                return netData;
            }

            @Override
            public void OnProgress(Integer... integer) {

            }

            @Override
            public void OnEnd(ActionsData data) {

            }
        };
    }
}
