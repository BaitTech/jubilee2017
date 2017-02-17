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

package inc.bait.jubilee.model.persistence.net;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import inc.bait.jubilee.JubileeApp;
import inc.bait.jubilee.model.appmodel.Account;
import inc.bait.jubilee.model.appmodel.Action;
import inc.bait.jubilee.model.appmodel.ActionCount;
import inc.bait.jubilee.model.appmodel.ActionPoints;
import inc.bait.jubilee.model.appmodel.Event;
import inc.bait.jubilee.model.appmodel.EventCount;
import inc.bait.jubilee.model.appmodel.News;
import inc.bait.jubilee.model.helper.util.LogUtil;
import inc.bait.jubilee.model.persistence.SessionManager;
import inc.bait.jubilee.model.persistence.net.field.LoginField;
import inc.bait.jubilee.model.persistence.net.field.RegisterField;
import inc.bait.jubilee.model.persistence.net.model.ActionsData;
import inc.bait.jubilee.model.persistence.net.model.EventViewData;
import inc.bait.jubilee.model.persistence.net.model.EventsData;
import inc.bait.jubilee.model.persistence.net.model.LocationData;
import inc.bait.jubilee.model.persistence.net.model.NewsData;
import inc.bait.jubilee.model.persistence.net.model.RegistrationData;


/**
 * Created by octopus on 10/17/16.
 */
public class NetBridge {
    // Max read limit that we allow
    // our input stream to mark/reset.
    private static final int MAX_READ_LIMIT_PER_IMG =
            1024 * 1024;
    private static String TAG =
            LogUtil.makeLogTag(NetBridge.class);
    private final int REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR =
            1;
    private Application application;
    private String url;
    private Context context;
    private Activity activity;
    private boolean canFindLocation;
    private boolean resolvingError;
    private NetData netData;
    private OnCompleteListener networkListener;
    private GoogleApiClient googleApiClient;
    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener;

    public NetBridge(String url) {
        LogUtil.i(TAG,
                "NetBridge: ");
        this.setUrl(url);
        init();
    }

    public NetBridge() {
        init();
    }

    public NetBridge(Context context) {
        this.setContext(context);
        init();
    }

    public NetBridge(Activity activity) {
        LogUtil.i(TAG,
                "NetBridge: ");
        this.setActivity(activity);
        this.setContext(activity.getApplicationContext());
        init();
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =
                connMgr.getActiveNetworkInfo();
        return (networkInfo != null &&
                networkInfo.isConnected());
    }

    private void init() {
        LogUtil.i(TAG,
                "init: ");
    }

