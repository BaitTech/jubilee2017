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

package inc.bait.jubilee.model.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import inc.bait.jubilee.model.error.NoCallBacksFoundError;
import inc.bait.jubilee.model.persistence.net.NetBridge;
import inc.bait.jubilee.model.persistence.net.NetData;

public abstract class Transaction<T, X> extends Handler {
    private T[]  params;
    private boolean isCustom =
            false;
    private Custom custom;
    private final ThreadLocal<Runnable> threadLocal =
            new ThreadLocal<Runnable>() {
                @Override
                protected Runnable initialValue() {
                    return new Runnable() {
                        @Override
                        public void run() {
                            if (isCustom) {
                                custom.transact();
                                return;
                            }
                            Task task = new Task();
                            if (getParams() != null) {
                                task.execute(getParams());
                            }
                            else {
                                task.execute();
                            }
                        }
                    };
                }
            };
    private OnUpdateListener<T, X> onUpdateListener;
    private OnCompleteListener<T> onCompleteListener
            = null;
    private Context context;
    private Context activityContext;
    private CallBacks<T, X> callBacks;
    private int id;

    private Transaction() {
        super(new Handler().getLooper());
        onUpdateListener = getOnUpdateListener();
        callBacks = getCallBacks();
    }

    public Transaction(Context context,
                       Context activityContext,
                       int id) {
        this();
        this.setContext(context);
        this.setActivityContext(activityContext);
        this.id = id;
    }

    private Transaction(Custom custom) {
        this();
        this.custom = custom;
        this.isCustom = true;
    }

    public static Transaction WithTask(final Custom custom) {
        return new Transaction(custom) {
            @Override
            protected void checkCallBacks()
                    throws NoCallBacksFoundError {
                if (custom == null) {
                    throw new NoCallBacksFoundError();
                }
            }

            @Override
            public OnUpdateListener getOnUpdateListener() {
                return null;
            }

            @Override
            public CallBacks getCallBacks() {
                return null;
            }
        };
    }

    public void execute() {
        try {
            checkCallBacks();
        } catch (NoCallBacksFoundError error) {
            error.printStackTrace();
            return;
        }
        post(getThreadLocal());
    }

    public void execute(long millis) {
        try {
            checkCallBacks();
        } catch (NoCallBacksFoundError error) {
            error.printStackTrace();
            return;
        }
        postDelayed(getThreadLocal(),
                millis);
    }

    private T[] getParams() {
        return params;
    }

    public void setParams(T[] params) {
        this.params = params;
    }

    public abstract OnUpdateListener<T, X> getOnUpdateListener();

    protected void checkCallBacks()
            throws NoCallBacksFoundError {
        if (callBacks == null) {
            throw new NoCallBacksFoundError();
        }
    }

    private void finalize(T t) {
        if (getOnCompleteListener() != null) {
            getOnCompleteListener().OnComplete(id,
                    t);
        }
    }

    public abstract CallBacks<T, X> getCallBacks();

    private Runnable getThreadLocal() {
        return threadLocal.get();
    }

    private OnCompleteListener<T> getOnCompleteListener() {
        return onCompleteListener;
    }

    public void setOnCompleteListener(
            OnCompleteListener<T> onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getActivityContext() {
        return activityContext;
    }

    private void setActivityContext(Context activityContext) {
        this.activityContext = activityContext;
    }

    public interface CallBacks<T, X> {
        /**
         * method is called
         * just before
         * the task is started
         */
        void OnStart();

        /**
         * OnExecute the main
         * logic here
         */
        T OnExecute();

        /**
         * show an updated
         * message
         * here while the task
         * is continuing
         */

        void OnProgress(X... x);

        /**
         * call this end the custom
         *
         * @param t the data returned
         */
        void OnEnd(T t);
    }

    public interface OnCompleteListener<T> {
        void OnComplete(int id,
                        T t);
    }

    public interface Custom {
        void transact();
    }

    public interface OnUpdateListener<T, X> {
        /**
         * @param t the array of <T parameters
         * @return array of X
         */
        X[] OnUpdate(T... t);
    }

    public abstract static class NetTransaction<T extends NetData>
            extends Transaction<T, Integer>
            implements NetBridge.OnCompleteListener<T> {
        public static final int LOC_FIND =
                ID.LocationFindTransactionID;
        public static final int REGISTER_ACCOUNT =
                ID.RegisterTransaction;
        public static final int LOGIN_ACCOUNT =
                ID.LoginTransaction;
        public static final int FETCH_EVENTS =
                ID.EventsTransactionID;
        public static final int FETCH_ACTIONS =
                ID.ActionsTransactionID;
        public static final int FETCH_NEWS =
                ID.FetchNewsTransactionID;
        public static final int INCREMENT_EVENT_VIEW =
                ID.UpdateViewTransactionID;
        public static final int INCREMENT_ACTION_VIEW =
                ID.ActionViewTransactionID;
        public static final int INCREMENT_NEWS_SHARE =
                ID.NewsShareTransactionID;
        public static final int INCREMENT_USER_POINTS =
                ID.UpdatePointsTransactionID;
        private NetBridge netBridge;

        public NetTransaction(Context context,
                              Context activityContext,
                              int id) {
            super(context,
                    activityContext,
                    id);
            netBridge = new NetBridge(context);
        }

        public NetTransaction(Context context,
                              String url,
                              int id) {
            super(context,
                    null,
                    id);
            netBridge = new NetBridge(url);
            netBridge.setNetworkListener(this);
        }

        public NetTransaction(Activity activity,
                              int id) {
            super(activity.getApplicationContext(),
                    null,
                    id);
            netBridge = new NetBridge(activity);
            netBridge.setNetworkListener(this);
        }

        public NetTransaction(Application application,
                              int id) {
            super(application.getApplicationContext(),
                    null,
                    id);
            netBridge = new NetBridge(getContext());
            netBridge.setApplication(
                    application);
            netBridge.setNetworkListener(
                    this);
        }

        public NetBridge getNetBridge() {
            return netBridge;
        }

        @Override
        public void OnOperationComplete(T t) {
            super.finalize(t);
        }
    }

    private class Task extends AsyncTask<T, X, T> {
        @SafeVarargs
        @Override
        protected final T doInBackground(T... params) {
            if (onUpdateListener != null &&
                    params.length != 0) {
                publishProgress(onUpdateListener.OnUpdate(params));
            }
            return callBacks.OnExecute();
        }

        @Override
        protected void onPreExecute() {
            callBacks.OnStart();
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(X... values) {
            super.onProgressUpdate(values);
            callBacks.OnProgress(values);
        }

        @Override
        protected void onPostExecute(T t) {
            super.onPostExecute(t);
            callBacks.OnEnd(t);
            Transaction.this.finalize(t);
        }
    }
}
