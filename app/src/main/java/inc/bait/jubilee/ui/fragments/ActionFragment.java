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
import inc.bait.jubilee.model.adapter.ActionAdapter;
import inc.bait.jubilee.model.appmodel.Action;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.persistence.net.model.ActionsData;
import inc.bait.jubilee.model.transaction.FetchActionsTransaction;
import inc.bait.jubilee.ui.activity.JubileeActivity;

public class ActionFragment extends JubileeFragment implements
        Transaction.OnCompleteListener<ActionsData> {

    public static final String TITLE = "Actions";
    private static final String COLUMN_COUNT = "column-count";
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    JubileeActivity activity;
    Context context;
    private int columnCount = 1;

    private OnListFragmentInteractionListener listener;


    public ActionFragment() {
    }

    @SuppressWarnings("unused")
    public static ActionFragment newInstance(int columnCount) {
        ActionFragment fragment = new ActionFragment();
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
            columnCount = getArguments().getInt(COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_action_list,
                container,
                false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();

        swipeRefreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        recyclerView =
                (RecyclerView) view.findViewById(R.id.list);
        activity = (JubileeActivity) getActivity();
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadActions();
                    }
                });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary));
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }
        this.swipeRefreshLayout.post(new Runnable() {
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                loadActions();
            }
        });
    }

    private void displayActions(ArrayList<Action> actions) {
        recyclerView.setAdapter(new ActionAdapter(actions,
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
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void loadActions() {
        Transaction tx = new FetchActionsTransaction(
                activity.getApp());
        tx.setOnCompleteListener(this);
        tx.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        executeRunnable(
                new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.post(
                                new Runnable() {
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(
                                                true);
                                        loadActions();
                                    }
                                });
                    }
                });
    }

    @Override
    public void OnComplete(int id,
                           ActionsData data) {
        switch (id) {
            case Transaction.NetTransaction.FETCH_ACTIONS: {
                if (data != null) {
                    displayActions(data.getActions());
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    public interface OnListFragmentInteractionListener {
        void OnListFragmentInteraction(Action action);

        void OnFragmentInteraction(A2FListener listener);
    }
}
