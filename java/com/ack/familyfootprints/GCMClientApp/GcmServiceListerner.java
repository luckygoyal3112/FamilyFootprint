package com.ack.familyfootprints.GCMClientApp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.ack.familyfootprints.DBHelper.LocationDBHelper;
import com.ack.familyfootprints.MainActivity;
import com.ack.familyfootprints.R;
import com.ack.familyfootprints.Users.UserLocAdapater;
import com.ack.familyfootprints.Users.UsersAdapter;
import com.ack.familyfootprints.model.Ping;
import com.ack.familyfootprints.model.Pinger;
import com.ack.familyfootprints.model.PingerLocation;
import com.ack.familyfootprints.timelineTab.sendNotificationNewLoc;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
/**
 * Created by Lucky Goyal on 4/4/2016.
 *
 */
public class GcmServiceListerner extends FirebaseMessagingService {
    //private LoggingService.Logger logger;
    private static final String TAG = GcmServiceListerner.class.getSimpleName();

    public GcmServiceListerner() {
        // logger = new LoggingService.Logger(this);
        Log.d(TAG, "Received Msg  from GCM CSS");
    }
    @Override
    public void onMessageReceived(RemoteMessage message){
        Log.d(TAG, "Received Msg  from GCM CSS" );
        String from = message.getFrom();
        //Map data = message.getData();
        Bundle data = new Bundle();
        for (Map.Entry<String, String> entry : message.getData().entrySet()) {
            data.putString(entry.getKey(), entry.getValue());
        }
        //String notification = message.getNotification().getBody();
        sendNotification("Received GCM Message: " + data.toString());
        if (from == null) {
            Log.w(TAG, "Couldn't determine origin of message. Skipping.");
            return;
        }
        try {
            digestData(data);
        } catch (JSONException e) {
            Log.e(TAG, "onMessageReceived: Could not digest data", e);
        }
    }
    private void digestData(Bundle data) throws JSONException {
        final String action = (String) data.get("action");
        Log.d(TAG, "Action: " + action);
        UsersAdapter cUserAdapter = new UsersAdapter (this);
        if (action == null) {
            Log.w(TAG, "onMessageReceived: Action was null, skipping further processing.");
            return;
        }
        //Intent broadcastIntent = new Intent(action);
        switch (action) {
            case Globals.SEND_CLIENT_LIST:
                Log.d(TAG, "Inside Action: " + action);
                ArrayList<Pinger> pingers = getPingers(data);
                cUserAdapter.addPinger(pingers);
                //broadcastIntent.putParcelableA-rrayListExtra(Globals.PINGERS, pingers);
                break;
            case Globals.BROADCAST_NEW_CLIENT:
                Log.d(TAG, "Inside Action: " + action);
                Pinger newPinger = getNewPinger(data);
                cUserAdapter.addPinger(newPinger);
                //broadcastIntent.putExtra(Globals.NEW_PINGER, newPinger);

                break;
            case Globals.SEND_CONNECTION_LIST:
                Log.d(TAG, "Inside Action: " + action);
                ArrayList<Pinger> pingers_conn = getPingers(data);
                cUserAdapter.addPinger(pingers_conn);
                addConnectionPinger(pingers_conn);
                //broadcastIntent.putParcelableA-rrayListExtra(Globals.PINGERS, pingers);
                break;
            case Globals.PING_CLIENT:
                Log.d(TAG, "Inside Action: " + action  + "Received new location");
                PingerLocation newPingerLoc = getNewPingerLocation(data);
                UserLocAdapater cUserLocAdapter = new UserLocAdapater (this);
                cUserLocAdapter.addLocation(newPingerLoc);
                //broadcastIntent.putExtra(Globals.NEW_PING, newPing);
                break;
            case Globals.INVITE_REQUEST:
                Log.d(TAG, "Inside Action: " + action);
                Pinger newInvite = getNewPinger(data);
                //IR: Invitation Received.
               // UsersAdapter cUserAdapter = new UsersAdapter (this);
                cUserAdapter.updatePingerStatus(newInvite.getPhone(), "IR");
                cUserAdapter.addInvitedPinger(newInvite, "IR");
                //handleInviteNotification(data);
                //broadcastIntent.putExtra(Globals.NEW_PING, newInvite);
                break;
            case Globals.INVITE_REQUEST_RESP:
                Log.d(TAG, "Inside Action: " + action);
                final String invRespFlag = (String) data.get("invresponse");
                Pinger invResponse = getNewPinger(data);
                handleInviteResponse(invResponse, invRespFlag);
                break;
            case Globals.INVITE_REQUEST_RESP_LOC:
                Log.d(TAG, "Inside Action: " + action);
                final String invRespFlagLoc = (String) data.get("invresponse");
                Pinger invResponseLoc = getNewPinger(data);
                handleInviteResponseLoc(invResponseLoc, invRespFlagLoc);
                PingerLocation newPingerLoca = getNewPingerLocation(data);
                UserLocAdapater cUserLocAdaptera = new UserLocAdapater (this);
                cUserLocAdaptera.addLocation(newPingerLoca);
                break;
            case Globals.UNFRIEND_NOTIFICATION:
                Log.d(TAG, "Inside Action: " + action);
                Pinger unfriend_notify = getNewPinger(data);
                handleUnfriendNotification(unfriend_notify.getPhone());
                break;
            case Globals.SIM_CHANGE_DETECTED:
                Log.d(TAG, "Inside Action: " + action);
                Pinger client = getNewPinger(data);
                handleSimChangeDetected(client.getPhone());
                break;
        }
        //LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private void handleSimChangeDetected(String Phone) {
        //showSimChangeDetected(Phone);
    }
    /*private void showSimChangeDetected(String invitedPhone) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to Invite " + invitedName +
                "? By choosing Yes, you will be able to track each other's Timeline, " +
                "once" + invitedName + "accepts your invite.");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                inviteContact(invitedName, invitedPhone, regId);
                try {
                    insertInviteContact(invitedName, invitedPhone, regId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("No", null).show();
    }*/


