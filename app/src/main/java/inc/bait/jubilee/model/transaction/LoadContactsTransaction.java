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

package inc.bait.jubilee.model.transaction;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.util.ArrayList;

import inc.bait.jubilee.model.appmodel.Contact;
import inc.bait.jubilee.model.base.NonNullList;
import inc.bait.jubilee.model.error.JubileeException;
import inc.bait.jubilee.model.error.LoadContactsError;

public class LoadContactsTransaction {
    private Context context;
    private NonNullList<Contact> contacts;
    private OnLoadListener listener;
    private Handler handler;
    private Cursor cursor;
    private CallBack<Contact, LoadContactsError> callBack;
    private int counter = 0;
    private Filter filter;
    private LoadContactsTransaction(Context context) {
        this.context = context;
        this.contacts = new NonNullList<>();
        handler = new Handler();
        filter = Filter.NONE;
    }

    private LoadContactsTransaction(Context context,
                                    OnLoadListener listener) {
        this(context);
        this.listener = listener;
    }
    public static LoadContactsTransaction With(Context context,
                                               OnLoadListener listener,
                                               CallBack callBack) {
        LoadContactsTransaction transaction =
                new LoadContactsTransaction(context,
                        listener);
        transaction.setCallBack(callBack);
        return transaction;

    }
    public LoadContactsTransaction appLyFilter(Filter filter) {
        LoadContactsTransaction.this.filter = filter;
        return this;
    }

    public void load() {
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        Uri contentUri =
                                ContactsContract.Contacts.CONTENT_URI;
                        String ID =
                                ContactsContract.Contacts._ID;
                        String DISPLAY_NAME =
                                ContactsContract.Contacts.DISPLAY_NAME;
                        String HAS_PHONE_NUMBER =
                                ContactsContract.Contacts.HAS_PHONE_NUMBER;
                        Uri phoneContentUri =
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                        String phoneContactID =
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
                        String number =
                                ContactsContract.CommonDataKinds.Phone.NUMBER;
                        Uri emailContentUri =
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI;
                        String emailContact =
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID;
                        String data =
                                ContactsContract.CommonDataKinds.Email.DATA;
                        ContentResolver resolver =
                                context.getContentResolver();
                        cursor = resolver.query(contentUri,
                                null,
                                null,
                                null,
                                null);
                        assert cursor != null;
                        if (cursor.getCount() > 0) {
                            String name;
                            String phone = null,
                                    email = null;
                            while (cursor.moveToNext()) {
                                handler.post(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                assert callBack !=
                                                        null;
                                                callBack.OnProgress(
                                                        "Reading contact : " +
                                                                counter++ +
                                                                "/" +
                                                                cursor.getCount());
                                                callBack.OnProgress(
                                                        counter);
                                            }
                                        });
                                String contact_id =
                                        cursor.getString(
                                                cursor.getColumnIndex(
                                                        ID));
                                name = cursor.getString(
                                        cursor.getColumnIndex(
                                                DISPLAY_NAME));
                                int hasPhone =
                                        Integer.parseInt(
                                                cursor.getString(
                                                        cursor.getColumnIndex(
                                                                HAS_PHONE_NUMBER)));
                                if (hasPhone > 0) {
                                    Cursor phoneCursor =
                                            resolver.query(phoneContentUri,
                                                    null,
                                                    phoneContactID +
                                                            "= ?",
                                                    new String[]{contact_id},
                                                    null);
                                    assert phoneCursor !=
                                            null;
                                    while (phoneCursor.moveToNext()) {
                                        phone = phoneCursor.getString(
                                                phoneCursor.getColumnIndex(
                                                        number
                                                ));
                                    }
                                    phoneCursor.close();
                                    Cursor emailCursor =
                                            resolver.query(emailContentUri,
                                                    null,
                                                    emailContact + " = ?",
                                                    new String[]{contact_id},
                                                    null);
                                    assert emailCursor != null;
                                    while (emailCursor.moveToNext()) {
                                        email = emailCursor.getString(
                                                emailCursor.getColumnIndex(
                                                        data
                                                ));
                                    }
                                    emailCursor.close();
                                }
                                Contact contact =
                                        new Contact(name,
                                                phone);
                                contact.setEmail(email);
                                contacts.add(contact);

                            }
                            returnContacts();

                        } else {
                            assert callBack != null;
                            callBack.OnError(
                                    new LoadContactsError("no contacts found")
                            );
                            cursor.close();
                        }
                    }

                };
        handler.post(runnable);
    }
    private void returnContacts() {
        if (filter == Filter.NONE) {
            listener.OnComplete(getContacts());
        }
        else if (filter == Filter.PHONE_NUMBERS) {
            ArrayList<Contact> contacts1 =
                    new ArrayList<>();
            for (Contact contact : contacts) {
                if (!TextUtils.isEmpty(
                        contact.getPhone())) {
                    contacts1.add(contact);
                }
            }
            NonNullList<Contact> contacts2 =
                    new NonNullList<>();
            for (Contact contact : contacts1) {
                if (!TextUtils.isEmpty(contact.getEmail())) {
                    contacts2.add(contact);
                }
            }
            listener.OnComplete(contacts2);
        }
        else if (filter == Filter.EMAIL) {
            ArrayList<Contact> contacts1 =
                    new ArrayList<>();
            for (Contact contact : contacts) {
                if (!TextUtils.isEmpty(
                        contact.getEmail())) {
                    contacts1.add(contact);
                }
            }
            NonNullList<Contact> contacts2 =
                    new NonNullList<>();
            for (Contact contact : contacts1) {
                if (!TextUtils.isEmpty(contact.getPhone())) {
                    contacts2.add(contact);
                }
            }
            listener.OnComplete(contacts2);
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public NonNullList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(NonNullList<Contact> contacts) {
        this.contacts = contacts;
    }

    public OnLoadListener getListener() {
        return listener;
    }

    public void setListener(OnLoadListener listener) {
        this.listener = listener;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }
    public enum  Filter {
        NONE,
        PHONE_NUMBERS,
        EMAIL
    }

    public interface OnLoadListener {
        void OnComplete(NonNullList<Contact> contacts);
    }

    public interface CallBack<T , X extends JubileeException> {
        void OnProgress(String message);

        void OnProgress(int progress);

        T OnError(X x);
    }
}
