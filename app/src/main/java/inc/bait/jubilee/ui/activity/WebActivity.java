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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.appmodel.News;
import inc.bait.jubilee.model.helper.util.LogUtil;


public class WebActivity extends JubileeActivity {
    private String TAG =
            LogUtil.makeLogTag(WebActivity.class);
    static final String URL = "url";
    static final String TITLE = "title";
    static final String USER_CONSENT = "consent";
    static final String SHARE = "share";
    private String content;
    private News news = null;
    private boolean hasShare = false;
    private String url;
    WebView webView;
    AdView topAd;
    AdView bottomAd;
    Button acceptButton;
    Button rejectButton;
    private InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView) getView(R.id.web_view);
        topAd = (AdView) getView(R.id.ad_top);
        bottomAd = (AdView) getView(R.id.ad_bottom);
        acceptButton = (Button) getView(R.id.accept_button);
        rejectButton = (Button) getView(R.id.reject_button);
        setHasBackButton(true);
        setShouldBeOnline();
        interstitialAd = newInterstitialAd();
        loadInterstitial();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(URL)) {
            this.url = intent.getStringExtra(URL);
            LogUtil.i(TAG, url);
        }
        else {
            LogUtil.e(TAG,
                    "no url passed");
            finish();
        }
        if (intent.hasExtra(TITLE)) {
            String title = intent.getStringExtra(TITLE);
            setTitle(title);
        }
        if (intent.hasExtra(USER_CONSENT)) {
            rejectButton.setVisibility(View.VISIBLE);
            rejectButton.setOnClickListener(
                    new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });
            acceptButton.setVisibility(View.VISIBLE);
            acceptButton.setOnClickListener(
                    new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }
        if (intent.hasExtra(SHARE)) {
            hasShare = true;
            news = intent.getParcelableExtra(SHARE);
        }
        WebClient client = new WebClient();
        webView.setWebViewClient(client);
        webView.getSettings().setLoadsImagesAutomatically(
                true);
        webView.setScrollBarStyle(
                View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setUseWideViewPort(
                true);
        webView.getSettings().setAllowContentAccess(
                true);
        webView.getSettings().setJavaScriptEnabled(
                true);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        topAd.loadAd(adRequest);
        bottomAd.loadAd(adRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (hasShare) {
            getMenuInflater().inflate(R.menu.main,
                    menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share: {
                shareMessage();
                break;

            }
            case R.id.action_refresh:{
                refresh();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void shareMessage() {
        if (news != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, getTitle());
            intent.putExtra(Intent.EXTRA_TEXT,
                    news.getNewsDescription() + "\n\n\n"
            +news.getNewsLink());
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent,
                    "Share with"));
        }

    }
    private void refresh() {
        loadSite(WebActivity.this.url);
    }
    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                WebActivity.this.interstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {

            }
        });
        return interstitialAd;
    }
    private void loadInterstitial() {
        AdRequest adRequest =
                new AdRequest.Builder()
                        .build();
        interstitialAd.loadAd(adRequest);

    }

    @Override
    public void onPause() {
        if (topAd != null) {
            topAd.pause();
        }
        if (bottomAd != null) {
            bottomAd.pause();
        }
        super.onPause();
    }



    @Override
    public void onDestroy() {
        if (topAd != null) {
            topAd.destroy();
        }
        if (bottomAd != null) {
            bottomAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Runnable runnable =
                new Runnable() {
            @Override
            public void run() {
                if (topAd != null) {
                    topAd.resume();
                }
                if (bottomAd != null) {
                    bottomAd.resume();
                }
                loadSite(WebActivity.this.url);

            }
        };
        getHandler().post(runnable);

    }
    @SuppressLint("SetJavaScriptEnabled")
    private void loadSite(String url) {
        webView.loadUrl(url);
        webView.requestFocus();
    }
    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
