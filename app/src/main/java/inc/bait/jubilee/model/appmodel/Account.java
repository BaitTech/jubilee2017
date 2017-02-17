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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yoctopus on 1/21/17.
 */

public class Account implements Parcelable {
    private int id;
    private String unique_id;
    private Bitmap profilePicture;
    private String f_name;
    private String s_name;
    private String password;
    private String email;
    private String phone;
    private String county;
    private String username;
    private ActionPoints actionPoints;

    public Account(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }


    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ActionPoints getActionPoints() {
        return actionPoints;
    }

    public void setActionPoints(ActionPoints actionPoints) {
        this.actionPoints = actionPoints;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.unique_id);
        dest.writeParcelable(this.profilePicture, flags);
        dest.writeString(this.f_name);
        dest.writeString(this.s_name);
        dest.writeString(this.password);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.county);
        dest.writeString(this.username);
        dest.writeParcelable(this.actionPoints, flags);
    }

    protected Account(Parcel in) {
        this.id = in.readInt();
        this.unique_id = in.readString();
        this.profilePicture = in.readParcelable(Bitmap.class.getClassLoader());
        this.f_name = in.readString();
        this.s_name = in.readString();
        this.password = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.county = in.readString();
        this.username = in.readString();
        this.actionPoints = in.readParcelable(ActionPoints.class.getClassLoader());
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}
