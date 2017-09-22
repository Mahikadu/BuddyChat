package com.buddy.sample.chatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buddy.sdk.*;
import com.buddy.sdk.models.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.*;


// The main screen lists the apps users, so we can pick who to chat with
// and allows the current user to logout.
public class MainScreen extends Activity {
    private static final String TAG = "MainScreen";

    ListView _users;
    //UsersSimpleAdapter _adapter;
    List<User> userList;
    private static CustomAdapter adapter;

    User currentUser;

    public static boolean checkPlayServices(Context context, Activity activity) {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();

        int resultCode = googleAPI.isGooglePlayServicesAvailable(context);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(activity, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.i(TAG, "This device is not supported for push");
            } else {
                final Activity alertActivity = activity;
                new AlertDialog.Builder(activity)
                        .setMessage("This sample requires the Google APK.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertActivity.finish();
                                System.exit(0);
                            }
                        })
                        .show();
            }

            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkPlayServices(getApplicationContext(), this)) {
            return; // TODO: verify this is the right thing to do for onCreate
        }

        setContentView(R.layout.activity_main_screen);

        final Button btnLogout = (Button)findViewById(R.id.btnLogout);
       // final ImageView iv_home = (ImageView)findViewById(R.id.iv_home);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Buddy.logoutUser(new BuddyCallback<Boolean>(Boolean.class) {
                    @Override
                    public void completed(BuddyResult<Boolean> result) {
                        Intent i = new Intent(getBaseContext(), Login.class);
                        BuddyChatApplication.instance.setCurrentUser(null);

                        // Trigger the login dialog. If the current user is not set, the
                        // Buddy.setUserAuthenticationRequriedCallback fires.
                        BuddyChatApplication.instance.getCurrentUser(true, null);
                        startActivity(i);
                    }
                });
            }
        });
        final TextView lblHello = (TextView) findViewById(R.id.lblHello);

        BuddyChatApplication.instance.getCurrentUser(false, new GetCurrentUserCallback() {
            @Override
            public void complete(User user) {
                if (user != null) {
                    lblHello.setText(String.format("Hello %s!", user.userName));
                }
            }
        });

        // set up our users list
        _users = (ListView) findViewById(R.id.lvUsers);

        _users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // on click of an item, fire up a chat
                //
                User u = (User) _users.getItemAtPosition(i);

                startChat(u);
            }
        });

       // _adapter = new UsersSimpleAdapter(getBaseContext());
       // _users.setAdapter(_adapter);


        refreshList();
    }

    private void startChat(User u) {
        Intent ci = new Intent(getBaseContext(), Chat.class);

        ci.putExtra("userName", u.userName);
        ci.putExtra("name", u.firstName + " " + u.lastName);
        ci.putExtra("userId", u.id);

        startActivity(ci);
    }

    private void refreshList() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sortOrder", "-lastModified");

        // load the list of users, sorting by last login, descending
        //
        Buddy.get("/users", params, new BuddyCallback<PagedResult>(PagedResult.class) {
            @Override
            public void completed(BuddyResult<PagedResult> result) {

                if (result.getIsSuccess()) {

                    userList = result.getResult().convertPageResults(User.class);

                    // remove current user from the list
                    //
                    for (int i = 0; i < userList.size(); i++) {

                        User usr = userList.get(i);
                        if (usr.userName.equals(currentUser.userName)) {
                            userList.remove(i);
                            break;
                        }
                    }
//                    _adapter.setItemList(userList);
//                    _adapter.notifyDataSetChanged();

                    //adapter.setItemList(userList);
                    //adapter.notifyDataSetChanged();

                    adapter= new CustomAdapter(userList,getApplicationContext());
                    _users.setAdapter(adapter);

                }
            }
        });
    }

    private BroadcastReceiver onEvent = new BroadcastReceiver() {
        public void onReceive(Context ctx, Intent i) {
            String payload = i.getStringExtra("payload");

            if (payload == null) {
                return;
            }

            // if we get a chat message, then go ahead and
            // fire up the chat activity.

            String[] parts = payload.split("\t");

            String msg = parts[0];

            String id = parts[1];

            // find the user, then start the chat.
          /*  if (_adapter.getItemList() != null) {
                for (User u : _adapter.getItemList()) {
                    if (u.id.equals((id))) {
                        startChat(u);
                        break;
                    }
                }
            }*/
        }
    };

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(onEvent);

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!checkPlayServices(getApplicationContext(), this)) {
            return; // TODO: verify this is the right thing to do for onCreate
        }

        IntentFilter f = new IntentFilter(GcmListenerService.ACTION_MESSAGE_RECEIVED);

        LocalBroadcastManager.getInstance(this).registerReceiver(onEvent, f);

        // make sure we have a current user.  Doing this will fire up the login dialog
        // if we don't have one for some reason
        BuddyChatApplication.instance.getCurrentUser(false, new GetCurrentUserCallback() {

            public void complete(User u) {
                currentUser = u;
                if (u != null) {
                    refreshList();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
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

    class UsersSimpleAdapter extends SimpleAdapter<User> {

        public UsersSimpleAdapter(Context c) {
            super(null, c);
        }

        protected <T> void populateView(View v, T u) {
            User user = (User) u;
            TextView text1 = (TextView) v.findViewById(android.R.id.text1);
            text1.setText(String.format("%s %s (%s)", user.firstName, user.lastName, user.userName));

            TextView text2 = (TextView) v.findViewById(android.R.id.text2);

            Date now = new Date();
            long before = user.lastLogin.getTime();
            long current = now.getTime();
            long seconds = (current - before) / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            String friendly = null;
            long num = 0;
            if (days > 0) {
                num = days;
                friendly = days + " day";
            }
            else if (hours > 0) {
                num = hours;
                friendly = hours + " hour";
            }
            else {
                num = minutes;
                friendly = minutes + " minute";
            }
            String createdAt = friendly + " ago";

               // text2.setText(String.format("Last Seen Today: %d minutes ago", delta / (60000)));
            text2.setText(createdAt);



        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    class CustomAdapter extends ArrayAdapter<User> {

        private List<User> dataSet;
        Context mContext;

        // View lookup cache
        private class ViewHolder {
            TextView txtName;
            TextView txtType;
            TextView txtVersion;
            ImageView info;
        }

        public CustomAdapter(List<User> data, Context context) {
            super(context, R.layout.row_item, data);
            this.dataSet = data;
            this.mContext=context;

        }

       /* @Override
        public void onClick(View v) {

            int position=(Integer) v.getTag();
            Object object= getItem(position);
            User dataModel=(User)object;

            switch (v.getId())
            {
                case R.id.item_info:

                    break;
            }
        }*/

        private int lastPosition = -1;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            User dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.row_item, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);


                result=convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result=convertView;
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            viewHolder.txtName.setText(String.format("%s %s (%s)", dataModel.firstName, dataModel.lastName, dataModel.userName));

            Date now = new Date();
            long before = dataModel.lastLogin.getTime();
            long current = now.getTime();
            long seconds = (current - before) / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            String friendly = null;
            long num = 0;
            if (days > 0) {
                num = days;
                friendly = days + " day";
            }
            else if (hours > 0) {
                num = hours;
                friendly = hours + " hour";
            }
            else {
                num = minutes;
                friendly = minutes + " minute";
            }
            String createdAt = friendly + " ago";
            viewHolder.txtType.setText(createdAt);

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
