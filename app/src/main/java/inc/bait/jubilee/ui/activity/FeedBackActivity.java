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

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.persistence.net.model.FeedBackData;

public class FeedBackActivity extends JubileeActivity {

    FloatingActionButton fab;

    Toolbar toolbar;

    AutoCompleteTextView subjectEditText;

    AutoCompleteTextView messageEditText;
    private static  final int Send_Id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        fab = (FloatingActionButton) getView(R.id.fab);
        toolbar = (Toolbar) getView(R.id.toolbar);
        subjectEditText = (AutoCompleteTextView) getView(R.id.subjectEditText);
        messageEditText = (AutoCompleteTextView) getView(R.id.messageEditText);

        setHasBackButton(true);
        setShouldBeOnline();
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s =
                        subjectEditText.getText().toString();
                String m =
                        messageEditText.getText().toString();
                sendFeedBack(view,
                        s,
                        m);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void sendFeedBack(View view,
                              String subject,
                              String message) {
        if (TextUtils.isEmpty(message) ||
                TextUtils.isEmpty(subject)) {
            Snackbar.make(view,
                    "Please fill required fields",
                    Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        FeedBackSender sender =
                new FeedBackSender(this,
                        subject,
                        message,
                        Send_Id);
        sender.setOnCompleteListener(
                new Transaction.OnCompleteListener<FeedBackData>() {
                    @Override
                    public void OnComplete(int id,
                                           FeedBackData feedBackData) {
                        switch (id) {
                            case Send_Id : {

                            }
                        }
                    }
                });
        sender.execute(1000);
    }

    private static class FeedBackSender
            extends Transaction<FeedBackData,
                                    String> {
        private String subject,
                message;

        FeedBackSender(Context context,
                       String subject,
                       String message,
                       int id) {
            super(context,
                    context,
                    id);
            this.subject = subject;
            this.message = message;
        }

        @Override
        public OnUpdateListener<FeedBackData,
                String> getOnUpdateListener() {
            return null;
        }

        @Override
        public CallBacks<FeedBackData,
                String> getCallBacks() {
            return new CallBacks<FeedBackData, String>() {
                @Override
                public void OnStart() {

                }

                @Override
                public FeedBackData OnExecute() {
                    return null;
                }

                @Override
                public void OnProgress(String... s) {

                }

                @Override
                public void OnEnd(FeedBackData data) {

                }
            };
        }
    }
}
