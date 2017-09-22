package com.buddy.sample.chatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

public class AbstractActivity extends Activity {

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    /* method to check for internet connection */
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    /*  method to validate email id */
    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /* method to validate phone number*/
    public boolean isPhoneValid(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        } else {
            if (phoneNumber.length() < 6 || phoneNumber.length() > 13) {
                return false;
            } else {
                return android.util.Patterns.PHONE.matcher(phoneNumber).matches();
            }
        }
    }

    /*  method to display toast message*/
    public void displayMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void pushActivity(Context mContext, Class activityClass, Bundle extras, boolean finishStatus) {
        try {
            Intent intent = new Intent(mContext, activityClass);
            if (extras != null) {
                intent.putExtras(extras);
            }
            startActivity(intent);
            if (finishStatus) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
