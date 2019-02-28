/*
 * Copyright Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ack.familyfootprints.Users;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;

import com.ack.familyfootprints.DBHelper.InviteDBHelper;
import com.ack.familyfootprints.DBHelper.UsersAllDBHelper;
import com.ack.familyfootprints.DBHelper.UsersKnownDBHelper;
import com.ack.familyfootprints.GCMClientApp.QuickstartPreferences;
import com.ack.familyfootprints.model.Pinger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Display available {@link Pinger}s.
 */
public class UsersAdapter  {

    private static final String TAG = "UsersAdapter";
    //private final ArrayList<Pinger> mPingers;
    private final ArrayList<Pinger> mUsers;
    private final LayoutInflater mLayoutInflater;
    private final Context context;

    public UsersAdapter(Context context) {
        this(context, new ArrayList<Pinger>());
    }

    public UsersAdapter(Context context, ArrayList<Pinger> users) {
        mUsers = users;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * Add a pinger to the list.
     */
    public void addPinger(Pinger pinger) {
        String uts = getTodayTime();
        String localname = null;
        UsersAllDBHelper usersAllDb = new UsersAllDBHelper(context);
        Log.e(TAG, "Name: " + pinger.getName());
        Log.e(TAG, "Phone: "+ pinger.getPhone());
        Log.e(TAG, "RegId: " + pinger.getRegistrationToken());
        if (knowcontactExists(context, pinger.getPhone())&& !selfPinger(pinger.getPhone(), context)) {
           // localname = retrieveContactName(pinger.getPhone(), context);

            long rowId_i = usersAllDb.insertContactsAll(pinger.getName(), null, pinger.getPhone(),
                    pinger.getRegistrationToken(), uts, uts);

            Log.e(TAG, "New Client with the following info added in contactsAll, Row Id:: " + rowId_i);
            Log.e(TAG, "Name: " + pinger.getName());
            Log.e(TAG, "Phone: " + pinger.getPhone());
            Log.e(TAG, "RegId: " + pinger.getRegistrationToken());
            if (contactExists(context, pinger.getPhone()) && knowcontactExists(context, pinger.getPhone())){
                UsersKnownDBHelper usersKnownDb = new UsersKnownDBHelper(context);
                long rowIdi = usersKnownDb.insertContactsAll(pinger.getName(), null, pinger.getPhone(),
                        pinger.getRegistrationToken(), uts, uts, getContactPhoneName(pinger.getPhone()));
                Log.e(TAG, "Known Contact added at rowId " + rowIdi);
            } else {
                Log.e(TAG, "UnKnown Contact");
            }
        } else {
            Log.e(TAG, "Old Contact. Already in Milestone contacts");
        }
    }

    public void addPinger(ArrayList<Pinger> pingers) {
        int listSize = pingers.size();
        for (int i = 0; i<listSize; i++) {
            Pinger pinger_i = pingers.get(i);
            String uts = getTodayTime();
            UsersAllDBHelper usersAllDb = new UsersAllDBHelper(context);

            if (knowcontactExists(context, pinger_i.getPhone()) && !selfPinger(pinger_i.getPhone(), context)) {
                long rowId = usersAllDb.insertContactsAll(pinger_i.getName(),null,  pinger_i.getPhone(),
                        pinger_i.getRegistrationToken(), uts, uts);
                Log.e(TAG, "New Client with the following info added in contactsAll, Row Id:: " + rowId);
                Log.e(TAG, "Name: " + pinger_i.getName());
                Log.e(TAG, "Phone: " + pinger_i.getPhone());
                Log.e(TAG, "RegId: " + pinger_i.getRegistrationToken());
                if (contactExists(context, pinger_i.getPhone()) && knowcontactExists(context, pinger_i.getPhone())){
                    UsersKnownDBHelper usersKnownDb = new UsersKnownDBHelper(context);
                    long rowIdi = usersKnownDb.insertContactsAll(pinger_i.getName(),null, pinger_i.getPhone(),
                            pinger_i.getRegistrationToken(), uts, uts, getContactPhoneName(pinger_i.getPhone()));
                    Log.e(TAG, "Known Contact added at rowId " + rowIdi);
                } else {
                    Log.e(TAG, "UnKnown Contact");
                }
            } else {
                Log.e(TAG, "Old Contact. Already in Milestone contacts");
            }
        }
    }

    public void updatePingerStatus(String pingerPhone, String status) {
        Log.i(TAG, "Updating pinger status " + pingerPhone + " " + status);
        String uts = getTodayTime();
        UsersKnownDBHelper usersKnownDb = new UsersKnownDBHelper(context);
        Cursor cur = usersKnownDb.getPhoneContact(pingerPhone);

        int locRow = cur.getCount();
        if (locRow > 0 ) {
            if (cur.moveToFirst()) {
                do {
                    String rowId = cur.getString(cur.getColumnIndex("_id"));
                    Log.i(TAG, "regID is " + rowId);
                    usersKnownDb.updateUserStatus(rowId, uts, status);
                }while (cur.moveToNext());
            } Log.i(TAG, "More Matching Invites");
        } else {
            /*Toast.makeText(
                    getActivity(), "You have No Pending Invites.",
                    Toast.LENGTH_LONG).show();*/
            Log.i(TAG, "Matching Pinger not Found");
        }
    }

    private boolean selfPinger(String phone, Context context) {

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String profileName = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);
        String profilePhone = sharedPreferences.getString(QuickstartPreferences.PROFILE_PHONE, null);

        Log.d(TAG, "profileName is " + profileName);
        Log.d(TAG, "profilePhone is " + profilePhone);

        if (profilePhone.equals(phone)) {
            Log.d(TAG, "This is record for self which is " + phone);
            return true;
        } else {
            Log.d(TAG, "Not self");
            return false;
        }


    }

