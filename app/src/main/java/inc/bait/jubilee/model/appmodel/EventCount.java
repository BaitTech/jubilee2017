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

package inc.bait.jubilee.model.appmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yoctopus on 1/28/17.
 */

public class EventCount implements Parcelable {
    private int count = 5;
    private String type = "views";
    public static EventCount defaultEvent() {
        return new EventCount(5,"views");
    }

    public EventCount(int count,
                      String type) {
        this.count = count;
        this.type = type;
    }


    public EventCount() {
    }

    @Override
    public String toString() {
        return count +
                " "+
                type ;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
        dest.writeString(this.type);
    }

    protected EventCount(Parcel in) {
        this.count = in.readInt();
        this.type = in.readString();
    }

    public static final Creator<EventCount> CREATOR = new Creator<EventCount>() {
        @Override
        public EventCount createFromParcel(Parcel source) {
            return new EventCount(source);
        }

        @Override
        public EventCount[] newArray(int size) {
            return new EventCount[size];
        }
    };
}
