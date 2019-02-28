package com.ack.familyfootprints.timelineTab;

 import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ack.familyfootprints.DBHelper.InviteDBHelper;
import com.ack.familyfootprints.GCMClientApp.Globals;
import com.ack.familyfootprints.GCMClientApp.QuickstartPreferences;
import com.ack.familyfootprints.model.PingerKeys;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Lucky Goyal on 3/15/2016.
 */
public class sendNotificationNewLoc {

    AtomicInteger msgId = new AtomicInteger();
    private static final String TAG = sendNotificationNewLoc.class.getSimpleName();
    private SharedPreferences mDefaultSharedPreferences;

    // GCM startup
   // gcm = GoogleCloudMessaging.getInstance(this);

    public sendNotificationNewLoc(Context context) {
    }

    public void handleNewloc(Context context, String contactName, String mobilePhone, String dateSelected, String uts, String strAdrName, String strAdrFull, double lat, double lng, String ute, String oldrowId, String oldTransit, String oldTransitTime, String oldDate, String oldest) {
        Log.d(TAG, "Inside sendNotificationNewLoc");
        if (isSharingAllowed(context)) {
            // String message = prepareMessage(strAdr, dateSelected, uts, lat, lng);
            //sendLocToRegisteredUsers(context, message);
            shareloc(context,contactName, mobilePhone, dateSelected, uts, strAdrName, strAdrFull, lat, lng, ute, oldrowId, oldTransit, oldTransitTime, oldDate, oldest);
        } else {
            Log.d(TAG, "Sharing not Allowed");
        }
    }
    public void handleNewlocPhone(Context context, String contactName, String mobilePhone, String dateSelected, String uts, String strAdrName, String strAdrFull, double lat, double lng, String Phone, String Name, String RegId) {
        Log.d(TAG, "Inside Phn: sendNotificationNewLoc ");
        if (isSharingAllowed(context)) {
            // String message = prepareMessage(strAdr, dateSelected, uts, lat, lng);
            //sendLocToRegisteredUsers(context, message);
            sharelocPhone(context,contactName, mobilePhone, dateSelected, uts, strAdrName, strAdrFull, lat, lng, Phone, Name, RegId);
        } else {
            Log.d(TAG, "Sharing not Allowed");
        }
    }

