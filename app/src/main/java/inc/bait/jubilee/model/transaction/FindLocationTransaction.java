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

import android.app.Activity;

import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.persistence.net.model.LocationData;

/**
 * Created by yoctopus on 1/30/17.
 */

public class FindLocationTransaction extends
        Transaction.NetTransaction<LocationData> {
    public FindLocationTransaction(Activity activity) {
        super(activity,
                LOC_FIND);
    }


    @Override
    public OnUpdateListener<LocationData,
            Integer> getOnUpdateListener() {
        return null;
    }

    @Override
    public CallBacks<LocationData,
            Integer> getCallBacks() {
        return new CallBacks<LocationData,
                Integer>() {
            @Override
            public void OnStart() {

            }

            @Override
            public LocationData OnExecute() {
                LocationData data =
                        new LocationData("location");
                data.setData(
                        getNetBridge().getCurrentLocation());
                return data;
            }

            @Override
            public void OnProgress(Integer... integer) {

            }

            @Override
            public void OnEnd(LocationData locationData) {

            }
        };
    }
}
