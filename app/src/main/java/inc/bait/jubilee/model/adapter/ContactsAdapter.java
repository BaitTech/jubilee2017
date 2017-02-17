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

package inc.bait.jubilee.model.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.appmodel.Contact;
import inc.bait.jubilee.ui.activity.InviteActivity;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private final ArrayList<Contact> contacts;
    private final InviteActivity.OnContactSelectedListener listener;


    public ContactsAdapter(ArrayList<Contact> contacts,
                          InviteActivity.OnContactSelectedListener listener) {
        this.contacts = contacts;
        this.listener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact,
                        parent,
                        false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,
                                 int position) {
        final Contact contact = contacts.get(
                position);
        if (contact.getPhoto() != null) {
            holder.photoView.setImageBitmap(
                    contact.getPhoto());
        }
        holder.nameTextView.setText(
                contact.getName());
        if (!TextUtils.isEmpty(contact.getEmail())) {
            holder.email_phoneTextView.setText(
                    contact.getEmail());
        }
        if (!TextUtils.isEmpty(contact.getPhone())) {
            holder.email_phoneTextView.setText(
                    contact.getPhone());
        }
        holder.selectCheckBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.selectCheckBox.isChecked()) {
                            listener.OnDeselected(contact);
                        } else {
                            listener.OnSelected(contact);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircularImageView photoView;
        TextView nameTextView;
        TextView email_phoneTextView;
        CheckBox selectCheckBox;

        ViewHolder(View view) {
            super(view);
            photoView = (CircularImageView)
                    view.findViewById(R.id.contact_photo);
            nameTextView = (TextView)
                    view.findViewById(R.id.name_textView);
            email_phoneTextView = (TextView)
                    view.findViewById(R.id.email_phone_textView);
            selectCheckBox = (CheckBox)
                    view.findViewById(R.id.selectCheckBox);

        }
    }
}
