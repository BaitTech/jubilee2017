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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import inc.bait.jubilee.JubileeApp;
import inc.bait.jubilee.R;
import inc.bait.jubilee.model.appmodel.News;
import inc.bait.jubilee.model.persistence.net.Address;
import inc.bait.jubilee.ui.fragments.NewsFragment.OnListFragmentInteractionListener;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final List<News> newses;
    private final OnListFragmentInteractionListener listener;

    public NewsAdapter(List<News> newses,
                       OnListFragmentInteractionListener listener) {
        this.newses = newses;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_news,
                        parent,
                        false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final News news = newses.get(position);
        holder.newsTitle.setText(news.getNewsTitle());
        holder.newsDescription.setText(news.getNewsDescription());

        holder.newsMore.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.OnListFragmentInteraction(news);
                }
            }
        });
        downloadNewsImage(holder.newsImage,
                news.getImageUrl());

    }
    private void downloadNewsImage(NetworkImageView imageView,
                                   String link) {
        imageView.setDefaultImageResId(R.drawable.jubilee_icon_ps);
        imageView.setImageUrl(Address.JUBILEE_NEWS_IMAGES.toString()+
                link,
                JubileeApp.getInstance().getImageLoader());

    }

    @Override
    public int getItemCount() {
        return newses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView newsImage;
        TextView newsTitle;
        TextView newsDescription;
        TextView newsMore;


        ViewHolder(View view) {
            super(view);
            newsImage = (NetworkImageView)
                    view.findViewById(R.id.news_image);
            newsTitle = (TextView)
                    view.findViewById(R.id.news_title);
            newsDescription = (TextView)
                    view.findViewById(R.id.news_description);
            newsMore = (TextView)
                    view.findViewById(R.id.news_more);
        }
    }
}
