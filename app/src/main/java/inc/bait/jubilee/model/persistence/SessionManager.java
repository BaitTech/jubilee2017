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

package inc.bait.jubilee.model.persistence;

import android.content.Context;

import inc.bait.jubilee.model.appmodel.Account;
import inc.bait.jubilee.model.error.InvalidPreference;
import inc.bait.jubilee.model.helper.util.LogUtil;
import inc.bait.jubilee.model.persistence.pref.AppPreferences;
import inc.bait.jubilee.model.persistence.pref.Pref;
import inc.bait.jubilee.model.persistence.pref.Preference;

public class SessionManager {
	// LogCat tag
	private static String TAG =
			LogUtil.makeLogTag(SessionManager.class);
	private AppPreferences preferences;

	public SessionManager(Context context) {
		preferences = new AppPreferences(context);

	}

	public void setLogin(boolean isLoggedIn) {
		Preference preference =
				new Preference(Pref.IS_LOGGED_IN,
				isLoggedIn);
		try {
			preferences.savePreference(preference);
		} catch (InvalidPreference invalidPreference) {
			invalidPreference.printStackTrace();
		}
		LogUtil.d(TAG, "User login session modified!");
	}
	
	public boolean isLoggedIn(){
		Preference preference =
				preferences.getPreference(Pref.IS_LOGGED_IN,
				false);
		return (boolean) preference.getData();
	}
	public void saveAccount(Account account) {
		Preference phone =
				new Preference(Pref.PHONE,
						account.getPhone());
		Preference pass =
				new Preference(Pref.PASSWORD,
						account.getPassword());
		try {
			preferences.savePreference(phone);
			preferences.savePreference(pass);
		} catch (InvalidPreference invalidPreference) {
			invalidPreference.printStackTrace();
		}
	}
	public Account getAccount() {
		Preference phone =
				preferences.getPreference(
						Pref.PHONE,
						"phone");
		String phones = (String) phone.getData();
		if (phones.length() < 10) {
			phones = String.valueOf(0).concat(phones);
		}
		Preference password =
				preferences.getPreference(
						Pref.PASSWORD,
						"password");
		return new Account(phones,
				(String) password.getData());
	}
}
