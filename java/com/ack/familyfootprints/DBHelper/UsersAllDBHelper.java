package com.ack.familyfootprints.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucky Goyal on 1/8/2016.
 */
public class UsersAllDBHelper extends SQLiteOpenHelper {

    private static String DBNAME = "mtravellersqlite_group_2";

    /** Version number of the database */
    private static int VERSION = 4;

    /** Field 1 of the table Groups, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";

    /** Field 2 of the table Groups, stores the  Name*/
    public static final String FIELD_NAME = "name";

    /** Field 2.5 of the table Groups, stores the  Name*/
    public static final String FIELD_LNAME = "localName";

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

    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE = "contacts_all";

    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;

    /** Constructor
     * @param context*/

    public UsersAllDBHelper(Context context) {
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
                FIELD_LNAME+ " text ," +
                FIELD_PHONE + " text ," +
                FIELD_CONTACT_STATUS + " text ," +
                FIELD_REGID + " text ," +
                FIELD_CTS + " time ," +
                FIELD_UTS + " time " +
                " ) ";

        db.execSQL(sql);
    }

    /** Inserts a new Contacts to the table All Comtacts */
    public long insertContactsAll(String name, String lname, String phone, String regId,  String cts, String uts) {

        // Creating an instance of ContentValues
        ContentValues contentValues = new ContentValues();

        // Setting Name in ContentValues
        contentValues.put(FIELD_NAME, name);

        // Setting Name in ContentValues
        contentValues.put(FIELD_LNAME, lname);

        // Setting Phone in ContentValues
        contentValues.put(FIELD_PHONE, phone);

        // Setting Status in ContentValues
        contentValues.put(FIELD_CONTACT_STATUS, "NEW");

        // Setting RegId in ContentValues
        contentValues.put(FIELD_REGID, regId);

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
    public Cursor getAllContacts(){
        Cursor C = mDB.query(DATABASE_TABLE, new String[]{FIELD_ROW_ID, FIELD_NAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_CTS, FIELD_UTS}, null, null, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getRowContacts(long rowId) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID, FIELD_NAME, FIELD_LNAME, FIELD_PHONE, FIELD_CONTACT_STATUS, FIELD_REGID, FIELD_CTS, FIELD_UTS}
                , "rowId = ?", new String[]{String.valueOf(rowId)}, null, null, null, null);
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

    public int updateGroup(long rowId, String name, String lname, String phone, String regId, String conStatus, String uts) {
        // Drop older StringDBs table if existed
        ContentValues contentValues = new ContentValues();

        // Setting Name in ContentValues
        contentValues.put(FIELD_NAME, name);

        contentValues.put(FIELD_LNAME, lname);

        // Setting Phone in ContentValues
        contentValues.put(FIELD_PHONE, phone);

        // Setting Status in ContentValues
        contentValues.put(FIELD_CONTACT_STATUS, conStatus);

        // Setting RegId in ContentValues
        contentValues.put(FIELD_REGID, regId);

        // Setting date in ContentValues
        contentValues.put(FIELD_UTS, uts);

        // updating row
        return mDB.update(DATABASE_TABLE, contentValues, FIELD_ROW_ID + " = ?",
                new String[] { String.valueOf(rowId) });
    }
}
