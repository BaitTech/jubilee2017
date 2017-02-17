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

package inc.bait.jubilee;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.File;

import inc.bait.jubilee.model.appmodel.Account;
import inc.bait.jubilee.model.helper.BitmapCache;
import inc.bait.jubilee.model.helper.util.LogUtil;
import inc.bait.jubilee.service.JubileeService;
import inc.bait.jubilee.ui.activity.JubileeActivity;


public class JubileeApp extends MultiDexApplication {
    private Context jubileeContext;
    private JubileeActivity currentActivity;
    private String TAG =
            LogUtil.makeLogTag(
                    JubileeApp.class);
    private JubileeService jubileeService;
    private Account account;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static JubileeApp app;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

    }
    public static synchronized JubileeApp getInstance() {
        return app;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(
                    getApplicationContext());
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null) {
            imageLoader = new ImageLoader(this.requestQueue,
                    new BitmapCache());
        }
        return this.imageLoader;
    }
    public <T> void addToRequestQueue(Request<T> req,
                                      String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
        LogUtil.d(TAG, "adding request"+tag);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String file : children) {
                if (!deleteDir(new File(dir, file))) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public Context getJubileeContext() {
        return jubileeContext;
    }

    public void setJubileeContext(Context jubileeContext) {
        this.jubileeContext = jubileeContext;
    }

    public JubileeActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(JubileeActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public JubileeService getJubileeService() {
        return jubileeService;
    }

    public void setJubileeService(JubileeService jubileeService) {
        this.jubileeService = jubileeService;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
