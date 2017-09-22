package com.buddy.sample.chatapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.buddy.sdk.Buddy;

import java.util.Date;


public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    private static final String TAG = "GcmListenerService";

    public static final String ACTION_MESSAGE_RECEIVED =
            "com.buddy.sample.buddychat.MESSAGE_RECEIVED";

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

        Log.w(TAG, "Deleted messages on server.");
    }

    @Override
    public void onSendError(String msgId, String error) {
        super.onSendError(msgId, error);

        Log.e(TAG, "Send error: " + error);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String userName = data.getString("userName");
        String message = data.getString("message");
        String payload = data.getString("payload");

        Intent receivedDataIntent = new Intent();
        receivedDataIntent.putExtras(data);
        Buddy.recordNotificationReceived(receivedDataIntent);

        sendNotification(userName, message, payload);

        Log.i(TAG, "Received: " + data.toString());
    }

    private void sendNotification(String userName, String message, String payload) {

        Intent payloadIntent = new Intent(ACTION_MESSAGE_RECEIVED);

        if (payload != null && BuddyChatApplication.activeChat != null) {

            // if we got a payload and a chatwindow is being shown
            // then just send a payload-only message
            payloadIntent.putExtra("payload", payload);
        } else {

            // otherwise, no chat is shown so show the push UI
            populateIntent(payloadIntent, userName, message, payload);

            if (message != null) {
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                Intent innerIntent = new Intent(this, Chat.class);

                populateIntent(innerIntent, userName, message, payload);

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentText(message)
                                .setContentTitle("Buddy Chat")
                                .setContentIntent(PendingIntent.getActivity(this, 0, innerIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                                .setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_ALL);

                notificationManager.notify((int) new Date().getTime(), builder.build());
            }
        }

        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(payloadIntent);
    }

    private void populateIntent(Intent intent, String u, String m, String p) {

        String uid = null;
        if (p != null) {
            String[] parts = Chat.crackPayload(p);
            if (parts.length == 2) {
                uid = parts[Chat.PART_USERID];
            } else {
                Log.e(TAG, "Bad payload: " + p);
            }
        }

        intent.putExtra("payload", p);
        intent.putExtra("userId", uid);
        intent.putExtra("userName", u);
        intent.putExtra("message", m);
    }
}