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
 * Created by yoctopus on 1/21/17.
 */

public class Action implements Parcelable {
    public static final int LOCAL_INTENT = 1;
    public static final int LINK_INTENT = 2;
    private int id;
    private String imageUrl;
    private String title;
    private String description;
    private ActionCount actionCount;
    private ActionPoints actionPoints;
    private String link;

    public Action(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActionCount getActionCount() {
        return actionCount;
    }

    public void setActionCount(ActionCount actionCount) {
        this.actionCount = actionCount;
    }

    public ActionPoints getActionPoints() {
        return actionPoints;
    }

    public void setActionPoints(ActionPoints actionPoints) {
        this.actionPoints = actionPoints;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.imageUrl);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeParcelable(this.actionCount, flags);
        dest.writeParcelable(this.actionPoints, flags);
        dest.writeString(this.link);
    }

    protected Action(Parcel in) {
        this.id = in.readInt();
        this.imageUrl = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.actionCount = in.readParcelable(ActionCount.class.getClassLoader());
        this.actionPoints = in.readParcelable(ActionPoints.class.getClassLoader());
        this.link = in.readString();
    }

    public static final Creator<Action> CREATOR = new Creator<Action>() {
        @Override
        public Action createFromParcel(Parcel source) {
            return new Action(source);
        }

        @Override
        public Action[] newArray(int size) {
            return new Action[size];
        }
    };
}
