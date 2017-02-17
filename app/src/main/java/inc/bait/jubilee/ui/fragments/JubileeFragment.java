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

package inc.bait.jubilee.ui.fragments;

import android.os.Handler;
import android.support.v4.app.Fragment;

/**
 * Created by yoctopus on 2/9/17.
 */

public abstract class JubileeFragment  extends Fragment {
    public abstract String getTitle();
    private Handler handler = new Handler();
    public void executeRunnable(Runnable runnable) {
        handler.post(runnable);
    }
}
