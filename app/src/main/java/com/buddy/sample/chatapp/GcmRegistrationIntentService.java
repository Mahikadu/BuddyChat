package com.buddy.sample.chatapp;

import android.app.IntentService;
import android.content.Intent;

import com.buddy.sdk.Buddy;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class GcmRegistrationIntentService extends IntentService {
    private static final String TAG = "GcmRegistrationService";

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                String token = instanceID.getToken(BuddyChatApplication.SENDER_ID,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                Buddy.setPushToken(token, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}