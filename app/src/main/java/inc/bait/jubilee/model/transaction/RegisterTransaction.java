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
import inc.bait.jubilee.model.persistence.net.model.RegistrationData;

/**
 * Created by yoctopus on 1/21/17.
 */

public class RegisterTransaction extends
        Transaction.NetTransaction<RegistrationData> {
    private Account account;

    public RegisterTransaction(Application application,
                               Account account) {
        super(application,
                REGISTER_ACCOUNT);
        this.account = account;
    }


    @Override
    public OnUpdateListener<RegistrationData, Integer> getOnUpdateListener() {
        return null;
    }

    @Override
    public CallBacks<RegistrationData, Integer> getCallBacks() {
        return new CallBacks<RegistrationData, Integer>() {
            @Override
            public void OnStart() {

            }

            @Override
            public RegistrationData OnExecute() {
                String result =
                        getNetBridge().registerAccount(account);
                return new RegistrationData(result);
            }

            @Override
            public void OnProgress(Integer... integer) {

            }

            @Override
            public void OnEnd(RegistrationData registrationData) {

            }
        };
    }
}
