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

import inc.bait.jubilee.model.appmodel.Account;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.persistence.net.NetData;

/**
 * Created by yoctopus on 1/23/17.
 */

public class LoginTransaction extends
        Transaction.NetTransaction<NetData> {
    private Account account;

    public LoginTransaction(Application application,
                            Account account) {
        super(application,
                LOGIN_ACCOUNT);
        this.account = account;
    }


    @Override
    public OnUpdateListener<NetData,
            Integer> getOnUpdateListener() {
        return null;
    }

    @Override
    public CallBacks<NetData,
            Integer> getCallBacks() {
        return new CallBacks<NetData,
                Integer>() {
            @Override
            public void OnStart() {

            }

            @Override
            public NetData OnExecute() {
                getNetBridge().loginAccount(account);
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