    public void addConnectionPinger(ArrayList<Pinger> pingers_conn) {
        int listSize = pingers_conn.size();
        for (int i = 0; i<listSize; i++) {
            UsersAdapter cUserAdapter = new UsersAdapter(this);
            Pinger pinger_i = pingers_conn.get(i);
            Log.d(TAG, "Adding connection Pinger for " + pinger_i.getPhone() );
            if (pinger_i.getInvitationRespFlag().equals("IS")) {
                cUserAdapter.addInvitedPinger(pinger_i, "IS");
                cUserAdapter.updatePingerStatus(pinger_i.getPhone(), "IS");
            } else if (pinger_i.getInvitationRespFlag().equals("IR")) {
                cUserAdapter.addInvitedPinger(pinger_i, "IR");
            } else if (pinger_i.getInvitationRespFlag().equals("IA")) {
                cUserAdapter.addInvitedPinger(pinger_i, "IS");
                storeAcceptedInviteNotificationLoc(pinger_i.getPhone(), pinger_i.getName(), pinger_i.getRegistrationToken());
            } else if (pinger_i.getInvitationRespFlag().equals("ID")) {
                cUserAdapter.addInvitedPinger(pinger_i, "IS");
                storeRejectedInviteNotification(pinger_i.getPhone());
            }
        }
    }
    private void handleUnfriendNotification(String phone) {
        Log.d(TAG, "Handling Unfriend request for" + phone );
        UsersAdapter cUserAdapter = new UsersAdapter (this);
        cUserAdapter.updatePingerStatus(phone, "RUF");
        cUserAdapter.UpdateInvitedPinger(phone, "RUF");
    }
    private void handleInviteResponseLoc(Pinger invResponseLoc, String invRespFlagLoc) {
        Log.d(TAG, "Invite Response is" + invRespFlagLoc );
        if (invRespFlagLoc.equals("IA")){
            storeAcceptedInviteNotificationLoc(invResponseLoc.getPhone(),invResponseLoc.getName(),invResponseLoc.getRegistrationToken());
        } else {
            Log.d(TAG, "Unknown Response" );
        }
    }

    private void handleInviteResponse(Pinger invResponse, String invRespFlag) {
        Log.d(TAG, "Invite Response is" + invRespFlag );
        if (invRespFlag.equals("IA")){
            storeAcceptedInviteNotification(invResponse.getPhone(),invResponse.getName(),invResponse.getRegistrationToken());
        } else if (invRespFlag.equals("ID")){
            storeRejectedInviteNotification(invResponse.getPhone());
        } else {
            Log.d(TAG, "Unknown Response" );
        }
    }

    private void storeAcceptedInviteNotificationLoc(String phone, String Name, String RegId) {
        Log.d(TAG, "Invitation/ Acceptance received from " + phone );
        UsersAdapter cUserAdapter = new UsersAdapter (this);
        cUserAdapter.updatePingerStatus(phone, "IA");
        cUserAdapter.UpdateInvitedPinger(phone, "IA");
        sendOldLoc(this,phone, Name, RegId);
    }
    private void storeAcceptedInviteNotification(String phone, String Name, String RegId) {
        //IA: Invite Acceptedz
        Log.d(TAG, "Invitation Acceptance received from " + phone );
        UsersAdapter cUserAdapter = new UsersAdapter (this);
        cUserAdapter.updatePingerStatus(phone, "IA");
        cUserAdapter.UpdateInvitedPinger(phone, "IA");
        sendOldLoc(this,phone, Name, RegId);
    }

