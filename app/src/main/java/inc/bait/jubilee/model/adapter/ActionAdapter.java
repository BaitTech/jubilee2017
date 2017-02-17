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

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import inc.bait.jubilee.JubileeApp;
import inc.bait.jubilee.R;
import inc.bait.jubilee.model.appmodel.Action;
import inc.bait.jubilee.model.persistence.net.Address;
import inc.bait.jubilee.ui.fragments.ActionFragment.OnListFragmentInteractionListener;


public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> {

    private final List<Action> actions;
    private final OnListFragmentInteractionListener listener;

    public ActionAdapter(List<Action> actions,
                         OnListFragmentInteractionListener listener) {
        this.actions = actions;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_action,
                        parent,
                        false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,
                                 int position) {
        final Action action = actions.get(position);

        holder.title.setText(action.getTitle());
        holder.description.setText(action.getDescription());
        holder.points.setText(action.getActionPoints().toString());
        holder.count.setText(action.getActionCount().toString());
        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.OnListFragmentInteraction(action);
                }
            }
        });
        downloadNewsImage(holder.icon,
                action.getImageUrl());
    }
    private void downloadNewsImage(NetworkImageView imageView,
                                   String link) {
        imageView.setImageUrl(Address.JUBILEE_ACTION_IMAGES.toString()+
                link,
                JubileeApp.getInstance().getImageLoader());

    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        NetworkImageView icon;
        TextView title;
        TextView description;
        TextView points;
        TextView count;
        FloatingActionButton fab;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            icon = (NetworkImageView) view.findViewById(R.id.action_icon);
            title = (TextView) view.findViewById(R.id.action_title);
            description = (TextView) view.findViewById(R.id.action_description);
            points = (TextView) view.findViewById(R.id.action_points);
            count = (TextView) view.findViewById(R.id.action_count);
            fab = (FloatingActionButton) view.findViewById(R.id.action_click);

        }

    }
}
