package com.ack.familyfootprints.notificationTab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ack.familyfootprints.DBHelper.InviteDBHelper;
import com.ack.familyfootprints.DBHelper.LocationDBHelper;
import com.ack.familyfootprints.GCMClientApp.Globals;
import com.ack.familyfootprints.GCMClientApp.QuickstartPreferences;
import com.ack.familyfootprints.MapsActivity;
import com.ack.familyfootprints.R;
import com.ack.familyfootprints.model.PingerKeys;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Lucky Goyal on 7/4/2016.
 */
public class NotificationIntent extends AppCompatActivity {
    AtomicInteger msgId = new AtomicInteger();
    private Object image;

    private static final String TAG = "NotificationIntent";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bitmap my_btmp = null;
        InputStream photo_stream;

        Bitmap round_btmp = null;
        final String name = intent.getStringExtra("name");
        final String phone = intent.getStringExtra("phone");
        final String regId = intent.getStringExtra
                ("regId");
        final String rowId = intent.getStringExtra("rowId");

        Log.i(TAG, "Phone number is" + phone);
        long Contact_Id = getContactIDFromNumber(phone,this);
        Log.i(TAG, "contact id  is" + Contact_Id);
        setContentView(R.layout.activity_notification_intent);

        ImageView profile  = (ImageView)findViewById(R.id.user_profile_photo);


        Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(Contact_Id));
        try {

            //InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), my_contact_Uri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                 photo_stream =  ContactsContract.Contacts.openContactPhotoInputStream(
                        getContentResolver(), my_contact_Uri, true);
            } else {
                 photo_stream =  ContactsContract.Contacts.openContactPhotoInputStream(
                         getContentResolver(), my_contact_Uri );
            }
            if (photo_stream != null) {
                BufferedInputStream buf = new BufferedInputStream(photo_stream);
                my_btmp = BitmapFactory.decodeStream(buf);
                round_btmp = getRoundedBitmap(my_btmp);
                profile.setImageBitmap(round_btmp);
                assert photo_stream != null;
                photo_stream.close();
            } else  {
                Log.i(TAG, "Cannot Resolve Contact's image");
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_contact_picture_holo_light);
                profile.setImageDrawable(myDrawable);
                //profile.setImageDrawable(ActivityCompat.getDrawable(this, R.drawable.generatedID));
            }
            } catch (IOException e) {
             e.printStackTrace();
        }

        TextView invite_name = (TextView) findViewById(R.id.user_profile_name);
        invite_name.setText(name);

        //TextView invite_phone = (TextView) findViewById(R.id.Mobile);
        //invite_phone.setText("Mobile" + "\\n" + phone);

        //setToolBar();
        Button accept = (Button) findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptinvite(rowId, name, phone, regId);
            }
        });
        Button reject = (Button) findViewById(R.id.reject);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectinvite(rowId, name, phone, regId);
            }
        });
    }

    private void rejectinvite(String rowId, String name, String phone, String regId) {
        //Reject invite & update user known table with ID as invStatus
        String uts = getTodayTime();
        InviteDBHelper inviteDb = new InviteDBHelper(this);
        inviteDb.updateInviteResp(rowId, uts, "ID");
        sendInviteRespNotification(name, phone, regId, "ID");
        Log.i(TAG, "Rejected row" + rowId);

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    private void acceptinvite(String rowId, String name, String phone, String regId) {
        //Accept invite & update user known table with IA as invStatus

        String uts = getTodayTime();
        InviteDBHelper inviteDb = new InviteDBHelper(this);
        inviteDb.updateInviteResp(rowId, uts, "IA");
       /* sendInviteRespNotification(name, phone, regId, "IA");
        Log.i(TAG, "Accepted row" + rowId);
        sendOldLoc(this,phone, name, regId);*/
        showFriendConfirmdialog(name, phone, regId, "IA");
    }

    private void sendInviteRespLocNotification(Context context, String invName, String invPhone, String regId, String ia) {

        Log.d(TAG, "About to send current location to "+ invPhone);
        String rowId =null;
        String dateSelected = null,uts = null, strAdrP = null, strAdrFull = null;
        double lat =0.0, lng = 0.0;
        LocationDBHelper locDb = new LocationDBHelper(context);
        Cursor locationCursor = locDb.getMaxRow();
        if (locationCursor != null) {
            if (locationCursor.moveToFirst()){
                try {do {
                    rowId = (locationCursor.getString(locationCursor.getColumnIndex("rowId")));
                    // getlastMile = (locationCursor.getString(locationCursor.getColumnIndex("lastMile")));
                } while (locationCursor.moveToNext());}
                finally {
                    if (locationCursor != null && !locationCursor.isClosed())
                     locationCursor.close();
                }
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

        FirebaseMessaging.getInstance().send(
                new RemoteMessage.Builder(Globals.GCM_SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId(Integer.toString(msgId.incrementAndGet()))
                        .addData(PingerKeys.ACTION, Globals.INVITE_REQUEST_RESP_LOC)
                        .addData(PingerKeys.INVRESPONSE, ia)
                        .addData(PingerKeys.SENDERREGID, FirebaseInstanceId.getInstance().getToken())
                        .addData(PingerKeys.SENDERNAME, contactName)
                        .addData(PingerKeys.SENDERPHONE, MobilePhone)
                        .addData(PingerKeys.RECEIVERREGID, regId)
                        .addData(PingerKeys.RECEIVERNAME, invName)
                        .addData(PingerKeys.RECEIVERPHONE, invPhone)
                        .addData(PingerKeys.DATESELECTED, dateSelected)
                        .addData(PingerKeys.UTS, uts)
                        .addData(PingerKeys.PLACENAME, strAdrP)
                        .addData(PingerKeys.PLACEADR, strAdrFull)
                        .addData(Globals.REGISTRATION_TOKEN, FirebaseInstanceId.getInstance().getToken())
                        .addData(PingerKeys.NAME, contactName)
                        .addData(PingerKeys.PHONE, MobilePhone)
                        .addData(PingerKeys.DATESELECTED, dateSelected)
                        .addData(PingerKeys.UTS, uts)
                        .addData(PingerKeys.PLACENAME, strAdrP)
                        .addData(PingerKeys.PLACEADR, strAdrFull)
                        .addData(PingerKeys.LAT, String.valueOf(lat))
                        .addData(PingerKeys.LNG, String.valueOf(lng))
                        .addData(PingerKeys.TO, regId)
                        .addData(PingerKeys.TOPHONE, invName)
                        .addData(PingerKeys.TONAME, invPhone)
                        .addData(PingerKeys.CONNECTION_STATUS, ia)
                        .addData(PingerKeys.OUTS, "OTNF")
                        .addData(PingerKeys.OROWID, "NA")
                        .addData(PingerKeys.ODATE, "NA")
                        .addData(PingerKeys.OEST, "NA")
                        .addData(PingerKeys.OTRANSIT, "NA" )
                        .addData(PingerKeys.OTRANSITTIME, "NA")
                        .build());

    }
    private String getTodayTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String uts = dateFormat.format(new Date()); // Find todays date
        return uts;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded(Lat, Lng);
    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0
                , bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    public static long getContactIDFromNumber(String contactNumber, Context context) {
        String UriContactNumber = Uri.encode(contactNumber);
        long phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, UriContactNumber),
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getLong(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();

        return phoneContactID;
    }
    private void sendInviteRespNotification(String invitedName, String invitedPhone, String regId, String status)
    {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.w(TAG, "Sending Milestone Invite Response and frst time location to " + invitedName + " from: " + token);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String profileName = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);
        String profilePhone = sharedPreferences.getString(QuickstartPreferences.PROFILE_PHONE, null);

        FirebaseMessaging.getInstance().send(
                new RemoteMessage.Builder(Globals.GCM_SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId(Integer.toString(msgId.incrementAndGet()))
                        .addData(PingerKeys.ACTION, Globals.INVITE_REQUEST_RESP_LOC)
                        .addData(PingerKeys.INVRESPONSE, status)
                        .addData(PingerKeys.SENDERREGID, FirebaseInstanceId.getInstance().getToken())
                        .addData(PingerKeys.SENDERNAME, profileName)
                        .addData(PingerKeys.SENDERPHONE, profilePhone)
                        .addData(PingerKeys.RECEIVERREGID, regId)
                        .addData(PingerKeys.RECEIVERNAME, invitedName)
                        .addData(PingerKeys.RECEIVERPHONE, invitedPhone)
                        .build());


        Toast.makeText(
                this, "Timeline Invitation Response sent to." + invitedName,
                Toast.LENGTH_SHORT).show();
        //AnalyticsHelper.send(context, TrackingEvent.PING_SENT);
    }
    private void showFriendConfirmdialog(final String invitedName, final String invitedPhone, final String regId, final String status ) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to accept the Invite from" + invitedName +
                "? By choosing Yes, you will be able to track each other's Timeline,");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                sendInviteRespLocNotification(getBaseContext(),invitedName, invitedPhone, regId, "IA");
                Intent intent = new Intent(NotificationIntent.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", null).show();
    }

}