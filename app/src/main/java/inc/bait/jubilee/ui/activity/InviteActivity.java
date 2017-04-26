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

package inc.bait.jubilee.ui.activity;

import android.Manifest;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.adapter.ContactAdapter;
import inc.bait.jubilee.model.appmodel.Contact;
import inc.bait.jubilee.model.base.NonNullList;
import inc.bait.jubilee.model.helper.ApiHelper;
import inc.bait.jubilee.model.helper.util.LogUtil;
import inc.bait.jubilee.model.notification.Notification;

public class InviteActivity extends JubileeActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    };
    private final int CONTACTS_REQUEST_CODE = 7;
    private final int SMS_REQUEST_CODE = 8;
    private String TAG = LogUtil.makeLogTag(
            InviteActivity.class);
    private boolean selectedAll = false;
    private NonNullList<Contact> selectedContacts;
    private OnContactSelectedListener selectedListener;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ApiHelper.enableStrictMode(InviteActivity.class);
        setContentView(R.layout.content_invite);
        setHasBackButton(true);
        recyclerView = (RecyclerView)
                findViewById(R.id.rv_contact_list);
        fab = (FloatingActionButton)
                findViewById(R.id.fab);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));
        recyclerView.setItemAnimator(
                new DefaultItemAnimator());

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        selectedContacts =
                new NonNullList<>();
        selectedListener =
                new OnContactSelectedListener() {
                    @Override
                    public void onSelected(Contact contact) {
                        selectedContacts.add(contact);
                    }

                    @Override
                    public void onUnselected(Contact contact) {
                        if (selectedContacts.isEmpty()) {
                            return;
                        }
                        selectedContacts.remove(contact);
                    }
                };
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendSms();
                    }
                });
        ApiHelper.isReadContactsAllowed(this,
                new ApiHelper.CallBack() {
                    @Override
                    public void onSuccess() {
                        loadContacts();
                    }

                    @Override
                    public void onFailure() {
                        if (Build.VERSION.SDK_INT >=
                                Build.VERSION_CODES.M) {
                            requestPermissions(
                                    new String[]{
                                            Manifest.permission
                                                    .READ_CONTACTS},
                                    CONTACTS_REQUEST_CODE);
                        }
                    }
                });
    }

    private void loadContacts() {
        getLoaderManager().initLoader(0,
                null,
                this);
    }

    private void sendSms() {
        LogUtil.e(TAG, "Sending sms");
        ApiHelper.isSendSmsAllowed(
                InviteActivity.this,
                new ApiHelper.CallBack() {
                    @Override
                    public void onSuccess() {
                        inviteContactsBySms(
                                selectedContacts);
                    }

                    @Override
                    public void onFailure() {
                        if (Build.VERSION.SDK_INT >=
                                Build.VERSION_CODES.M) {
                            requestPermissions(
                                    new String[]{
                                            Manifest.permission
                                                    .SEND_SMS},
                                    SMS_REQUEST_CODE);
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_invite,
                menu);
        MenuItem searchItem =
                menu.findItem(R.id.action_search);
        SearchManager searchManager =
                (SearchManager)
                        getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView =
                    (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(
                            getComponentName()));
            SearchView.OnQueryTextListener queryTextListener =
                    new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(
                                String query) {
                            filterContacts(query);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(
                                String newText) {
                            filterContacts(newText);
                            return false;
                        }
                    };
            searchView.setOnQueryTextListener(
                    queryTextListener);
        }
        return true;
    }

    private void filterContacts(final String query) {
        getLoaderManager().initLoader(0,
                null,
                this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_all: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void inviteContactsBySms(NonNullList<Contact> contacts) {
        if (contacts.isEmpty()) {
            LogUtil.e(TAG, "Sending sms, contacts empty");
            return;
        }
        String message =
                "Please feel welcome to join" +
                        "me in supporting " +
                        "jubilee party" +
                        "by installing " +
                        "this app";
        SmsManager manager =
                SmsManager.getDefault();
        try {
            for (Contact contact : contacts) {
                LogUtil.e(TAG,
                        "sending to " +
                                contact.getName());
                manager.sendTextMessage(
                        contact.getPhone(),
                        null,
                        message,
                        null,
                        null);
            }
        } catch (Exception e) {
            new Notification(this)
                    .addTitle("Send failure")
                    .addMessage("Could not send messages")
                    .notify(Notification.DIALOG);
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CONTACTS_REQUEST_CODE: {
                if (grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    loadContacts();
                    break;
                }
            }
            case SMS_REQUEST_CODE: {
                if (grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    sendSms();
                    break;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id,
                                         Bundle args) {
        Uri contentUri =
                ContactsContract.Contacts.CONTENT_URI;
        return new CursorLoader(
                InviteActivity.this,
                contentUri,
                PROJECTION,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        recyclerView.setAdapter(new ContactAdapter(this,
                data,
                selectedListener));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    public interface OnContactSelectedListener {
        void onSelected(Contact contact);
        void onUnselected(Contact contact);
    }
}
