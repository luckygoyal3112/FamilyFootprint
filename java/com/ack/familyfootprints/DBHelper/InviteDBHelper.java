package com.ack.familyfootprints.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucky Goyal on 1/8/2016.
 */
public class InviteDBHelper extends SQLiteOpenHelper {

    private static String DBNAME = "mtravellersqlite_invite";

    /** Version number of the database */
    private static int VERSION = 2;

    /** Field 1 of the table Groups, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";

    /** Field 2 of the table Groups, stores the  Name*/
    public static final String FIELD_NAME = "name";

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

    /** Field 7 of the table locations, stores if the group is marked as Fav.*/
    public static final String FIELD_INVITE_STATUS = "invStatus";

    /** Field 7 of the table locations, stores if the group is marked as Fav.*/
    public static final String IMAGE = "image";

    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE = "contacts_all";

    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;

    /** Constructor
     * @param context*/

    public InviteDBHelper(Context context) {
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
                FIELD_PHONE + " text ," +
                FIELD_CONTACT_STATUS + " text ," +
                FIELD_REGID + " text ," +
                FIELD_FAV + " text ," +
                FIELD_INVITE_STATUS + " text ," +
                FIELD_CTS + " time ," +
                FIELD_UTS + " time " +
                " ) ";

        db.execSQL(sql);
    }

    /** Inserts a new Contacts to the table Invites  */
    public long insertInvites(String name, String phone, String regId,  String cts, String uts, String invStatus) {

        // Creating an instance of ContentValues
        ContentValues contentValues = new ContentValues();

        // Setting Name in ContentValues
        contentValues.put(FIELD_NAME, name);

        // Setting Phone in ContentValues
        contentValues.put(FIELD_PHONE, phone);

        // Setting Status in ContentValues
        contentValues.put(FIELD_CONTACT_STATUS, "Invited");

        // Setting RegId in ContentValues
        contentValues.put(FIELD_REGID, regId);

        // Setting RegId in ContentValues
        contentValues.put(FIELD_FAV, 0);

        // Setting RegId in ContentValues
        contentValues.put(FIELD_INVITE_STATUS, invStatus);

        // Setting date in ContentValues
        contentValues.put(FIELD_CTS, cts);

        // Setting date in ContentValues
        contentValues.put(FIELD_UTS, uts);

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
    public Cursor getAllInvites(){
        Cursor C = mDB.query(DATABASE_TABLE, new String[]{FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                FIELD_INVITE_STATUS, FIELD_CTS, FIELD_UTS}, null, null, null, null, null, null);
        //mDB.close();

        return C;
    }
    /** Returns all the Pending Invites from the table */
    public Cursor getAllPendingInvites(){
        Cursor C = mDB.query(DATABASE_TABLE, new String[]{FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                FIELD_INVITE_STATUS, FIELD_CTS, FIELD_UTS}
                , "invStatus = ?", new String[]{String.valueOf("IR")}, null, null, null, null);
        //mDB.close();

        return C;
    }
    /** Returns all the Accepted Invites from the table */
    public Cursor getAllAcceptedInvites(){
        Cursor C = mDB.query(DATABASE_TABLE, new String[]{FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                FIELD_INVITE_STATUS, FIELD_CTS, FIELD_UTS}
                , "invStatus = ?", new String[]{String.valueOf("IA")}, null, null, null, null);
        //mDB.close();

        return C;
    }
    public Cursor getRowInvites(long rowId) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                        FIELD_INVITE_STATUS,FIELD_CTS, FIELD_UTS}
                , "rowId = ?", new String[]{String.valueOf(rowId)}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getPhoneInvite(String phone) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                        FIELD_INVITE_STATUS,FIELD_CTS, FIELD_UTS}
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
        db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE);

        // create fresh StringDBs table
        this.onCreate(db);
    }

    public Cursor getTobeInvitedContacts() {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_INVITE_STATUS}
                , "inviteStatus = ?", new String[]{"P"}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getPhoneContact(String phone) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                        FIELD_INVITE_STATUS,FIELD_CTS, FIELD_UTS}
                , "phone = ?", new String[]{String.valueOf(phone)}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getAcceptedPhoneContact(String phone) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                        {FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                                FIELD_INVITE_STATUS,FIELD_CTS, FIELD_UTS}
                , "phone = ? and invStatus = ?", new String[]{String.valueOf(phone), String.valueOf("IA")}, null, null, null, null);
        //mDB.close();
        return C;


    }
    public Cursor getRowContacts(long rowId) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                        FIELD_INVITE_STATUS,FIELD_CTS, FIELD_UTS}
                , "rowId = ?", new String[]{String.valueOf(rowId)}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public int updateInviteResp(String rowId, String uts, String status) {
        // Drop older StringDBs table if existed
        ContentValues contentValues = new ContentValues();

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_INVITE_STATUS, status);
        contentValues.put(FIELD_UTS, uts);
        //contentValues.put(FIELD_INVITE_STATUS_DESC, status_desc);

        // updating row
        return mDB.update(DATABASE_TABLE, contentValues, FIELD_ROW_ID + " = ?",
                new String[]{String.valueOf(rowId)});
    }

    public Cursor getInvitedPhoneContact(String phone) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_FAV,
                        FIELD_INVITE_STATUS,FIELD_CTS, FIELD_UTS}
                , "phone = ? and invStatus = ?", new String[]{String.valueOf(phone), "IS"}, null, null, null, null);
        //mDB.close();

        return C;
    }
    public int updateImage(byte[] imageBytes, String phone){
        ContentValues cv = new ContentValues();
        cv.put(IMAGE, imageBytes);
        return mDB.update(DATABASE_TABLE, cv, FIELD_PHONE + " = ?",
                new String[]{String.valueOf(phone)});
    }


}
