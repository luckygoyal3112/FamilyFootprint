package com.ack.familyfootprints.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lucky Goyal on 1/8/2016.
 */
public class UsersKnownDBHelper extends SQLiteOpenHelper {

    private static String DBNAME = "mtravellersqlite_group_3";

    /** Version number of the database */
    private static int VERSION = 5;



    /** Field 1 of the table Groups, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";

    /** Field 2 of the table Groups, stores the  Name*/
    public static final String FIELD_NAME = "name";

    /** Field 2 of the table Groups, stores the  Name*/
    public static final String FIELD_LNAME = "lname";

    /** Field 3 of the table Groups, stores the Phone*/
    public static final String FIELD_PHONE = "phone";

    /** Field 4 of the table Groups, stores the Conact Status Type*/
    public static final String FIELD_CONTACT_STATUS = "contactStatus";

    /** Field 5 of the table Groups, stores the Refgistration Id*/
    public static final String FIELD_REGID = "regID";

    /** Field 6 of the table Groups, stores the time on which group was added*/
    public static final String FIELD_CTS = "cts";

    /** Field 6 of the table Groups, stores the time on which group was updated*/
    public static final String FIELD_UTS = "uts";

    /** Field 7 of the table locations, stores if the group is marked as Fav.*/
    public static final String FIELD_FAV = "fav";

    /** Field 8 of the table locations, stores if the group is marked as Fav.*/
    public static final String FIELD_INVITE_STATUS = "invStatus";

    /** Field 9 of the table locations, stores the contactPhone Name*/
    public static final String FIELD_CONTACT_PHONENAME = "conPhoneName";

    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE = "contacts_all";

    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;

    /** Constructor
     * @param context*/

    public UsersKnownDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }    /** This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called


     * provided the database does not exists
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =     "create table " + DATABASE_TABLE + " ( " +
                FIELD_ROW_ID + " integer primary key autoincrement , " +
                FIELD_NAME + " text ," +
                FIELD_LNAME + " text ," +
                FIELD_PHONE + " text ," +
                FIELD_CONTACT_STATUS + " text ," +
                FIELD_REGID + " text ," +
                FIELD_FAV + " text ," +
                FIELD_INVITE_STATUS + " text ," +
                FIELD_CTS + " time ," +
                FIELD_UTS + " time ," +
                FIELD_CONTACT_PHONENAME + " text "+
                " ) ";

        db.execSQL(sql);
    }

    /** Inserts a new Contacts to the table All Comtacts */
    public long insertContactsAll(String name, String lname, String phone, String regId,  String cts, String uts, String contactPhoneName) {

        // Creating an instance of ContentValues
        ContentValues contentValues = new ContentValues();

        // Setting Name in ContentValues
        contentValues.put(FIELD_NAME, name);

        // Setting Name in ContentValues
        contentValues.put(FIELD_LNAME, lname);

        // Setting Phone in ContentValues
        contentValues.put(FIELD_PHONE, phone);

        // Setting Status in ContentValues
        contentValues.put(FIELD_CONTACT_STATUS, "Invite");

        // Setting RegId in ContentValues
        contentValues.put(FIELD_REGID, regId);

        // Setting RegId in ContentValues
        contentValues.put(FIELD_FAV, 0);

        // Setting RegId in ContentValues
        contentValues.put(FIELD_INVITE_STATUS, "N");

        // Setting date in ContentValues
        contentValues.put(FIELD_CTS, cts);

        // Setting date in ContentValues
        contentValues.put(FIELD_UTS, uts);

        //Setting date in ContentValues
        contentValues.put(FIELD_CONTACT_PHONENAME, contactPhoneName);


        // Creating an instance of LocationInsertTask
        long rowID = mDB.insert(DATABASE_TABLE, null, contentValues);
        //mDB.close();
        return rowID;
    }

    /** Deletes all contacts from the table */
    public int del(){
        int cnt = mDB.delete(DATABASE_TABLE, null, null);
        //mDB.close();
        return cnt;
    }

    /** Returns all the locations from the table */
    public Cursor getAllContacts(){
        Cursor C = mDB.query(DATABASE_TABLE, new String[]{FIELD_ROW_ID, FIELD_NAME, FIELD_LNAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                FIELD_INVITE_STATUS, FIELD_CTS, FIELD_UTS, FIELD_CONTACT_PHONENAME}, null, null, null, null, null, null);
        //mDB.close();

        return C;
    }
    /** Returns all the locations from the table */
    public Cursor getAllPendingContacts(){
        Cursor C = mDB.query(DATABASE_TABLE, new String[]{FIELD_ROW_ID, FIELD_NAME, FIELD_LNAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                FIELD_INVITE_STATUS, FIELD_CTS, FIELD_UTS, FIELD_CONTACT_PHONENAME},
                "invStatus IN ('N','IS','SUF','RUF')", null , null, null, FIELD_CONTACT_STATUS , null)
                ;
        //mDB.close();
        return C;
    }
    
    public Cursor getRowContacts(long rowId) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID, FIELD_NAME, FIELD_LNAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                        FIELD_INVITE_STATUS,FIELD_CTS, FIELD_UTS, FIELD_CONTACT_PHONENAME}
                , "rowId = ?", new String[]{String.valueOf(rowId)}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getPhoneContact(String phone) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID, FIELD_NAME, FIELD_LNAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                        FIELD_INVITE_STATUS,FIELD_CTS, FIELD_UTS, FIELD_CONTACT_PHONENAME}
                , "phone = ?", new String[]{String.valueOf(phone)}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getMaxRow(){
        Cursor C= mDB.query(DATABASE_TABLE, new String[]
                { "max(gid) as rowId" }
                , null, null, null, null, null);
        //mDB.close();
        return C;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older StringDBs table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

        // create fresh StringDBs table
        this.onCreate(db);
    }
    public int updateUserStatus(String rowId,String uts, String invstatus) {
        // Drop older StringDBs table if existed
        String invStatusDesc = null;
        if (invstatus == "IS") {
            invStatusDesc = "Invitation Sent";
        }else if (invstatus == "IR") {
            invStatusDesc = "Invitation Received";
        } else if (invstatus == "IA") {
            invStatusDesc = "Invitation Accepted";
        } else if (invstatus == "IS") {
            invStatusDesc = "Invitation Declined.";
        }else if (invstatus == "SUF") {
            invStatusDesc = "You Unfriended ";
        }else if (invstatus == "RUF") {
            invStatusDesc = "You were Unfriended ";
        }
        ContentValues contentValues = new ContentValues();

        // Setting Inv Status in ContentValues
        contentValues.put(FIELD_INVITE_STATUS, invstatus);

        contentValues.put(FIELD_CONTACT_STATUS, invStatusDesc);

        // Setting date in ContentValues
        contentValues.put(FIELD_UTS, uts);

        // updating row
        return mDB.update(DATABASE_TABLE, contentValues, FIELD_ROW_ID + " = ?",
                new String[] { String.valueOf(rowId) });
    }


    public Cursor getTobeInvitedContacts() {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_INVITE_STATUS}
                , "inviteStatus = ?", new String[]{"P"}, null, null, null, null);
        //mDB.close();
        return C;
    }

    public String getTodayDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        String todayDateTime = dateFormat.format(new Date()); // Find todays date
        return todayDateTime;
    }
}
