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

package inc.bait.jubilee.model.persistence.net.field;

/**
 * Created by yoctopus on 1/21/17.
 */

public enum RegisterField {
    PHONE(LoginField.PHONE.toString()),
    PASSWORD(LoginField.PASSWORD.toString()),
    F_NAME("fname"),
    S_NAME("sname"),
    COUNTY("county");
    String field;
    RegisterField(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return  field;
    }
    /*
    * params.put("fname",
                                f_name);
                        params.put("sname",
                                s_name);
                        params.put("phone",
                                phone);
                        params.put("county",
                                county);
                        params.put("password",
                                password);
    * */
}
