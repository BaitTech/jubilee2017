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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.appmodel.Action;
import inc.bait.jubilee.model.appmodel.Event;
import inc.bait.jubilee.model.appmodel.News;
import inc.bait.jubilee.model.base.Transaction;
import inc.bait.jubilee.model.helper.util.LogUtil;
import inc.bait.jubilee.model.persistence.net.Address;
import inc.bait.jubilee.model.persistence.net.NetData;
import inc.bait.jubilee.model.persistence.pref.Pref;
import inc.bait.jubilee.model.persistence.pref.Preference;
import inc.bait.jubilee.model.transaction.IncrementEventViewTransaction;
import inc.bait.jubilee.model.transaction.IncrementPointsTransaction;
import inc.bait.jubilee.ui.fragments.A2FListener;
import inc.bait.jubilee.ui.fragments.ActionFragment;
import inc.bait.jubilee.ui.fragments.EventFragment;
import inc.bait.jubilee.ui.fragments.JubileeFragment;
import inc.bait.jubilee.ui.fragments.NewsFragment;
import inc.bait.jubilee.ui.fragments.WebFragment;

public class MainActivity extends JubileeActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ActionFragment.OnListFragmentInteractionListener,
        EventFragment.OnListFragmentInteractionListener,
        NewsFragment.OnListFragmentInteractionListener,
        WebFragment.OnFragmentInteractionListener {
    Toolbar toolbar;
    TabLayout tabLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ViewPager viewPager;
    CircularImageView profile;

    TextView name;
    TextView points;
    private A2FListener listener;
    private ListFragments listFragments;
    private String TAG =
            LogUtil.makeLogTag(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) getView(R.id.toolbar);
        tabLayout = (TabLayout) getView(R.id.tabs);
        drawerLayout = (DrawerLayout) getView(R.id.drawer_layout);
        navigationView = (NavigationView) getView(R.id.nav_view);
        viewPager = (ViewPager) getView(R.id.container);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        listFragments =
                new ListFragments();
        FragmentManager fragmentManager =
                getSupportFragmentManager();
        SectionsPagerAdapter pagerAdapter =
                new SectionsPagerAdapter(
                        fragmentManager);
        viewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position,
                                               float positionOffset,
                                               int positionOffsetPixels) {
                        setTitle(R.string.app_name);

                    }

                    @Override
                    public void onPageSelected(int position) {
                        setTitle(R.string.app_name);

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        setTitle(R.string.app_name);

                    }
                });
        viewPager.setAdapter(
                pagerAdapter);
        tabLayout.setupWithViewPager(
                viewPager);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this, drawerLayout,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(
                this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUpHeaderLayout();
    }

    private void setUpHeaderLayout() {
        View header = navigationView.getHeaderView(0);
        profile = (CircularImageView) header.findViewById(R.id.profile_icon);
        name = (TextView) header.findViewById(R.id.profile_name);
        points = (TextView) header.findViewById(R.id.profile_points);
        name.setText(getAccount().getF_name() +
                " "+
                getAccount().getS_name());
        points.setText(getAccount().getActionPoints().toString());
        loadImage(profile);

    }
    private void loadImage(ImageView imageView) {
        Preference imageUrl =
                getPreferences().getPreference(
                        Pref.ACCOUNT_IMAGE_URL,
                        "");
        String path = (String) imageUrl.getData();
        if (TextUtils.isEmpty(path)) {
            return;
        }
        ImageLoader loader =
                ImageLoader.getInstance();
        ImageLoaderConfiguration configuration =
                ImageLoaderConfiguration.createDefault(
                        this);
        loader.init(
                configuration);
        path = "file://" + path;
        LogUtil.d(TAG,
                path);
        loader.displayImage(path,
                imageView,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri,
                                                 View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri,
                                                View view,
                                                FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri,
                                                  View view,
                                                  Bitmap loadedImage) {
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri,
                                                   View view) {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(
                GravityCompat.START)) {
            drawerLayout.closeDrawer(
                    GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        Runnable runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.nav_profile: {
                                startActivity(
                                        getIntent(ProfileActivity.class));
                                break;
                            }
                            case R.id.nav_policy: {
                                Intent intent = getIntent(
                                        WebActivity.class);
                                intent.putExtra(
                                        WebActivity.URL,
                                        Address.JUBILEE_PRIVACY_POLICY
                                                .toString());
                                intent.putExtra(
                                        WebActivity.TITLE,
                                        "Privacy Policy");
                                startActivity(intent);
                                break;
                            }
                            case R.id.nav_eula: {
                                Intent intent = getIntent(
                                        WebActivity.class);
                                intent.putExtra(
                                        WebActivity.URL,
                                        Address.JUBILEE_EULA
                                                .toString());
                                intent.putExtra(
                                        WebActivity.TITLE,
                                        "Eula");
                                startActivity(intent);
                                break;

                            }
                            /*
                            case R.id.nav_invite: {
                                startActivity(getIntent(
                                        InviteActivity.class));
                                break;
                            }
                            */
                            case R.id.nav_settings: {
                                Intent intent = getIntent(
                                        SettingsActivity.class);
                                startActivity(intent);
                                break;
                            }

                            case R.id.nav_credits: {
                                Intent intent = getIntent(
                                        WebActivity.class);
                                intent.putExtra(
                                        WebActivity.URL,
                                        Address.JUBILEE_CREDITS
                                                .toString());
                                intent.putExtra(
                                        WebActivity.TITLE,
                                        "Credits");
                                startActivity(intent);
                                break;
                            }
                            case R.id.nav_feedback: {
                                Intent intent = getIntent(
                                        FeedBackActivity.class);
                                startActivity(intent);
                                break;
                            }
                        }
                    }
                };
        getHandler().postDelayed(runnable,
                1000);
        return true;
    }

    @Override
    public void OnListFragmentInteraction(
            final Action action) {
        Preference password =
                getPreferences().getPreference(
                        Pref.PASSWORD,
                        "password");
        getAccount().setPassword((String) password.getData());
        Transaction tx =
                new IncrementPointsTransaction(
                        getApp(),
                        getAccount(),
                        action.getActionPoints().getCount()
                );
        tx.setOnCompleteListener(
                new Transaction.OnCompleteListener<NetData>() {
                    @Override
                    public void OnComplete(int id,
                                           NetData data) {
                        Intent intent = getIntent(
                                WebActivity.class);
                        intent.putExtra(
                                WebActivity.TITLE,
                                action.getTitle());
                        intent.putExtra(
                                WebActivity.URL,
                                action.getLink());
                        startActivity(intent);

                    }
                });
        tx.execute();
    }

    @Override
    public void OnFragmentInteraction(A2FListener listener) {
        this.listener = listener;
    }


    @Override
    public void OnListFragmentInteraction(final Event event) {
        Transaction tx =
                new IncrementEventViewTransaction(getApp(),
                        event);
        tx.setOnCompleteListener(
                new Transaction.OnCompleteListener<NetData>() {
                    @Override
                    public void OnComplete(int id,
                                           NetData data) {
                        Intent intent = getIntent(
                                WebActivity.class);
                        intent.putExtra(
                                WebActivity.TITLE,
                                event.getTitle());
                        intent.putExtra(
                                WebActivity.URL,
                                event.getLink());
                        startActivity(intent);
                    }
                });
        tx.execute();
    }

    @Override
    public void OnListFragmentInteraction(News news) {
        Intent intent = getIntent(
                WebActivity.class);
        intent.putExtra(
                WebActivity.URL,
                news.getNewsLink());
        intent.putExtra(
                WebActivity.TITLE,
                news.getNewsTitle());
        intent.putExtra(WebActivity.SHARE,
                news);
        startActivity(intent);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return listFragments.getFragments().get(position);
        }

        @Override
        public int getCount() {
            return listFragments.getFragments().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return listFragments.getFragments()
                    .get(position).getTitle();
        }
    }

    class ListFragments {
        ArrayList<JubileeFragment> fragments;

        ListFragments() {
            fragments =
                    new ArrayList<>();
            fragments.add(
                    ActionFragment.newInstance(
                            0));
            fragments.add(
                    NewsFragment.newInstance(
                            0));
            fragments.add(
                    EventFragment.newInstance(
                            0));
            fragments.add(
                    WebFragment.newInstance(
                            getjubileeWebAddress()));
            Preference shuffle =
                    getPreferences().getPreference(
                            Pref.SHUFFLE,
                    true);
            if ((boolean) shuffle.getData()) {
                shuffle();
            }
        }

        void shuffle() {
            Collections.shuffle(
                    fragments);
        }

        ArrayList<JubileeFragment> getFragments() {
            return fragments;
        }
    }
}
