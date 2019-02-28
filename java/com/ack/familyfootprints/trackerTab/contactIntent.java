package com.ack.familyfootprints.trackerTab;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ack.familyfootprints.DBHelper.InviteDBHelper;
import com.ack.familyfootprints.DBHelper.UsersKnownDBHelper;
import com.ack.familyfootprints.GCMClientApp.Globals;
import com.ack.familyfootprints.GCMClientApp.QuickstartPreferences;
import com.ack.familyfootprints.MapsActivity;
import com.ack.familyfootprints.R;
import com.ack.familyfootprints.Users.UsersAdapter;
import com.ack.familyfootprints.model.Pinger;
import com.ack.familyfootprints.model.PingerKeys;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ack.familyfootprints.trackerTab.trackContacts.getRoundedBitmap;

/**
 * Created by Lucky Goyal on 5/25/2016.
 */
public class contactIntent extends AppCompatActivity {

    AtomicInteger msgId = new AtomicInteger();
    public static final String TAG = contactIntent.class.getSimpleName();
    public static boolean canBack = true;
    private ListAdapter mUserAdapter;
    private SharedPreferences mDefaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_contact_ping);
        //Intent intent = getIntent();
        setToolBar();
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getConList();
        // getConPList();
    }

    private void setToolBar() {
        Toolbar actionBarToolBar = (Toolbar) findViewById(R.id.ToolBarView);
        setSupportActionBar(actionBarToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
        actionBarToolBar.setLogoDescription("Find and Invite");
        getSupportActionBar().setTitle("Find and Invite");
        //
        // getSupportActionBar().setSubtitle("Invite the contacts you want to track");
        getSupportActionBar().setHomeButtonEnabled(canBack);
    }

    public void getConList() {

        UsersKnownDBHelper usersKnownDb = new UsersKnownDBHelper(this);
        Cursor ContactCursor = usersKnownDb.getAllPendingContacts();
        int ConRow = ContactCursor.getCount();
        String name = null;
        String phone = null;
        String regID = null;
        String contactStatus = null;
        String contactMyphoneName = null;
        if (ConRow > 0) {
            if (ContactCursor.moveToFirst()) {
                do {
                    name = ContactCursor.getString(ContactCursor.getColumnIndex("conPhoneName"));
                    phone = ContactCursor.getString(ContactCursor.getColumnIndex("phone"));
                    regID = ContactCursor.getString(ContactCursor.getColumnIndex("regID"));
                    contactStatus = ContactCursor.getString(ContactCursor.getColumnIndex("contactStatus"));
                    Log.i(TAG, "name is " + name);
                    Log.i(TAG, "phone is " + phone);
                    Log.i(TAG, "regID is " + regID);
                    Log.i(TAG, "invStatus is " + contactStatus);
                } while (ContactCursor.moveToNext());
            }
            Log.i(TAG, "No More Contacts Now");
        } else {
            Toast.makeText(
                    this, "No more of your contacts use FootPrints.",
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No more of your contacts use FootPrints.");
            //Intent service = new Intent(this, RegistrationIntentService.class);
            //this.startService(service);
        }
                /*mUserAdapter = new SimpleCursorAdapter(this,
                R.layout.list_view_contact,
                //android.R.layout.simple_list_item_1,
                ContactCursor,
                new String[]{"conPhoneName","contactStatus", "phone"},
                new int[]{R.id.contact_name,R.id.invite_status, R.id.contact_phone}, 0);
*/
        final ListView mListView = (ListView) findViewById(R.id.list_view);
        mUserAdapter = new MyCustomInvAdapter(getBaseContext(), ContactCursor, 0);

        mListView.setAdapter(mUserAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Log.i(TAG, "Selected position" + position);
                Log.i(TAG, "Selected Row Data for " + arg3);
                UsersKnownDBHelper usersKnownDb = new UsersKnownDBHelper(getBaseContext());
                Cursor ContactCursor = usersKnownDb.getRowContacts(arg3);
                String invitedName = null;
                String invitedPhone = null;
                String regId = null;
                if (ContactCursor != null) {
                    if (ContactCursor.moveToFirst()) {
                        invitedName = ContactCursor.getString(ContactCursor.getColumnIndex("conPhoneName"));
                        invitedPhone = ContactCursor.getString(ContactCursor.getColumnIndex("phone"));
                        regId = ContactCursor.getString(ContactCursor.getColumnIndex("regID"));
                        Log.i(TAG, "Invited Name is " + invitedName);
                        Log.i(TAG, "Invited Phone is " + invitedPhone);
                        Log.i(TAG, "Invited regID is " + regId);
                    }
                }
                if (invitedcontactExists(getBaseContext(), invitedPhone)) {
                    showFriendConfirmdialog(invitedName, invitedPhone, regId);
                    /*inviteContact(invitedName, invitedPhone, regId);
                    try {
                        insertInviteContact(invitedName, invitedPhone, regId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                } else {
                    Log.e(TAG, "Old Contact. You have already invited" + invitedName);
                    Toast.makeText(
                            getBaseContext(), "You have already invited" + invitedName,
                            Toast.LENGTH_SHORT).show();
                }
                /*Intent gtintent = new Intent(getBaseContext(), SendInvite.class);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("regId", regId);
                startActivity(intent);*/
            }
        });
    }

    private void showFriendConfirmdialog(final String invitedName, final String invitedPhone, final String regId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to Invite " + invitedName +
                "? By choosing Yes, you will be able to view each other's Timeline, " +
                "once " + invitedName + " accepts your invite.");
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
    }

    public boolean mapPhoneName(Context context, String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                Log.d(TAG, "Contact present in User's contact dir.");
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        Log.d(TAG, "Contact NOT present in User's contact dir.");
        return false;
    }

    private boolean invitedcontactExists(Context context, String phone) {
        InviteDBHelper inviteKnownDb = new InviteDBHelper(context);
        Cursor cur = inviteKnownDb.getInvitedPhoneContact(phone);
        Log.d(TAG, "Inside invite contactExists");

        try {
            if (cur.moveToFirst()) {
                Log.d(TAG, "Known Pinger  found. Dont invite again");
                return false;
            } else {
                Log.d(TAG, "Known Pinger  not found.. invite");
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
    }

    private void insertInviteContact(String invitedName, String invitedPhone, String regId) throws JSONException {
        Pinger pendInvite = Pinger.fromUser(invitedName, invitedPhone, null, regId, null);
        Log.e(TAG, "Name: " + pendInvite.getName());
        Log.e(TAG, "Phone: " + pendInvite.getPhone());
        Log.e(TAG, "RegId: " + pendInvite.getRegistrationToken());
        //Pinger newInvite = getNewPinger(data);
        UsersAdapter cUserAdapter = new UsersAdapter(this);
        //IS: Invitation Sent
        cUserAdapter.addInvitedPinger(pendInvite, "IS");
        cUserAdapter.updatePingerStatus(pendInvite.getPhone(), "IS");
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        contactIntent.this.finish();
    }

    private String getTodayTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String uts = dateFormat.format(new Date()); // Find todays date
        return uts;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_demo:
                onAppInviteClicked();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inviteContact(String invitedName, String invitedPhone, String regId) {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.w(TAG, "Sending Footprints Invite to " + invitedName + " from: " + token);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String profileName = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);
        String profilePhone = sharedPreferences.getString(QuickstartPreferences.PROFILE_PHONE, null);

        FirebaseMessaging.getInstance().send(
                new RemoteMessage.Builder(Globals.GCM_SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId(Integer.toString(msgId.incrementAndGet()))
                        .addData(PingerKeys.ACTION, Globals.INVITE_REQUEST)
                        .addData(PingerKeys.SENDERREGID, FirebaseInstanceId.getInstance().getToken())
                        .addData(PingerKeys.SENDERNAME, profileName)
                        .addData(PingerKeys.SENDERPHONE, profilePhone)
                        .addData(PingerKeys.INVITEDREGID, regId)
                        .addData(PingerKeys.INVITEDNAME, invitedName)
                        .addData(PingerKeys.INVITEDPHONE, invitedPhone)
                        .build());

        Toast.makeText(
                this, "Footprints Invitation sent to." + invitedName,
                Toast.LENGTH_SHORT).show();
        //AnalyticsHelper.send(context, TrackingEvent.PING_SENT);
    }

    private void onAppInviteClicked() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String profileName = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);
        String profilePhone = sharedPreferences.getString(QuickstartPreferences.PROFILE_PHONE, null);
        String invitation_message = "Please accept" + profileName + "'s invitation on Footprints";
        Intent intent = new AppInviteInvitation.IntentBuilder("Footprints Invite")
                .setMessage(invitation_message)
                //.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText("TEXT")
                .build();
        // startActivityForResult(intent, REQUEST_INVITE);
    }

    public class MyCustomInvAdapter extends CursorAdapter {

        private ArrayList mData = new ArrayList();
        private LayoutInflater mInflater;

        public MyCustomInvAdapter(Context context, Cursor cursor, int flags) {

            //super();
            //super();
            super(context, cursor, 0);
            mInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Bitmap my_btmp = null;
            InputStream photo_stream;
            Bitmap round_btmp = null;

            TextView contactName = (TextView) view.findViewById(R.id.contact_name);
            String contact_name = cursor.getString(cursor.getColumnIndex("conPhoneName"));
            contactName.setText(contact_name);

            TextView contactStatus = (TextView) view.findViewById(R.id.invite_status);
            String contact_status = cursor.getString(cursor.getColumnIndex("contactStatus"));
            contactStatus.setText(contact_status);

            TextView contactPhone= (TextView) view.findViewById(R.id.contact_phone);
            String contact_phone = cursor.getString(cursor.getColumnIndex("phone"));
            contactPhone.setText(contact_phone);

            long Contact_Id = getContactIDFromNumber(contact_phone, context);
            ImageView profile = (ImageView) view.findViewById(R.id.imageView1);
            ContentResolver cr = context.getContentResolver();
            Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(Contact_Id));
            try {
                //InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), my_contact_Uri);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(
                            cr, my_contact_Uri, true);
                } else {
                    photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(
                            cr, my_contact_Uri);
                }
                if (photo_stream != null) {
                    BufferedInputStream buf = new BufferedInputStream(photo_stream);
                    my_btmp = BitmapFactory.decodeStream(buf);
                    round_btmp = getRoundedBitmap(my_btmp);
                    profile.setImageBitmap(round_btmp);
                    assert photo_stream != null;
                    photo_stream.close();
                } else {
                    Log.i(TAG, "Cannot Resolve Contact's image");
                    Drawable myDrawable = getResources().getDrawable(R.drawable.ic_contact_picture_holo_light);
                    profile.setImageDrawable(myDrawable);
                    //profile.setImageDrawable(ActivityCompat.getDrawable(this, R.drawable.generatedID));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = null;
            v = mInflater.inflate(R.layout.list_view_contact, parent, false);
            return v;
        }
    }

    public static class ViewHolder {
        public TextView textView;
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
}