    /**
     * Add a pinger to the list.
     */
    public void addInvitedPinger(Pinger pinger, String status ) {
        String uts = getTodayTime();

        if (invitedcontactExists(context, pinger.getPhone())) {
            //Invite Requested.
            InviteDBHelper inviteAllDb = new InviteDBHelper(context);
            long rowId = inviteAllDb.insertInvites(pinger.getName(), pinger.getPhone(),
                    pinger.getRegistrationToken(), uts, uts, status);
            Log.e(TAG, "Client with the following info added in Invited All, Row Id:: " + rowId);
            Log.e(TAG, "Name: " + pinger.getName());

            Log.e(TAG, "Phone: " + pinger.getPhone());
            Log.e(TAG, "RegId: " + pinger.getRegistrationToken());
        } else {
            UpdateInvitedPinger(pinger.getPhone(), status);
            Log.e(TAG, "Old Contact. You have already invited. Agaian invited client" + pinger.getName());
            Log.e(TAG, "Phone: " + pinger.getPhone());
            Log.e(TAG, "RegId: " + pinger.getRegistrationToken());
        }

    }
    public void UpdateInvitedPinger(String phone, String status ) {
        Log.i(TAG, "Updating Invite Response " + phone +" with status " + status);
        //This function is used at both sender and receiver end.

        String uts = getTodayTime();
        InviteDBHelper inviteAllDb = new InviteDBHelper(context);
        Cursor cur = inviteAllDb.getPhoneContact(phone);
        int locRow = cur.getCount();
        if (locRow > 0 ) {
             if (cur.moveToFirst()) {
                do {
                    String rowId = cur.getString(cur.getColumnIndex("_id"));
                    Log.i(TAG, "regID is " + rowId);
                    inviteAllDb.updateInviteResp(rowId, uts, status);
                }while (cur.moveToNext());
             } Log.i(TAG, "More Matching Invites");
        } else {
            /*Toast.makeText(
                    getActivity(), "You have No Pending Invites.",
                    Toast.LENGTH_LONG).show();*/
            Log.i(TAG, "No matching Invites");
        }

        //rowId = cur.getLong(cur.getColumnIndex("_id"));
    }

    private String getTodayTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String uts = dateFormat.format(new Date()); // Find todays date
        return uts;
    }
    /**
     * Return the pinger with matching registration token or null of no match is found.
     */
    private Pinger getPingerByToken(String token) {
        for (Pinger pinger: mUsers) {
            if (pinger.getRegistrationToken().equals(token)) {
                return pinger;
            }
        }
        Log.e(TAG, "Pinger not found.");
        return null;
    }
    public boolean contactExists(Context context, String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
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

    private boolean knowcontactExists(Context context, String phone) {
        UsersKnownDBHelper usersKnownDb = new UsersKnownDBHelper(context);
        Cursor cur = usersKnownDb.getPhoneContact(phone);
        Log.d(TAG, "Inside knowcontactExists");
        try {
            if (cur.moveToFirst()) {
                Log.d(TAG, "Known Pinger  found. Dont add again");
                return false;
            } else {
                Log.d(TAG, "Known Pinger  not found.. Add");
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
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
   /* private String retrieveContactName(String phone, Context context) {

        long Contact_Id = getContactIDFromNumber(phone,context);
        Log.i(TAG, "contact id  is" + Contact_Id);

        Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(Contact_Id));

        String contactName = null;

            // querying contact data store
        Cursor cursor = getContentResolver().query(my_contact_Uri, null, null, null, null);
        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();
        Log.d(TAG, "Contact Name: " + contactName);
        return contactName;

    }*/
    private boolean invitedcontactExists(Context context, String phone) {
        InviteDBHelper inviteKnownDb = new InviteDBHelper(context);
        Cursor cur = inviteKnownDb.getPhoneContact( phone);
        Log.d(TAG, "Inside knowcontactExists");
        try {
            if (cur.moveToFirst()) {
                Log.d(TAG, "Known Pinger  found. Dont add again");
                return false;
            } else {
                Log.d(TAG, "Known Pinger  not found.. Add");
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
    }

    /*
       @Override
       public int getCount() {
           return mUsers.size();
       }

       @Override
       public Pinger getItem(int position) {
           return mUsers.get(position);
       }

       @Override
       public long getItemId(int position) {
           return mUsers.get(position).hashCode();
       }

       @Override
       public View getView(int position, View convertView, ViewGroup parent) {
           return null;
       }
       */
    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = initializeConvertView(parent);
        }

        final Pinger pinger = getItem(position);

        Glide.with(convertView.getContext()).load(pinger.getPictureUrl()).
                into((ImageView) convertView.getTag(R.id.profile_picture));

        ((TextView) convertView.getTag(R.id.name)).setText(pinger.getName());

        return convertView;
    }

    private View initializeConvertView(ViewGroup parent) {
        View convertView;
        final View view = mLayoutInflater.inflate(R.layout.pinger_item_view, parent, false);
        convertView = view;
        convertView.setTag(R.id.name, view.findViewById(R.id.name));
        final View tmpProfilePicture = view.findViewById(R.id.profile_picture);
        // OutlineProviders are available from API 21 onwards.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tmpProfilePicture.setClipToOutline(true);
            tmpProfilePicture.setOutlineProvider(new PingerOutlineProvider());
        }
        convertView.setTag(R.id.profile_picture, tmpProfilePicture);
        return convertView;
    }
*/
    public ArrayList<Pinger> getItems() {
        return mUsers;
    }

     public String getContactPhoneName(final String phoneNumber)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }
        return contactName;
    }
}
