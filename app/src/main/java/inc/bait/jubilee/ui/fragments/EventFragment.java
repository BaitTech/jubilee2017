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
import inc.bait.jubilee.model.adapter.EventAdapter;
import inc.bait.jubilee.model.appmodel.Event;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.persistence.net.model.EventsData;
import inc.bait.jubilee.model.transaction.FetchEventsTransaction;
import inc.bait.jubilee.ui.activity.JubileeActivity;

public class EventFragment extends JubileeFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        Transaction.OnCompleteListener<EventsData> {
    public static final String TITLE = "Events";
    private static final String COLUMN_COUNT = "column-count";
    private int columnCount = 1;
    private Handler handler = new Handler();
    private OnListFragmentInteractionListener listener;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    JubileeActivity activity;
    private Context context;
    public EventFragment() {
    }
    public static EventFragment newInstance(int columnCount) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT,
                columnCount);
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
            columnCount =
                    getArguments().getInt(
                            COLUMN_COUNT);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list,
                container,
                false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (JubileeActivity) getActivity();
         refreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary));
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }


    }
    private void displayEvents(ArrayList<Event> events) {
        recyclerView.setAdapter(new EventAdapter(events,
                listener));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnListFragmentInteractionListener) {
            listener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
                        loadEvents();
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

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        loadEvents();
    }
    private void loadEvents() {
        Transaction tx =
                new FetchEventsTransaction(
                activity.getApp());
        tx.setOnCompleteListener(this);
        tx.execute(500);
    }



    @Override
    public void OnComplete(int id,
                           EventsData data) {
        switch (id) {
            case Transaction.NetTransaction.FETCH_EVENTS: {
                if (data != null) {
                    displayEvents(data.getEvents());
                }
                refreshLayout.setRefreshing(false);
            }
        }
    }

    public interface OnListFragmentInteractionListener {
        void OnListFragmentInteraction(Event event);
        void OnFragmentInteraction(A2FListener listener);
    }
}
