package com.ack.familyfootprints.GCMClientApp;

/**
 * Created by Lucky Goyal on 2/17/2016.
 */

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ack.familyfootprints.model.PingerKeys;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDListSvc";
    AtomicInteger msgId = new AtomicInteger();

    /**.

     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i(TAG, "START FCM regster, Token Refresh");
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.i(TAG, "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
            //sendRegistrationToServer(refreshedToken);
            storeRegistration(refreshedToken);

    }
    private void sendRegistrationToServer(String token) throws IOException {
        Log.d(TAG, "REGISTER USERID: " + token);
        //String name = "Lucky Goyal";
        final String profilePictureUrl;
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        // Register the user at the server.

        FirebaseMessaging.getInstance().send(
                new RemoteMessage.Builder(Globals.GCM_SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId(Integer.toString(msgId.incrementAndGet()))
                        .addData(Globals.ACTION, Globals.REGISTER_NEW_CLIENT)
                        .addData(Globals.REGISTRATION_TOKEN, token)
                        .addData(Globals.NAME, QuickstartPreferences.PROFILE_NAME)
                        .addData(Globals.MPHONE, QuickstartPreferences.PROFILE_PHONE)
                        .addData(Globals.PICTURE_URL, PingerKeys.DP)
                        .build());
    }
    private void storeRegistration(String token) {
        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER_E, true).apply();
        sharedPreferences.edit().putString(QuickstartPreferences.REG_ID_F, token).apply();
    }
}