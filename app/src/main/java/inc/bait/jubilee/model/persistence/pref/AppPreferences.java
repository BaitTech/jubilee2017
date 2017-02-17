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

package inc.bait.jubilee.model.persistence.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import inc.bait.jubilee.model.error.InvalidPreference;


/**
 * Created by octopus on 9/27/16.
 */
public class AppPreferences {
    private SharedPreferences preferences;
    public AppPreferences(Context context) {
        preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }
    public void savePreference(Preference preference)
            throws InvalidPreference {
        SharedPreferences.Editor editor = preferences.edit();
        if (preference.getData() instanceof  String) {
            editor.putString(preference.getName(),
                    (String) preference.getData());
        }
        else if ( preference.getData() instanceof Integer){
           editor.putInt(preference.getName(),
                   (Integer) preference.getData());
        }
        else if (preference.getData() instanceof Boolean) {
            editor.putBoolean(preference.getName(),
                    (Boolean) preference.getData());
        }
        else if (preference.getData() instanceof Long) {
            editor.putLong(preference.getName(),
                    (Long) preference.getData());
        }
        else if (preference.getData() instanceof Float) {
            editor.putFloat(preference.getName(),
                    (Float) preference.getData());
        }
        else {
            throw new InvalidPreference(preference);
        }
        editor.apply();
    }
    public Preference getPreference(Pref pref,
                                    Object type) {
        Preference preference = null;
        if (type instanceof String) {
            preference = new Preference(pref,
                    preferences.getString(pref.toString(),
                            (String) type));
        }
        else if (type instanceof Integer) {
            preference = new Preference(pref,
                    preferences.getInt(pref.toString(),
                            (Integer) type));
        }
        else if (type instanceof Boolean) {
            preference = new Preference(pref,
                    preferences.getBoolean(pref.toString(),
                            (Boolean) type));
        }
        else if (type instanceof Long) {
            preference = new Preference(pref,
                    preferences.getLong(pref.toString(),
                            (Long) type));
        }
        else if (type instanceof Float) {
            preference = new Preference(pref,
                    preferences.getFloat(pref.toString(),
                            (Float) type));
        }
        return preference;
    }


}
