package com.ack.familyfootprints;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ack.familyfootprints.GCMClientApp.QuickstartPreferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.atomic.AtomicInteger;


//import android.app.Fragment;

//import android.widget.Toolbar;

/**
 * Created by Lucky Goyal on 11/26/2015.
 */
public class MainActivity extends AppCompatActivity {
    AtomicInteger msgId = new AtomicInteger();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    public MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        final boolean sentToken_check ;
        Log.d(TAG, "OnCreate");

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String profileName = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);
        String profilePhone = sharedPreferences.getString(QuickstartPreferences.PROFILE_PHONE, null);


        Log.d(TAG, "profileName is " + profileName);
        Log.d(TAG, "profilePhone is " + profilePhone);


        checkPlayServices();
        String regId = FirebaseInstanceId.getInstance().getToken();
         //String regId = sharedPreferences.getString(QuickstartPreferences.REG_ID_F, null);
        if (regId!=null) {
            Log.d(TAG, "Already Registered regId is" + regId);
           // loadNewContacts(prorfileName,profilePhone);
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            /*}else if(checkPlayServices()){
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);*/
        } else {
            Log.d(TAG, "Still Awaiting FCM Regis");
            Intent intent_maps = new Intent(this, MapsActivity.class);
            startActivity(intent_maps);
        }
         /*setContentView(R.layout.activity_main);
            Log.d(TAG, "Progressing for Registration");
            Toast.makeText(getApplicationContext(), "Progressing for Registration!", Toast.LENGTH_SHORT).show();
            mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "got broadcast receier");
                    //Log.d(TAG, "got broadcast receier");
                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences
                            .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER_E, false);
                    if (sentToken) {
                        mInformationTextView.setText(getString(R.string.gcm_send_message));
                        Intent intent_maps = new Intent(context, MapsActivity.class);
                        startActivity(intent_maps);
                    } else {
                        mInformationTextView.setText(getString(R.string.token_error_message));
                    }
                }
            };
            Log.d(TAG, "checking registration");
            mInformationTextView = (TextView) findViewById(R.id.informationTextView);
            if (checkPlayServices()) {
                Log.d(TAG, "Before calling registraion intent");
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(getApplicationContext(), MyInstanceIDListenerService.class);
                startService(intent);
            }
       */
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }
    @Override
    protected void onPause() {
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.i(TAG, "Google Play service check failed");
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        Log.i(TAG, "Google Play service check worked");
        return true;
    }
}