    public boolean isSharingAllowed(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        boolean checkSharePerm = sharedPreferences
                .getBoolean(QuickstartPreferences.SHARE_PERMISSION, true);
        return checkSharePerm;
    }
    private void sendLocToRegisteredUsers(final Context context, final String message) {
        String messageType = "NEW_LOC";
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {


                Log.d(TAG, "About to call fcm.send broadcast for location");
                FirebaseMessaging.getInstance().send(
                        new RemoteMessage.Builder(Globals.GCM_SENDER_ID + "@gcm.googleapis.com")
                                .setMessageId(Integer.toString(msgId.incrementAndGet()))
                                .addData(PingerKeys.ACTION, Globals.NEW_LOC)
                                .addData(PingerKeys.SENDER, FirebaseInstanceId.getInstance().getToken())

                                .addData(Globals.NEW_LOC_DATA, message)
                                        //.addData(Globals.TIME_TO_LIVE,Globals.GCM_TIME_TO_LIVE)
                                .build());

                String msg = "Sent New Location Broadcast";
                Log.d(TAG, "Done fcm.send broadcast");
                return msg;

            }
            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                //Log.i(TAG, "No More Groups Now");
            }
        }.execute(message, messageType);
    }
    private void shareloc(Context context, String fromName, String fromPhone, String dateSelected, String uts, String strAdrName, String strAdrFull, double lat, double lng, String oute, String oldrowId, String oldTransit, String oldTransitTime, String oldDate, String oldest )
    {

        InviteDBHelper inviteDb = new InviteDBHelper(context);
        Cursor inviteCursor = inviteDb.getAllAcceptedInvites();
        int row = inviteCursor.getCount();
        if (row > 0 ) {
            if (inviteCursor.moveToFirst()) {
                try {
                    do {
                        String toName = inviteCursor.getString(inviteCursor.getColumnIndex("name"));
                        String toPhone = inviteCursor.getString(inviteCursor.getColumnIndex("phone"));
                        String toRegID = inviteCursor.getString(inviteCursor.getColumnIndex("regID"));
                        if (oldrowId.equals(null) ||oute.equals(null)) {
                            Log.i(TAG, "Empty oldrowid");
                            oute = "OTNF";
                            oldrowId = "NA";
                            oldTransit="NA";
                            oldTransitTime="NA";
                            oldDate="NA";
                            oldest="NA";
                         }

                        Log.i(TAG, "name is " + toName);
                        Log.i(TAG, "phone is " + toPhone);
                        Log.i(TAG, "regID is " + toRegID);
                        Log.i(TAG, "oldrow is " + oldrowId);



                        String token = FirebaseInstanceId.getInstance().getToken();
                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(context);
                        String profileName = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);
                        String profilePhone = sharedPreferences.getString(QuickstartPreferences.PROFILE_PHONE, null);

                        Log.w(TAG, "Sending Location to " + toName + " from: " + fromName);

                        FirebaseMessaging.getInstance().send(
                                new RemoteMessage.Builder(Globals.GCM_SENDER_ID + "@gcm.googleapis.com")
                                        .setMessageId(Integer.toString(msgId.incrementAndGet()))
                                        .addData(PingerKeys.ACTION, Globals.NEW_LOC)
                                        .addData(Globals.REGISTRATION_TOKEN, FirebaseInstanceId.getInstance().getToken())
                                        .addData(PingerKeys.NAME, profileName)
                                        .addData(PingerKeys.PHONE, profilePhone)
                                        .addData(PingerKeys.DATESELECTED, dateSelected)
                                        .addData(PingerKeys.UTS, uts)
                                        .addData(PingerKeys.PLACENAME, strAdrName)
                                        .addData(PingerKeys.PLACEADR, strAdrFull)
                                        .addData(PingerKeys.LAT, String.valueOf(lat))
                                        .addData(PingerKeys.LNG, String.valueOf(lng))
                                        .addData(PingerKeys.TO, toRegID)
                                        .addData(PingerKeys.TOPHONE, toPhone)
                                        .addData(PingerKeys.OUTS, oute)
                                        .addData(PingerKeys.OROWID, oldrowId)
                                        .addData(PingerKeys.OTRANSIT, oldTransit )
                                        .addData(PingerKeys.OTRANSITTIME, oldTransitTime)
                                        .addData(PingerKeys.ODATE, oldDate)
                                        .addData(PingerKeys.OEST, oldest)
                                        .build());
                    }
                while (inviteCursor.moveToNext()) ;
            } finally{
                    if (inviteCursor != null && !inviteCursor.isClosed())
                        inviteCursor.close();
                }
            } Log.i(TAG, "No More contacts");
        } else {
            Log.i(TAG, "No more contacts left to receive data");
        }
    }
    private void sharelocPhone(Context context, String fromName, String fromPhone, String dateSelected, String uts, String strAdrName, String strAdrFull, double lat, double lng, String Phone, String Name, String RegId )
    {
         SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
         String profileName = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);
         String profilePhone = sharedPreferences.getString(QuickstartPreferences.PROFILE_PHONE, null);

         Log.w(TAG, "Sending Old Location to " + Name + " from: " + fromName);

          FirebaseMessaging.getInstance().send(

                            new RemoteMessage.Builder(Globals.GCM_SENDER_ID + "@gcm.googleapis.com")
                                    .setMessageId(Integer.toString(msgId.incrementAndGet()))
                                    .addData(PingerKeys.ACTION, Globals.NEW_LOC)
                                    .addData(Globals.REGISTRATION_TOKEN, FirebaseInstanceId.getInstance().getToken())
                                    .addData(PingerKeys.NAME, profileName)
                                    .addData(PingerKeys.PHONE, profilePhone)
                                    .addData(PingerKeys.DATESELECTED, dateSelected)
                                    .addData(PingerKeys.UTS, uts)
                                    .addData(PingerKeys.PLACENAME, strAdrName)
                                    .addData(PingerKeys.PLACEADR, strAdrFull)
                                    .addData(PingerKeys.LAT, String.valueOf(lat))
                                    .addData(PingerKeys.LNG, String.valueOf(lng))
                                    .addData(PingerKeys.TO, RegId)
                                    .addData(PingerKeys.TOPHONE, Phone)
                                    .addData(PingerKeys.TONAME, Name)
                                    .addData(PingerKeys.OUTS, "OTNF")
                                    .addData(PingerKeys.OROWID, "NA")
                                    .addData(PingerKeys.OTRANSIT, "NA" )
                                    .addData(PingerKeys.OTRANSITTIME, "NA")
                                    .addData(PingerKeys.ODATE, "NA")
                                    .addData(PingerKeys.OEST, "NA")
                                    .build());


            }
        }

