package com.buddy.sample.chatapp;

import android.content.Intent;

public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);
    }
}
