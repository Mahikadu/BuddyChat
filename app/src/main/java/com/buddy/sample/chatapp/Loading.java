package com.buddy.sample.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.buddy.sdk.models.User;

public class Loading extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        boolean isPushSupported = MainScreen.checkPlayServices(getApplicationContext(), this);

        if (isPushSupported) {
            Intent intent = new Intent(this, GcmRegistrationIntentService.class);
            startService(intent);

            // see if there is a logged in user
            BuddyChatApplication.instance.getCurrentUser(false, new GetCurrentUserCallback() {
                @Override
                public void complete(User user) {
                    if (user != null) {
                        Intent i = new Intent(getBaseContext(), MainScreen.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