    private void sendOldLoc(Context context, String phone, String Name, String RegId) {
        Log.d(TAG, "About to send current location to "+ phone);
        String rowId =null;
        String dateSelected = null,uts = null, strAdrP = null, strAdrFull = null;
        double lat =0.0, lng = 0.0;
        LocationDBHelper locDb = new LocationDBHelper(context);
        Cursor locationCursor = locDb.getMaxRow();
        if (locationCursor != null) {
            if (locationCursor.moveToFirst()) {
                do {
                    rowId = (locationCursor.getString(locationCursor.getColumnIndex("rowId")));
                    // getlastMile = (locationCursor.getString(locationCursor.getColumnIndex("lastMile")));
                } while (locationCursor.moveToNext());
            }
            Cursor rowCursor = locDb.getRowLocations(rowId);
            if (rowCursor != null) {
                if (rowCursor.moveToFirst()) {
                    //rowId = (rowCursor.getLong(rowCursor.getColumnIndex("rowId")));
                    dateSelected = (rowCursor.getString(rowCursor.getColumnIndex("todayDate")));
                    uts =  (rowCursor.getString(rowCursor.getColumnIndex("fst")));
                    strAdrP = rowCursor.getString(rowCursor.getColumnIndex("placeName"));
                    strAdrFull = rowCursor.getString(rowCursor.getColumnIndex("address"));
                    lat = Double.parseDouble(rowCursor.getString(rowCursor.getColumnIndex("lat")));
                    lng = Double.parseDouble(rowCursor.getString(rowCursor.getColumnIndex("lng")));
                }
            }
            Log.d(TAG, "rowId is" + rowId);
        } else {
            Log.d(TAG, "No location available to share yet");
        }

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String contactName = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);
        String MobilePhone = sharedPreferences.getString(QuickstartPreferences.PROFILE_PHONE, null);

        sendNotificationNewLoc sendLoc = new sendNotificationNewLoc(context);
        sendLoc.handleNewlocPhone(context, contactName, MobilePhone, dateSelected, uts, strAdrP, strAdrFull, lat, lng, phone, Name, RegId);
    }

    private void storeRejectedInviteNotification(String phone) {
        //ID: Invite Declined
        Log.d(TAG, "Invitation Decline received from " + phone );
        UsersAdapter cUserAdapter = new UsersAdapter (this);
        cUserAdapter.updatePingerStatus(phone, "ID");
        cUserAdapter.UpdateInvitedPinger(phone, "ID");
    }
    private ArrayList<Pinger> getPingers(Bundle data) throws JSONException {
        final JSONArray clients = new JSONArray(data.getString("clients"));
        ArrayList<Pinger> pingers = new ArrayList<>(clients.length());
        for (int i = 0; i < clients.length(); i++) {
            JSONObject jsonPinger = clients.getJSONObject(i);
            pingers.add(Pinger.fromJson(jsonPinger));
        }
        return pingers;
    }
    private Pinger getNewPinger(Bundle data) throws JSONException {
        final JSONObject client = new JSONObject(data.getString("client"));
        return Pinger.fromJson(client);
    }
    private PingerLocation getNewPingerLocation(Bundle data) throws JSONException {
        final JSONObject client = new JSONObject(data.getString("new_loc"));
        return PingerLocation.fromJson(client);
    }
    private Ping handleInviteNotification(Bundle data) throws JSONException {
        final Bundle notificationData = data.getBundle(Globals.NOTIFICATION);
        return new Ping(notificationData.getString(Globals.NOTIFICATION_BODY),
                data.getString(Globals.SENDER));
    }
    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }
    @Override
    public void onMessageSent(String msgId) {
        sendNotification("Upstream message sent. Id=" + msgId);
    }

    // Put the message into a notification and post it.
    // This is just one simple ack of what you might choose to do with.
    // a GCM message.
    private void sendNotification(String msg) {
        //logger.log(Log.INFO, msg);
        Log.d(TAG, "Received Msg  from GCM CSS ::::::" + msg);
    }
    private void sendNotification(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        int icon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ? R.drawable.icon_cal : R.drawable.icon_cal;
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(remoteMessage.getFrom())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}