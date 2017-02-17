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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.helper.util.BitmapUtil;

public class WelcomeActivity extends JubileeActivity {

    ImageView background;
    Button signin;
    Button signup;
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        background = (ImageView) getView(R.id.backgroundImage);
        signin = (Button) getView(R.id.sign_in_button);
        signup = (Button) getView(R.id.sign_up_button);
        Runnable runnable =
                new Runnable() {
            @Override
            public void run() {
                signin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WelcomeActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WelcomeActivity.this,
                                RegistrationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                BitmapUtil.setBitmapFromAssets(WelcomeActivity.this,
                        "uhuru_kenyatta_william_ruto.jpg",
                        background);
            }
        };
        handler.post(runnable);
    }
}