    private void initLocationVariables() {
        setConnectionCallbacks(
                new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        LogUtil.i(TAG,
                                "onConnected: ");
                        setCanFindLocation(true);
                        getGoogleApiClient().connect();
                        try {
                            Location lastLocation =
                                    LocationServices.FusedLocationApi
                                            .getLastLocation(
                                                    getGoogleApiClient());
                            LocationData locationData =
                                    null;
                            if (lastLocation != null) {
                                locationData =
                                        new LocationData(
                                                "location");
                                locationData
                                        .setLocationName(
                                                getLocationName(
                                                        lastLocation));
                                locationData
                                        .setLocation(
                                                lastLocation);
                                LogUtil.i(TAG,
                                        "onConnected: location " +
                                                "Lat: " +
                                                lastLocation.getLatitude() +
                                                "Lon: " +
                                                lastLocation.getLongitude() +
                                                "provider " +
                                                lastLocation.getProvider());
                                LogUtil.i(TAG,
                                        "onConnected: locationName " +
                                                locationData.getLocationName());

                            }
                            returnData(locationData);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        LogUtil.i(TAG,
                                "onConnectionSuspended: ");
                    }
                });
        setConnectionFailedListener(
                new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(
                            @NonNull ConnectionResult connectionResult) {
                        LogUtil.i(TAG,
                                "onConnectionFailed: ");
                        if (connectionResult.hasResolution()) {
                            setResolvingError(true);
                            try {
                                connectionResult
                                        .startResolutionForResult(getActivity(),
                                                REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR);
                            } catch (IntentSender.SendIntentException e) {
                                getGoogleApiClient().connect();
                            }
                        } else {
                            showGoogleAPIErrorDialog(
                                    connectionResult.getErrorCode());
                        }
                    }
                });
    }

    public NetData getCurrentLocation() {
        initLocationVariables();
        setupGoogleApiClient();
        LogUtil.i(TAG,
                "getCurrentLocation: return");
        return netData;
    }

    private synchronized void setupGoogleApiClient() {
        LogUtil.i(TAG,
                "setupGoogleApiClient: ");
        setGoogleApiClient(new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(getConnectionCallbacks())
                .addOnConnectionFailedListener(
                        getConnectionFailedListener())
                .addApi(LocationServices.API)
                .build());
        getGoogleApiClient().connect();
    }

    private void showGoogleAPIErrorDialog(int errorCode) {
        LogUtil.i(TAG,
                "showGoogleAPIErrorDialog: " +
                        errorCode);
        GoogleApiAvailability googleApiAvailability =
                GoogleApiAvailability.getInstance();
        Dialog errorDialog = googleApiAvailability
                .getErrorDialog(getActivity(),
                        errorCode,
                        REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR);
        errorDialog.show();
    }

    private String getLocationName(Location location) {
        String name = "";
        Geocoder geocoder = new Geocoder(
                getContext(),
                Locale.getDefault());
        try {
            List<Address> list
                    = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
            if (list != null &&
                    !list.isEmpty()) {
                Address address =
                        list.get(
                                0);
                name =
                        address.getAddressLine(
                                0);
            }
        } catch (IOException | NullPointerException e) {
            LogUtil.e(TAG,
                    "getLocationName: ",
                    e);
        }
        LogUtil.i(TAG,
                "getLocationName: " +
                        name);
        return name;
    }

    public String registerAccount(Account account) {
        LogUtil.d(TAG,
                "registering account");
        final SessionManager sessionManager =
                new SessionManager(getContext());
        final String f_name =
                account.getF_name();
        LogUtil.d(TAG,
                "fname " +
                        f_name);
        final String s_name =
                account.getS_name();
        LogUtil.d(TAG,
                "sname " +
                        s_name);
        final String phone =
                String.valueOf(
                        account.getPhone());
        LogUtil.d(TAG,
                "phone " +
                        phone);
        final String password =
                account.getPassword();
        LogUtil.d(TAG,
                "password " +
                        password);
        final String county =
                account.getCounty();
        LogUtil.d(TAG,
                "county " +
                        county);
        String url = inc.bait.jubilee.model
                .persistence.net
                .Address.JUBILEE_REGISTER.toString();
        LogUtil.d(TAG,
                "url " +
                        url);
        StringRequest strReq =
                new StringRequest(Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                LogUtil.d(TAG,
                                        "Register Response: " +
                                                response);
                                try {
                                    JSONObject jObj =
                                            new JSONObject(response);
                                    boolean error =
                                            jObj.getBoolean("error");
                                    if (!error) {
                                        LogUtil.d(TAG, "registration success");

                                        JSONObject user =
                                                jObj.getJSONObject(
                                                        "user");
                                        String f_name =
                                                user.getString(
                                                        "fname");
                                        String s_name =
                                                user.getString(
                                                        "sname");
                                        String county =
                                                user.getString(
                                                        "county");
                                        String password =
                                                user.getString(
                                                        "password");
                                        String phone =
                                                user.getString(
                                                        "email_phone");
                                        Account account1 =
                                                new Account(phone,
                                                        password);
                                        account1.setUnique_id(user.getString("uid"));
                                        account1.setF_name(
                                                f_name);
                                        account1.setS_name(
                                                s_name);
                                        account1.setCounty(
                                                county);
                                        ActionPoints points =
                                                new ActionPoints(Integer.parseInt(
                                                        user.getString("points")
                                                ));
                                        account1.setActionPoints(points);
                                        RegistrationData data =
                                                new RegistrationData(
                                                        RegistrationData.SUCCESS);
                                        data.setAccount(account1);
                                        returnData(data);
                                        sessionManager.setLogin(
                                                true);

                                    } else {
                                        LogUtil.e(TAG, "registration failed");
                                        String errorMsg =
                                                jObj.getString(
                                                        "error_msg");
                                        RegistrationData data =
                                                new RegistrationData(
                                                        RegistrationData.ERROR);
                                        data.setMessage(errorMsg);
                                        returnData(data);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    LogUtil.e(TAG,
                                            e.getMessage());
                                }
                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(
                                    VolleyError error) {
                                LogUtil.e(TAG,
                                        "Network Error: " +
                                                error.getMessage());
                                returnData(new RegistrationData(
                                        RegistrationData.ERROR
                                ));
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params =
                                new HashMap<>();
                        params.put("tag", "register");
                        params.put(RegisterField.F_NAME.toString(),
                                f_name);
                        params.put(RegisterField.S_NAME.toString(),
                                s_name);
                        params.put(RegisterField.PHONE.toString(),
                                phone);
                        if (!TextUtils.isEmpty(county)) {
                            params.put(RegisterField.COUNTY.toString(),
                                    county);
                        }
                        params.put(RegisterField.PASSWORD.toString(),
                                password);
                        return params;
                    }
                };
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        strReq.setShouldCache(true);
        if (getApplication() instanceof JubileeApp) {
            JubileeApp.getInstance().addToRequestQueue(strReq,
                    tag_string_req);
        }
        return "logging_in";
    }

    public String loginAccount(Account account) {
        LogUtil.d(TAG, "logging in account");
        final SessionManager sessionManager =
                new SessionManager(getContext());
        final String inputPhone =
                String.valueOf(
                        account.getPhone());
        final String password =
                account.getPassword();
        StringRequest strReq =
                new StringRequest(Request.Method.POST,
                        inc.bait.jubilee.model
                                .persistence.net
                                .Address.JUBILEE_LOGIN.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(
                                    String response) {
                                try {
                                    JSONObject jObj =
                                            new JSONObject(response);
                                    boolean error =
                                            jObj.getBoolean("error");
                                    if (!error) {
                                        LogUtil.d(TAG, "registration success");

                                        JSONObject user =
                                                jObj.getJSONObject(
                                                        "user");
                                        String f_name =
                                                user.getString(
                                                        "fname");
                                        String s_name =
                                                user.getString(
                                                        "sname");
                                        String county =
                                                user.getString(
                                                        "county");
                                        String password =
                                                user.getString(
                                                        "password");
                                        String phone =
                                                user.getString(
                                                        "email_phone");
                                        Account account1 =
                                                new Account(phone,
                                                        password);
                                        account1.setUnique_id(user.getString("uid"));
                                        account1.setF_name(
                                                f_name);
                                        account1.setS_name(
                                                s_name);
                                        account1.setCounty(
                                                county);
                                        ActionPoints points =
                                                new ActionPoints(Integer.parseInt(
                                                        user.getString("points")
                                                ));
                                        account1.setActionPoints(points);
                                        RegistrationData data =
                                                new RegistrationData(
                                                        RegistrationData.SUCCESS);
                                        data.setAccount(account1);
                                        returnData(data);
                                        sessionManager.setLogin(
                                                true);

                                    } else {
                                        LogUtil.e(TAG, "registration failed");
                                        String errorMsg =
                                                jObj.getString(
                                                        "error_msg");
                                        RegistrationData data =
                                                new RegistrationData(RegistrationData.ERROR);
                                        data.setMessage(errorMsg);
                                        returnData(data);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    LogUtil.e(TAG,
                                            e.getMessage());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(
                                    VolleyError error) {
                                LogUtil.e(TAG,
                                        "Network Error: " +
                                                error.getMessage());
                                //return error
                                //notify error
                                RegistrationData data =
                                        new RegistrationData(RegistrationData.ERROR);
                                returnData(data);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params =
                                new HashMap<>();
                        params.put("tag", "login");
                        params.put(LoginField.PHONE.toString(),
                                inputPhone);
                        params.put(LoginField.PASSWORD.toString(),
                                password);
                        return params;
                    }
                };
        String tag_string_req = "req_login";
        if (getApplication() instanceof JubileeApp) {
            JubileeApp.getInstance().addToRequestQueue(strReq,
                    tag_string_req);
        }
        return null;
    }
    public String upDateUserPoints(final Account account,
                                   final int points) {
        final String inputPhone =
                String.valueOf(
                        account.getPhone());
        final String password =
                account.getPassword();
        LogUtil.e(TAG,password);
        LogUtil.e(TAG, inputPhone);
        LogUtil.e(TAG, points);
        StringRequest strReq =
                new StringRequest(Request.Method.POST,
                        inc.bait.jubilee.model
                                .persistence.net
                                .Address.JUBILEE_UPDATE_USER_POINTS
                                .toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(
                                    String response) {
                                JSONObject jObj;
                                try {
                                    jObj = new JSONObject(response);
                                    boolean error =
                                            jObj.getBoolean("error");
                                    if (!error) {
                                        NetData data =
                                                new NetData("points_addition");
                                        data.setData("success");
                                        returnData(data);
                                    }
                                    else {
                                        NetData data =
                                                new NetData("points_addition");
                                        data.setData("failure");
                                        returnData(data);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(
                                    VolleyError error) {
                                LogUtil.e(TAG,
                                        "Network Error: " +
                                                error.getMessage());

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params =
                                new HashMap<>();
                        params.put(LoginField.PHONE.toString(),
                                inputPhone);
                        params.put("points",
                                Integer.toString(points));
                        return params;
                    }
                };
        String tag_string_req = "req_login";
        if (getApplication() instanceof JubileeApp) {
            JubileeApp.getInstance().addToRequestQueue(strReq,
                    tag_string_req);
        }
        return null;

    }
    public String updateEventViews(final Event event) {
        StringRequest strReq =
                new StringRequest(Request.Method.POST,
                        inc.bait.jubilee.model
                                .persistence.net
                                .Address.JUBILEE_UPDATE_EVENT_VIEWS
                                .toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(
                                    String response) {
                                JSONObject jObj;
                                try {
                                    jObj = new JSONObject(response);
                                    boolean error =
                                            jObj.getBoolean("error");
                                    if (!error) {
                                        NetData data =
                                                new NetData("points_addition");
                                        data.setData("success");
                                        returnData(data);
                                    }
                                    else {
                                        NetData data =
                                                new NetData("points_addition");
                                        data.setData("failure");
                                        returnData(data);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(
                                    VolleyError error) {
                                LogUtil.e(TAG,
                                        "Network Error: " +
                                                error.getMessage());

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params =
                                new HashMap<>();
                        params.put("id",
                                String.valueOf(event.getId()));
                        return params;
                    }
                };
        String tag_string_req = "req_login";
        if (getApplication() instanceof JubileeApp) {
            JubileeApp.getInstance().addToRequestQueue(strReq,
                    tag_string_req);
        }
        return null;
    }

    public ArrayList<Action> getActions() {
        LogUtil.d(TAG, "getting actions");
        final ArrayList<Action> actions = new ArrayList<>();
        String url =
                inc.bait.jubilee.model
                        .persistence.net
                        .Address.JUBILEE_ACTIONS.toString();
        StringRequest stringRequest =
                new StringRequest(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(
                                    String response) {
                                LogUtil.d(TAG, "getactions successful");
                                JSONObject jsonObject;
                                JSONArray action;
                                try {
                                    jsonObject =
                                            new JSONObject(
                                                    response);
                                    action =
                                            jsonObject.getJSONArray("actions");
                                    for (int i = 0; i < action.length(); i++) {
                                        JSONObject jo =
                                                action.getJSONObject(i);
                                        ActionPoints points =
                                                ActionPoints.defaultPoints();
                                        ActionCount count =
                                                ActionCount.defaultCount();
                                        Action action1 =
                                                new Action(
                                                        jo.getString("action_heading"),
                                                        jo.getString("action_description"));
                                        action1.setId(
                                                Integer.parseInt(jo.getString("id")));
                                        action1.setImageUrl(
                                                jo.getString("action_image"));
                                        action1.setLink(
                                                jo.getString("action_link"));
                                        points.setCount(
                                                Integer.parseInt(
                                                        jo.getString("action_points")));
                                        count.setCount(
                                                Integer.parseInt(
                                                        jo.getString("action_frequency")));
                                        count.setType(
                                                jo.getString("action_frequency_type"));
                                        action1.setActionCount(
                                                count);
                                        String pointss = jo.getString("action_points");
                                        points.setCount(Integer.parseInt(pointss));
                                        action1.setActionPoints(
                                                points);
                                        LogUtil.d(TAG, "action :" + action1.toString());
                                        actions.add(action1);
                                    }
                                    ActionsData netData = new
                                            ActionsData(
                                            "actions");
                                    netData.setActions(
                                            actions);
                                    returnData(netData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    LogUtil.e(TAG, "getactions error");
                                    returnData(null);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                LogUtil.e(TAG, "Network error");
                                returnData(null);
                            }
                        });
        RequestQueue requestQueue =
                Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        return actions;

    }

    public ArrayList<Event> getEvents() {
        LogUtil.d(TAG, "getting events");
        final ArrayList<Event> events =
                new ArrayList<>();
        String url =
                inc.bait.jubilee.model
                        .persistence.net
                        .Address.JUBILEE_EVENTS.toString();
        StringRequest stringRequest =
                new StringRequest(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(
                                    String response) {
                                LogUtil.d(TAG, "get events successful");
                                JSONObject jsonObject;
                                JSONArray array;
                                try {
                                    jsonObject =
                                            new JSONObject(
                                                    response);
                                    array = jsonObject.getJSONArray(
                                            "events");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject jo =
                                                array.getJSONObject(i);
                                        EventCount count =
                                                EventCount.defaultEvent();
                                        Event event =
                                                new Event(jo.getString("heading"),
                                                        jo.getString(
                                                                "description"));
                                        event.setId(
                                                Integer.parseInt(jo.getString("id")));
                                        event.setImageUrl(
                                                jo.getString("icon"));
                                        event.setLink(jo.getString("link"));
                                        String views = jo.getString("views");
                                        count.setCount(Integer.parseInt(views));
                                        count.setType("views");
                                        //TODO setting image count
                                        event.setEventCount(
                                                count);
                                        LogUtil.d(TAG, "event :" + event.toString());
                                        events.add(event);

                                    }
                                    EventsData netData = new
                                            EventsData("events");
                                    netData.setEvents(events);
                                    returnData(netData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    LogUtil.e(TAG, "getting events error");
                                    returnData(null);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(
                                    VolleyError error) {
                                LogUtil.e(TAG, "Network error");
                                returnData(null);
                            }
                        });

        RequestQueue requestQueue =
                Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        return events;
    }

    public ArrayList<News> getNews() {
        LogUtil.d(TAG, "getting news");
        final ArrayList<News> newses = new ArrayList<>();
        String url = inc.bait.jubilee.model
                .persistence.net
                .Address.JUBILEE_NEWS.toString();
        StringRequest stringRequest =
                new StringRequest(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(
                                    String response) {
                                LogUtil.d(TAG, "getting news successful");
                                JSONObject jsonObject;
                                JSONArray news;
                                try {
                                    jsonObject =
                                            new JSONObject(
                                                    response);
                                    news = jsonObject.getJSONArray(
                                            "news");
                                    for (int i = 0; i < news.length(); i++) {
                                        JSONObject jo =
                                                news.getJSONObject(i);
                                        News news1 =
                                                new News(
                                                        jo.getString(
                                                                "news_title"),
                                                        jo.getString(
                                                                "news_info"));
                                        news1.setId(
                                                Integer.parseInt(jo.getString("id")));
                                        news1.setNewsLink(jo.getString("news_link"));
                                        news1.setImageUrl(
                                                jo.getString(
                                                        "news_image"));
                                        LogUtil.d(TAG, "news : " + news.toString());
                                        newses.add(news1);
                                    }
                                    NewsData netData = new
                                            NewsData(
                                            "news");
                                    netData.setNewses(
                                            newses);
                                    returnData(netData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    LogUtil.e(TAG, "getting news error");
                                    returnData(null);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(
                                    VolleyError error) {
                                LogUtil.e(TAG, "network error");
                                returnData(null);
                            }
                        });

        RequestQueue requestQueue =
                Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        return newses;
    }

    public boolean addEventViews(Event event) {
        LogUtil.d(TAG, "adding event views");
        String url = inc.bait.jubilee.model
                .persistence.net
                .Address.JUBILEE_ADD_EVENT_VIEWS.toString();
        StringRequest stringRequest =
                new StringRequest(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(
                                    String response) {
                                LogUtil.d(TAG, "adding views successful");
                                try {
                                    JSONObject jObj =
                                            new JSONObject(response);
                                    boolean error =
                                            jObj.getBoolean("error");
                                    EventViewData data =
                                            new EventViewData("add_view");
                                    data.setAdded(!error);
                                    returnData(data);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    LogUtil.e(TAG,
                                            e.getMessage());
                                    returnData(null);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(
                                    VolleyError error) {
                                LogUtil.e(TAG, "network error");
                                returnData(null);
                            }
                        });

        RequestQueue requestQueue =
                Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        return false;

    }


    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NetData getNetData() {
        return netData;
    }

    public void setNetData(NetData netData) {
        this.netData = netData;
    }

    public boolean isCanFindLocation() {
        return canFindLocation;
    }

    public void setCanFindLocation(boolean canFindLocation) {
        this.canFindLocation = canFindLocation;
    }

    public boolean isResolvingError() {
        return resolvingError;
    }

    public void setResolvingError(boolean resolvingError) {
        this.resolvingError = resolvingError;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public GoogleApiClient.ConnectionCallbacks getConnectionCallbacks() {
        return connectionCallbacks;
    }

    public void setConnectionCallbacks(
            GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        this.connectionCallbacks = connectionCallbacks;
    }

    public GoogleApiClient
            .OnConnectionFailedListener
    getConnectionFailedListener() {
        return connectionFailedListener;
    }

    public void setConnectionFailedListener(
            GoogleApiClient.OnConnectionFailedListener
                    connectionFailedListener) {
        this.connectionFailedListener = connectionFailedListener;
    }

    private void returnData(final NetData data) {
        if (getNetworkListener() != null) {
            getNetworkListener().OnOperationComplete(data);
        }

    }

    public OnCompleteListener getNetworkListener() {
        return networkListener;
    }

    public void setNetworkListener(OnCompleteListener networkListener) {
        this.networkListener = networkListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public interface OnCompleteListener<T extends NetData> {
        void OnOperationComplete(T t);

    }

    public class CacheRequest extends Request<NetworkResponse> {
        private final Response.ErrorListener mErrorListener;
        private final Response.Listener<NetworkResponse> mListener;

        public CacheRequest(int method, String url,
                            Response.Listener<NetworkResponse> listener,
                            Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
        }

        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
            Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
            if (cacheEntry == null) {
                cacheEntry = new Cache.Entry();
            }
            long now = System.currentTimeMillis();
            long softExpire = now + 180000;
            long ttl = now + 86400000;
            cacheEntry.data = response.data;
            cacheEntry.softTtl = softExpire;
            cacheEntry.ttl = ttl;
            String headerValue = (String) response.headers.get("Date");
            if (headerValue != null) {
                cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
            }
            headerValue = (String) response.headers.get("Last-Modified");
            if (headerValue != null) {
                cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
            }
            cacheEntry.responseHeaders = response.headers;
            return Response.success(response, cacheEntry);
        }

        protected void deliverResponse(NetworkResponse response) {
            this.mListener.onResponse(response);
        }

        protected VolleyError parseNetworkError(VolleyError volleyError) {
            return super.parseNetworkError(volleyError);
        }

        public void deliverError(VolleyError error) {
            this.mErrorListener.onErrorResponse(error);
        }
    }
}
