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

package inc.bait.jubilee.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.persistence.net.Address;

public class WebFragment extends JubileeFragment {
    public static final String TITLE = "Pamoja";

    private OnFragmentInteractionListener listener;
    private String url;
    private static final String URL = "url";
    private WebView webView;
    public WebFragment() {
        // Required empty public constructor
    }


    public static WebFragment newInstance(Address address) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(URL,
                address.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web,
                container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,
                savedInstanceState);
        webView = (WebView)
                view.findViewById(
                        R.id.webView);
        WebClient client =
                new WebClient();
        webView.setWebViewClient(
                client);
        webView.getSettings()
                .setLoadsImagesAutomatically(
                        true);
        webView.setScrollBarStyle(
                View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings()
                .setUseWideViewPort(
                true);
        webView.getSettings()
                .setAllowContentAccess(
                true);
        webView.getSettings()
                .setJavaScriptEnabled(
                true);
    }
    private void loadSite() {
        webView.loadUrl(
                url);
        webView.requestFocus();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        executeRunnable(new Runnable() {
            @Override
            public void run() {
                loadSite();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener {
        void OnFragmentInteraction(A2FListener listener);
    }
    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,
                                                String url) {
            view.loadUrl(
                    url);
            return true;
        }
    }
}
