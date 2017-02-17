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
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.appmodel.Contact;
import inc.bait.jubilee.model.base.NonNullList;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.error.LoadContactsError;
import inc.bait.jubilee.model.helper.ApiHelper;
import inc.bait.jubilee.model.notification.Notification;
import inc.bait.jubilee.model.transaction.LoadContactsTransaction;
import inc.bait.jubilee.ui.fragments.ContactsListFragment;

public class InviteActivity extends FragmentActivity implements
        ContactsListFragment.OnContactsInteractionListener {
    private final int CONTACTS_REQUEST_CODE = 7;
    private final int SMS_REQUEST_CODE = 8;
    private boolean selectedAll = false;
    private ProgressDialog progressDialog;
    private NonNullList<Contact> selectedContacts;
    private OnContactSelectedListener selectedListener;
    private NonNullList<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApiHelper.enableStrictMode(InviteActivity.class);
        setContentView(R.layout.content_invite);

        ContactsListFragment mContactsListFragment =
                (ContactsListFragment)
                getSupportFragmentManager().findFragmentById(
                        R.id.contact_list);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        selectedContacts =
                new NonNullList<>();
        selectedListener =
                new OnContactSelectedListener() {
                    @Override
                    public void OnSelected(Contact contact) {
                        selectedContacts.add(contact);
                    }

                    @Override
                    public void OnDeselected(Contact contact) {
                        if (selectedContacts.isEmpty()) {
                            return;
                        }
                        selectedContacts.remove(contact);
                    }
                };/*
        inviteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendSms();
                    }
                });*/
        ApiHelper.isReadContactsAllowed(this,
                new ApiHelper.CallBack() {
                    @Override
                    public void onSuccess() {
                        //loadContacts();
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
        progressDialog =
                new ProgressDialog(this);
        progressDialog.setMessage(
                "Loading contacts");
        progressDialog.show();
        Transaction.WithTask(
                new Transaction.Custom() {
                    @Override
                    public void transact() {
                        LoadContactsTransaction.With(
                                InviteActivity.this,
                                new LoadContactsTransaction
                                        .OnLoadListener() {
                                    @Override
                                    public void OnComplete(
                                            NonNullList<Contact> contacts) {
                                        InviteActivity.this.contacts =
                                                contacts;
                                        displayContacts(
                                                contacts);
                                    }
                                },
                                new LoadContactsTransaction
                                        .CallBack<Contact,
                                        LoadContactsError>() {
                                    @Override
                                    public void OnProgress(String message) {

                                    }

                                    @Override
                                    public void OnProgress(int progress) {

                                    }

                                    @Override
                                    public Contact OnError(
                                            LoadContactsError loadContactsError) {

                                        return null;
                                    }


                                })
                                .appLyFilter(
                                        LoadContactsTransaction.Filter
                                                .PHONE_NUMBERS)
                                .load();
                    }
                })
                .execute(2000);
    }

    private void displayContacts(final ArrayList<Contact> contacts1) {
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        /*recyclerView.setAdapter(
                                new ContactsAdapter(contacts1,
                                        selectedListener));
                        assert progressDialog != null;
                        progressDialog.dismiss();
                        */
                    }
                };
        //getHandler().post(runnable);

    }

    private void sendSms() {
        Transaction.WithTask(
                new Transaction.Custom() {
                    @Override
                    public void transact() {
                        if (selectedContacts.isEmpty()) {
                            return;
                        }
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
                })
                .execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
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
            queryTextListener =
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
        }*/
        return true;
    }

    private void filterContacts(String query) {
        if (contacts == null ||
                contacts.isEmpty()) {
            return;
        }
        ArrayList<Contact> contacts1 =
                new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact.getName().contains(
                    query)) {
                contacts1.add(
                        contact);
            }
        }
        displayContacts(contacts1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        int id = item.getItemId();
        if (id == R.id.action_select_all) {
            if (selectedAll) {
                selectedContacts.clear();
            } else {

            }
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void inviteContactsBySms(ArrayList<Contact> contacts) {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CONTACTS_REQUEST_CODE: {
                if (grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //loadContacts();

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
    public void onContactSelected(Uri contactUri) {
    }

    @Override
    public void onSelectionCleared() {

    }


    public interface OnContactSelectedListener {
        void OnSelected(Contact contact);

        void OnDeselected(Contact contact);
    }
}
