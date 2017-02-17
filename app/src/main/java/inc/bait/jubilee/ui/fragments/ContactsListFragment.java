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
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.CheckBox;
import android.widget.QuickContactBadge;
import android.widget.SearchView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import inc.bait.jubilee.BuildConfig;
import inc.bait.jubilee.R;
import inc.bait.jubilee.model.helper.ApiHelper;
import inc.bait.jubilee.model.helper.util.ImgLoader;
import inc.bait.jubilee.model.helper.util.LogUtil;

public class ContactsListFragment extends ListFragment implements
        AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ContactsListFragment";
    private static final String STATE_PREVIOUSLY_SELECTED_KEY =
            "com.example.android.contactslist.ui.SELECTED_ITEM";
    private ContactsAdapter adapter;
    private ImgLoader imgLoader;
    private String searchString;
    private OnContactsInteractionListener contactsInteractionListener;
    private int mPreviouslySelectedSearchItem = 0;
    private boolean mSearchQueryChanged;
    private boolean isSearchResultView = false;
    public ContactsListFragment() {}
    public void setSearchQuery(String query) {
        if (TextUtils.isEmpty(query)) {
            isSearchResultView = false;
        } else {
            searchString = query;
            isSearchResultView = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        adapter = new ContactsAdapter(getActivity());
        imgLoader = new ImgLoader(getActivity(),
                getListPreferredItemHeight()) {
            @Override
            protected Bitmap processBitmap(Object data) {
                return loadContactPhotoThumbnail((String) data,
                        getImageSize());
            }
        };
        imgLoader.setLoadingImage(
                R.drawable.ic_contact_picture_holo_light);
        imgLoader.addImageCache(
                getActivity().getSupportFragmentManager(),
                0.1f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contact_list_fragment,
                container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView,
                                             int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    imgLoader.setPauseWork(true);
                } else {
                    imgLoader.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView,
                                 int i, int i1, int i2) {}
        });
        if (mPreviouslySelectedSearchItem == 0) {
            getLoaderManager().initLoader(ContactsQuery.QUERY_ID,
                    null,
                    this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            contactsInteractionListener = (OnContactsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnContactsInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        imgLoader.setPauseWork(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View v, int position, long id) {
        final Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);
        final Uri uri = Contacts.getLookupUri(
                cursor.getLong(ContactsQuery.ID),
                cursor.getString(ContactsQuery.LOOKUP_KEY));
        contactsInteractionListener.onContactSelected(uri);
    }
    private void onSelectionCleared() {
        contactsInteractionListener.onSelectionCleared();
        getListView().clearChoices();
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreateOptionsMenu(Menu menu,
                                    MenuInflater inflater) {
        inflater.inflate(R.menu.contact_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (isSearchResultView) {
            searchItem.setVisible(false);
        }
        if (ApiHelper.hasHoneycomb()) {
            final SearchManager searchManager =
                    (SearchManager) getActivity().getSystemService(
                            Context.SEARCH_SERVICE);
            final SearchView searchView =
                    (SearchView) searchItem.getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(
                            getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String queryText) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
                    if (searchString == null && newFilter == null) {
                        return true;
                    }
                    if (searchString != null && searchString.equals(newFilter)) {
                        return true;
                    }
                    searchString = newFilter;
                    mSearchQueryChanged = true;
                    getLoaderManager().restartLoader(
                            ContactsQuery.QUERY_ID, null, ContactsListFragment.this);
                    return true;
                }
            });

            if (ApiHelper.hasICS()) {
                searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        if (!TextUtils.isEmpty(searchString)) {
                            onSelectionCleared();
                        }
                        searchString = null;
                        getLoaderManager().restartLoader(
                                ContactsQuery.QUERY_ID,
                                null,
                                ContactsListFragment.this);
                        return true;
                    }
                });
            }

            if (searchString != null) {
                final String savedSearchTerm = searchString;

                // Expands the search menu item
                if (ApiHelper.hasICS()) {
                    searchItem.expandActionView();
                }
                searchView.setQuery(savedSearchTerm, false);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(searchString)) {
            outState.putString(SearchManager.QUERY, searchString);
            outState.putInt(STATE_PREVIOUSLY_SELECTED_KEY,
                    getListView().getCheckedItemPosition());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_contact:
                final Intent intent = new Intent(Intent.ACTION_INSERT,
                        Contacts.CONTENT_URI);
                startActivity(intent);
                break;
            case R.id.menu_search:
                if (!ApiHelper.hasHoneycomb()) {
                    getActivity().onSearchRequested();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ContactsQuery.QUERY_ID) {
            Uri contentUri;
            if (searchString == null) {
                contentUri = ContactsQuery.CONTENT_URI;
            } else {
                contentUri =
                        Uri.withAppendedPath(ContactsQuery.FILTER_URI,
                                Uri.encode(searchString));
            }
            return new CursorLoader(getActivity(),
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);
        }

        LogUtil.e(TAG, "onCreateLoader - incorrect ID provided (" + id + ")");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            adapter.swapCursor(null);
        }
    }
    private int getListPreferredItemHeight() {
        final TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(
                android.R.attr.listPreferredItemHeight, typedValue, true);
        final DisplayMetrics metrics = new android.util.DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (int) typedValue.getDimension(metrics);
    }
    private Bitmap loadContactPhotoThumbnail(String photoData,
                                             int imageSize) {
        if (!isAdded() || getActivity() == null) {
            return null;
        }
        AssetFileDescriptor afd = null;
        try {
            Uri thumbUri;
            if (ApiHelper.hasHoneycomb()) {
                thumbUri = Uri.parse(photoData);
            } else {
                final Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI,
                        photoData);
                thumbUri = Uri.withAppendedPath(contactUri,
                        Photo.CONTENT_DIRECTORY);
            }
            afd = getActivity().getContentResolver()
                    .openAssetFileDescriptor(thumbUri, "r");
            FileDescriptor fileDescriptor = afd.getFileDescriptor();

            if (fileDescriptor != null) {
                return ImgLoader.decodeSampledBitmapFromDescriptor(
                        fileDescriptor, imageSize, imageSize);
            }
        } catch (FileNotFoundException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Contact photo thumbnail not found for contact " + photoData
                        + ": " + e.toString());
            }
        } finally {
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
    private class ContactsAdapter extends CursorAdapter implements SectionIndexer {
        private LayoutInflater mInflater;
        private AlphabetIndexer mAlphabetIndexer;
        private TextAppearanceSpan highlightTextSpan;
        ContactsAdapter(Context context) {
            super(context,
                    null,
                    0);
            mInflater = LayoutInflater.from(context);
            final String alphabet =
                    context.getString(R.string.alphabet);
            mAlphabetIndexer = new AlphabetIndexer(null,
                    ContactsQuery.SORT_KEY,
                    alphabet);
            highlightTextSpan =
                    new TextAppearanceSpan(getActivity(),
                            R.style.searchTextHiglight);
        }
        private int indexOfSearchQuery(String displayName) {
            if (!TextUtils.isEmpty(searchString)) {
                return displayName.toLowerCase(Locale.getDefault()).indexOf(
                        searchString.toLowerCase(Locale.getDefault()));
            }
            return -1;
        }
        @Override
        public View newView(Context context,
                            Cursor cursor,
                            ViewGroup viewGroup) {

            final View itemLayout =
                    mInflater.inflate(R.layout.contact_list_item,
                            viewGroup, false);
            final ViewHolder holder = new ViewHolder();
            holder.text1 = (TextView) itemLayout.findViewById(android.R.id.text1);
            holder.text2 = (TextView) itemLayout.findViewById(android.R.id.text2);
            holder.icon = (QuickContactBadge) itemLayout.findViewById(R.id.icon);
            holder.selectCheckBox =
                    (CheckBox) itemLayout.findViewById(R.id.select_checkBox);
            itemLayout.setTag(holder);
            return itemLayout;
        }
        @Override
        public void bindView(View view,
                             Context context,
                             Cursor cursor) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            final String photoUri = cursor.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);

            final String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);

            final int startIndex = indexOfSearchQuery(displayName);

            if (startIndex == -1) {
                holder.text1.setText(displayName);

                if (TextUtils.isEmpty(searchString)) {
                    holder.text2.setVisibility(View.GONE);
                } else {
                    holder.text2.setVisibility(View.VISIBLE);
                }
                holder.selectCheckBox.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }
                );
            } else {
                final SpannableString highlightedName =
                        new SpannableString(displayName);
                highlightedName.setSpan(highlightTextSpan, startIndex,
                        startIndex + searchString.length(), 0);
                holder.text1.setText(highlightedName);
                holder.text2.setVisibility(View.GONE);
            }
            final Uri contactUri = Contacts.getLookupUri(
                    cursor.getLong(ContactsQuery.ID),
                    cursor.getString(ContactsQuery.LOOKUP_KEY));
            holder.icon.assignContactUri(contactUri);
            imgLoader.loadImage(photoUri, holder.icon);
        }
        @Override
        public Cursor swapCursor(Cursor newCursor) {
            mAlphabetIndexer.setCursor(newCursor);
            return super.swapCursor(newCursor);
        }
        @Override
        public int getCount() {
            if (getCursor() == null) {
                return 0;
            }
            return super.getCount();
        }
        @Override
        public Object[] getSections() {
            return mAlphabetIndexer.getSections();
        }
        @Override
        public int getPositionForSection(int i) {
            if (getCursor() == null) {
                return 0;
            }
            return mAlphabetIndexer.getPositionForSection(i);
        }
        @Override
        public int getSectionForPosition(int i) {
            if (getCursor() == null) {
                return 0;
            }
            return mAlphabetIndexer.getSectionForPosition(i);
        }
        private class ViewHolder {
            CheckBox selectCheckBox;
            TextView text1;
            TextView text2;
            QuickContactBadge icon;
        }
    }
    public interface OnContactsInteractionListener {
        void onContactSelected(Uri contactUri);
        void onSelectionCleared();
    }
    public interface ContactsQuery {
        int QUERY_ID = 1;
        Uri CONTENT_URI = Contacts.CONTENT_URI;
        Uri FILTER_URI = Contacts.CONTENT_FILTER_URI;
        @SuppressLint("InlinedApi")
        String SELECTION =
                (ApiHelper.hasHoneycomb() ?
                        Contacts.DISPLAY_NAME_PRIMARY :
                        Contacts.DISPLAY_NAME) +
                        "<>''" +
                        " AND " +
                        Contacts.IN_VISIBLE_GROUP +
                        "=1";
        @SuppressLint("InlinedApi")
        String SORT_ORDER =
                ApiHelper.hasHoneycomb() ?
                        Contacts.SORT_KEY_PRIMARY :
                        Contacts.DISPLAY_NAME;
        @SuppressLint("InlinedApi")
        String[] PROJECTION = {
                Contacts._ID,
                Contacts.LOOKUP_KEY,
                ApiHelper.hasHoneycomb() ?
                        Contacts.DISPLAY_NAME_PRIMARY :
                        Contacts.DISPLAY_NAME,
                ApiHelper.hasHoneycomb() ?
                        Contacts.PHOTO_THUMBNAIL_URI :
                        Contacts._ID,
                SORT_ORDER,
        };
        int ID = 0;
        int LOOKUP_KEY = 1;
        int DISPLAY_NAME = 2;
        int PHOTO_THUMBNAIL_DATA = 3;
        int SORT_KEY = 4;
    }
}
