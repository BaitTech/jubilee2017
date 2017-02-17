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

import inc.bait.jubilee.model.appmodel.News;
import inc.bait.jubilee.model.persistence.net.NetData;

/**
 * Created by yoctopus on 2/2/17.
 */

public class NewsData extends NetData {
    private ArrayList<News> newses;
    public NewsData(String dataName) {
        super(dataName);
        newses = new ArrayList<>();
    }

    public ArrayList<News> getNewses() {
        return newses;
    }

    public void setNewses(ArrayList<News> newses) {
        this.newses = newses;
    }
}
