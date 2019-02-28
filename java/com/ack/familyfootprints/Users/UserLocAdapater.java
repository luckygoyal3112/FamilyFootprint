package com.ack.familyfootprints.Users;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;

import  com.ack.familyfootprints.DBHelper.InviteDBHelper;
import  com.ack.familyfootprints.DBHelper.TrackerDBHelper;
import  com.ack.familyfootprints.DBHelper.UsersKnownDBHelper;
import  com.ack.familyfootprints.model.Pinger;
import  com.ack.familyfootprints.model.PingerLocation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Lucky Goyal on 7/20/2016.
 */
public class UserLocAdapater {
    private static final String TAG = "UsersLocAdapter";
    //private final ArrayList<Pinger> mPingers;
    private final ArrayList<Pinger> mUsers;
    private final LayoutInflater mLayoutInflater;
    private final Context context;

    public UserLocAdapater(Context context) {
        this(context, new ArrayList<Pinger>());
    }

    public UserLocAdapater(Context context, ArrayList<Pinger> users) {
        mUsers = users;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    /**
     * Add a pinger to the list.
     */
    public void addLocation(PingerLocation userlocation) {
        long rowId_Old = 0;
        String uts = getTodayTime();
        TrackerDBHelper locDb = new TrackerDBHelper(context);
        Log.d(TAG, "Name: " + userlocation.getName());
        Log.d(TAG, "Phone: " + userlocation.getPhone());
        Log.d(TAG, "oDate: " + userlocation.getOldDate());
        Log.d(TAG, "oest: " + userlocation.getOldest());
        Log.d(TAG, "RegId: " + userlocation.getRegistrationToken());

        if (knowInviteExists(context, userlocation.getPhone())) {
            if (userlocation.getOldUts().equals("OTNF")) {
                Log.e(TAG, "Old Row TimeStamp not recieved");
            } else {
                Log.e(TAG, "Old Row TimeStamp recieved");
                Cursor locationCursor = locDb.getDateTimePhoneRow(userlocation.getOldDate(),userlocation.getOldest() );
                if (locationCursor != null) {
                    try {
                        if (locationCursor.moveToFirst()) {
                            do {
                                rowId_Old = (locationCursor.getLong(locationCursor.getColumnIndex("rowId")));
                            } while (locationCursor.moveToNext());
                        }
                    } finally {
                        if (locationCursor != null && !locationCursor.isClosed())
                            locationCursor.close();
                    }

                    //long rowId_Old = lo
                    // cDb.getMaxPhoneRow(userlocation.getPhone();
                    //String dateCorrected = getCorrectDate(userlocation.getUts());
                    String UtsOCorrected = getCorrectTime(userlocation.getOldUts());
                    int rc = locDb.updateLastLoc(rowId_Old, UtsOCorrected, "Y",
                            userlocation.getOldtransit(), userlocation.getOldransitTime());
                    Log.d(TAG, "Updating old row for p" +
                            "hone" + userlocation.getPhone());
                    Log.d(TAG, "with full end time" + userlocation.getOldUts());
                    Log.d(TAG, "with corrected end time" + UtsOCorrected);
                    Log.d(TAG, "with row Id" + rowId_Old);
                    Log.d(TAG, "with return code: " + rc);
                }
            }

            String dateCorrected = getCorrectDate(userlocation.getUts());
            String UtsCorrected = getCorrectTime(userlocation.getUts());

            Log.e(TAG, "Updating old row for phone"+ dateCorrected);
            Log.e(TAG, "Updating old row for phone"+ UtsCorrected);

            long rowId_i = locDb.insertTrackerLoc(userlocation.getName(), userlocation.getPhone(),
                    dateCorrected, UtsCorrected, userlocation.getPlaceName(),
                    userlocation.getAddress(), userlocation.getLat(), userlocation.getLng());

            Log.e(TAG, "Known Contact. Location added at rowId " + rowId_i);
        } else {
            Log.e(TAG, "UnKnown Contact");
        }
    }
    private String getCorrectDate(String dateOld) {
        String dateNew = null;
        String dateNewT = null;
        String dateCheck = null;

        try {


            Log.e(TAG, "Sender Date is " + dateOld );
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
            dateFormatGmt.setTimeZone(TimeZone.getDefault());
            // return Date in required format with timezone as String
          //  dateCheck =  dateFormatGmt.format(dateOld);
            dateFormatGmt.setTimeZone(TimeZone.getDefault());

//Local time zone
            SimpleDateFormat dateFormatLocal = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dateFormatLocalT = new SimpleDateFormat("HH:mm");

//Time in GMT
            dateNew = dateFormatLocal.format(dateFormatGmt.parse(dateOld));
            dateNewT = dateFormatLocalT.format(dateFormatGmt.parse(dateOld));

            Log.e(TAG, "Sender Full Date is " + dateOld );
            //Log.e(TAG, "ReceFull Date is " + dateCheck );
            Log.e(TAG, "Receiver Date is " + dateNew );
            Log.e(TAG, "Receiver Time is " + dateNewT );
        }catch (ParseException e) {
            e.printStackTrace();
        }

        TimeZone zone = TimeZone.getDefault();
        Log.e(TAG, "Timezone is "+ zone);

        return dateNew;
    }
    private String getCorrectTime(String dateOld) {
        String dateNew= null;

        try {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
            dateFormatGmt.setTimeZone(TimeZone.getDefault());

//Local time zone
            SimpleDateFormat dateFormatLocal = new SimpleDateFormat("HH:mm");

//Time in GMT
             dateNew = dateFormatLocal.format(dateFormatGmt.parse(dateOld));
            Log.e(TAG, "Sender Time is " + dateOld );
            Log.e(TAG, "Receiver Time is " + dateNew );
        }catch (ParseException e) {
            e.printStackTrace();
        }

        TimeZone zone = TimeZone.getDefault();
        Log.e(TAG, "Timezone is "+ zone);

        return dateNew;
    }

    private boolean knowInviteExists(Context context, String phone) {
        InviteDBHelper inviteDb = new InviteDBHelper(context);
        Cursor cur = inviteDb.getAcceptedPhoneContact(phone);
        Log.d(TAG, "Inside knowInviteExists");
        try {
            if (cur.moveToFirst()) {
                Log.d(TAG, "Known Invited Accepted Pinger  found. Go ahead add location.");
                return true;
            } else {
                Log.d(TAG, "Known Pinger  not found.. Dont add location");
                return false;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
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

}
