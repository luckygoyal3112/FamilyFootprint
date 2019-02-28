package com.ack.familyfootprints.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucky Goyal on 1/8/2016.
 */
public class otpCntHelper extends SQLiteOpenHelper {

    private static String DBNAME = "mtravellersqlite_otp";

    /** Version number of the database */
    private static int VERSION = 1;

    /** Field 1 of the table Groups, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";

    /** Field 1.5 of the table Groups, stores the  OTP*/
    public static final String FIELD_PHONE = "phone";

    /** Field 2 of the table Groups, stores the  OTP*/
    public static final String FIELD_OTP = "otp";

    /** Field 3 of the table Groups, stores the time on which group was added*/
    public static final String FIELD_CTS = "cts";
    /** A constant, stores the the table name */

    private static final String DATABASE_TABLE = "otp_all";

    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;

    /** Constructor
     * @param context*/

    public otpCntHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }    /** This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called


     * provided the database does not exists
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =     "create table " + DATABASE_TABLE + " ( " +
                FIELD_ROW_ID + " integer primary key autoincrement , " +
                FIELD_PHONE + " text ," +
                FIELD_OTP + " text ," +
                FIELD_CTS + " time " +
                " ) ";

        db.execSQL(sql);
    }

    /** Inserts a new Contacts to the table All Comtacts */
    public long insertRecord(String phone, String otp, String cts) {

        // Creating an instance of ContentValues
        ContentValues contentValues = new ContentValues();

        // Setting date in ContentValues
        contentValues.put(FIELD_PHONE, phone);

        // Setting OTP in ContentValues
        contentValues.put(FIELD_OTP, otp);

        // Setting date in ContentValues
        contentValues.put(FIELD_CTS, cts);

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
    public Cursor getAllOTP(){
        Cursor C = mDB.query(DATABASE_TABLE, new String[]{FIELD_ROW_ID, FIELD_PHONE, FIELD_OTP, FIELD_CTS}, null, null, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getRowContacts(long rowId) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID,FIELD_PHONE, FIELD_OTP, FIELD_CTS}
                , "rowId = ?", new String[]{String.valueOf(rowId)}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getPhoneotp(String phone) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID,FIELD_PHONE, FIELD_OTP, FIELD_CTS}
                , "phone = ?", new String[]{String.valueOf(phone)}, null, null, "_id DESC");
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
}
