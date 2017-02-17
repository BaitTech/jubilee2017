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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.adapter.NewsAdapter;
import inc.bait.jubilee.model.appmodel.News;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.persistence.net.model.NewsData;
import inc.bait.jubilee.model.transaction.FetchNewsTransaction;
import inc.bait.jubilee.ui.activity.JubileeActivity;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NewsFragment extends JubileeFragment implements
        Transaction.OnCompleteListener<NewsData> {
    public static final String TITLE = "NewsFeed";
    private static final String COLUMN_COUNT = "column-count";
    private int columnCount = 1;
    private OnListFragmentInteractionListener listener;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    JubileeActivity activity;
    Handler handler = new Handler();
    public NewsFragment() {
    }

    @SuppressWarnings("unused")
    public static NewsFragment newInstance(int columnCount) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, columnCount);
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
            columnCount = getArguments().getInt(COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list,
                container,
                false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();
        activity = (JubileeActivity) getActivity();
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNews();
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }

    }
    private void fetchNews() {
        Transaction tx =
                new FetchNewsTransaction(

        activity.getApp());
        tx.setOnCompleteListener(this);
        tx.execute();

    }
    private void displayNews(final ArrayList<News> newses) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(new NewsAdapter(newses,
                        listener));
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            listener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString()
                    + " must implement " +
                    "OnListFragmentInteractionListener");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        executeRunnable(
                new Runnable() {
            @Override
            public void run() {
                refreshLayout.post(
                        new Runnable() {
                    public void run() {
                        refreshLayout.setRefreshing(
                                true);
                        fetchNews();
                    }
                });
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void OnComplete(int id,
                           NewsData data) {
        switch (id) {
            case Transaction.NetTransaction.FETCH_NEWS : {
                if (data != null) {
                    displayNews(data.getNewses());
                }
                refreshLayout.setRefreshing(false);
            }
        }
    }


    public interface OnListFragmentInteractionListener {
        void OnListFragmentInteraction(News news);
        void OnFragmentInteraction(A2FListener listener);
    }
}
