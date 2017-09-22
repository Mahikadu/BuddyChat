/*
 * Copyright (C) 2016 Buddy Platform, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.buddy.sample.chatapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import com.buddy.sdk.Buddy;
import com.buddy.sdk.BuddyCallback;
import com.buddy.sdk.BuddyResult;
import com.buddy.sdk.ConnectivityLevel;
import com.buddy.sdk.ConnectivityLevelChangedCallback;
import com.buddy.sdk.UserAuthenticationRequiredCallback;
import com.buddy.sdk.models.User;

import android.support.multidex.MultiDex;
import android.widget.Toast;
import android.view.Gravity;

public class BuddyChatApplication extends Application {

    /**
     * Substitute your own sender ID here. This is the Project Number you got
     * from the Google Developers Console, as described in the accompanying README.md.
     */
    public static final String SENDER_ID = "433521377220";


    /**
     * Substitute your Buddy app's App ID and App Key here. You can create a Buddy app
     * at http://dev.buddyplatform.com. For more details see the accompanying README.md.
     */
    public static final String APPID = "bbbbbc.PCwDGrKNsqwHc";
    public static final String APPKEY = "b7083f20-82b8-d1aa-d1fe-ae969552ccc3";


    public static BuddyChatApplication instance;
    public static Chat activeChat;
    public User currentUser;
    boolean loginVisible;

    public BuddyChatApplication() {
        instance = this;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /*@Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }*/

    @Override
    public void onCreate() {
        super.onCreate();

        Buddy.init(getApplicationContext(), APPID, APPKEY);

        // Automatically show the Login activity whenever
        // authentication fails for a user-level API call
        Buddy.setUserAuthenticationRequiredCallback(new UserAuthenticationRequiredCallback() {
            @Override
            public void authenticate() {
                if (loginVisible) {
                    return;
                }
                loginVisible = true;
                Intent loginIntent = new Intent(BuddyChatApplication.this, Login.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
            }
        });

        Buddy.setConnectivityLevelChangedCallback(new ConnectivityLevelChangedCallback() {
            @Override
            public void connectivityLevelChanged(ConnectivityLevel level) {
            String message = getResources().getString((level == ConnectivityLevel.None) ?
                    R.string.connection_lost :
                    R.string.reconnected);

            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            }
        });
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void getCurrentUser(final boolean refresh, final GetCurrentUserCallback callback) {

        if (currentUser != null && !refresh) {
            if (callback != null) {
                callback.complete(currentUser);
            }
        } else {
            Buddy.getCurrentUser(new BuddyCallback<User>(User.class) {
                @Override
                public void completed(BuddyResult<User> result) {
                    if (result.getIsSuccess() && result.getResult() != null) {
                        currentUser = result.getResult();
                    }
                    if (callback != null) {
                        callback.complete(currentUser);
                    }
                }
            });
        }
    }
}
