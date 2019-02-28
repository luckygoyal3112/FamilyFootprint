package com.ack.familyfootprints.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucky Goyal on 1/8/2016.
 */
public class GroupDBHelper extends SQLiteOpenHelper {

    private static String DBNAME = "mtravellersqlite_group7";

    /** Version number of the database */
    private static int VERSION = 3;

    /** Field 1 of the table Groups, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";

    /** Field 2 of the table Groups, stores the Group Name*/
    public static final String FIELD_GROUP = "groupName";

    /** Field 3 of the table Groups, stores the group Admin*/
    public static final String FIELD_ADMIN = "admin";

    /** Field 4 of the table Groups, stores the Group Type*/
    public static final String FIELD_GROUPTYPE = "groupType";

    /** Field 5 of the table Groups, stores the Remarks*/
    public static final String FIELD_RMRKS = "remarks";

    /** Field 6 of the table Groups, stores the time on which group was added*/
    public static final String FIELD_CTS = "cts";

    /** Field 6 of the table Groups, stores the time on which group was updated*/
    public static final String FIELD_UTS = "uts";

    /** Field 7 of the table locations, stores if the group is marked as Fav.*/
    public static final String FIELD_FAV = "fav";

    /** Field 1 of the table Contact, which is the primary key */
    public static final String CFIELD_ROW_ID = "_id";

    /** Field 2 of the table Contact, which is the primary key */
    //public static final String CFIELD_GROUP_ID = "gid";

    /** Field 3 of the table Contact, stores the Group Name*/
    public static final String CFIELD_GROUP = "groupName";

    /** Field 4 of the table Contact, stores the Cntact Name*/
    public static final String CFIELD_CON = "contactName";

    /** Field 5 of the table Contact, stores the contct phone number*/
    public static final String CFIELD_NMBR = "contactNumber";

    /** Field 5 of the table Groups, stores the Refgistration Id*/
    public static final String CFIELD_REGID = "regID";

    /** Field 5 of the table Contact, stores the contct phone number*/
    public static final String CFIELD_INVITE_STATUS = "inviteStatus";

    /** Field 6 of the table Groups, stores the time on which group was added*/
    public static final String CFIELD_CTS = "cts";

    /** Field 6 of the table Groups, stores the time on which group was updated*/
    public static final String CFIELD_UTS = "uts";

    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE_GROUP = "groups_a";
    private static final String DATABASE_TABLE_CON= "groups_contact_b";

    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;

    /** Constructor
     * @param context*/

    public GroupDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }    /** This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called


     * provided the database does not exists
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =     "create table " + DATABASE_TABLE_GROUP + " ( " +
                FIELD_ROW_ID + " integer primary key autoincrement , " +
                FIELD_GROUP + " text ," +
                FIELD_ADMIN + " text ," +
                FIELD_GROUPTYPE + " text ," +
                FIELD_RMRKS + " text ," +
                FIELD_FAV + " text ," +
                FIELD_CTS + " time ," +
                FIELD_UTS + " time " +
                " ) ";

        db.execSQL(sql);

        String sql_c =     "create table " + DATABASE_TABLE_CON + " ( " +
                CFIELD_ROW_ID + " integer primary key autoincrement , " +
                CFIELD_GROUP + " text ," +
                CFIELD_CON + " text ," +
                CFIELD_NMBR + " text ," +
                CFIELD_REGID+ " text ," +
                CFIELD_INVITE_STATUS + " text ," +
                CFIELD_CTS + " time ," +
                CFIELD_UTS + " time " +
                " ) ";

        db.execSQL(sql_c);
        //db.close();
    }

    /** Inserts a new group to the table Groups */
    public long insertGroup(String group, String admin, String groupType, String remarks, String fav, String cts, String uts) {

        // Creating an instance of ContentValues
        ContentValues contentValues = new ContentValues();

        // Setting Update Timestamp in ContentValues
        contentValues.put(FIELD_GROUP, group);

        // Setting Effective Start Timestamp in ContentValues
        contentValues.put(FIELD_ADMIN, admin);

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_GROUPTYPE, groupType);

        // Setting place in ContentValues
        contentValues.put(FIELD_RMRKS, remarks);

        // Setting place in ContentValues
        contentValues.put(FIELD_FAV, fav);

        // Setting date in ContentValues
        contentValues.put(FIELD_CTS, cts);

        // Setting date in ContentValues
        contentValues.put(FIELD_UTS, uts);

        // Creating an instance of LocationInsertTask
        long rowID = mDB.insert(DATABASE_TABLE_GROUP, null, contentValues);
        //mDB.close();
        return rowID;
    }
    /** Inserts a new group to the table Groups */
    public long insertContact( String group, String contactName, String contactPhone, String regId, String inv,String cts, String uts) {

        // Creating an instance of ContentValues
        ContentValues contentValues = new ContentValues();
       // contentValues.put(CFIELD_GROUP_ID, groupId);
        contentValues.put(CFIELD_GROUP, group);
        contentValues.put(CFIELD_CON, contactName);
        contentValues.put(CFIELD_NMBR, contactPhone);
        contentValues.put(CFIELD_REGID, regId);
        contentValues.put(CFIELD_INVITE_STATUS, inv);
        contentValues.put(CFIELD_CTS, cts);
        contentValues.put(CFIELD_UTS, uts);

        // Creating an instance of LocationInsertTask
        long rowID = mDB.insert(DATABASE_TABLE_CON, null, contentValues);
        //mDB.close();
        return rowID;
    }
    /** Deletes all locations from the table */
    public int del(){
        int cnt = mDB.delete(DATABASE_TABLE_GROUP, null, null);
        //mDB.close();
        return cnt;
    }

    /** Returns all the locations from the table */
    public Cursor getAllGroups(){
        Cursor C = mDB.query(DATABASE_TABLE_GROUP, new String[]{FIELD_ROW_ID, FIELD_GROUP, FIELD_ADMIN, FIELD_GROUPTYPE, FIELD_RMRKS, FIELD_FAV, FIELD_CTS, FIELD_UTS}, null, null, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getAllContacts(){
        Cursor C = mDB.query(DATABASE_TABLE_CON, new String[]{CFIELD_ROW_ID, CFIELD_GROUP, CFIELD_CON, CFIELD_REGID, CFIELD_INVITE_STATUS}, null, null, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getRowGroups(long rowId) {
        Cursor C = mDB.query(DATABASE_TABLE_GROUP, new String[]
                {FIELD_ROW_ID, FIELD_GROUP, FIELD_ADMIN, FIELD_GROUPTYPE, FIELD_RMRKS, FIELD_FAV, FIELD_CTS, FIELD_UTS}
                , "rowId = ?", new String[]{String.valueOf(rowId)}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getRowContacts(long rowId) {
        Cursor C = mDB.query(DATABASE_TABLE_CON, new String[]
                {CFIELD_ROW_ID, CFIELD_GROUP, CFIELD_CON, CFIELD_NMBR, CFIELD_REGID, CFIELD_INVITE_STATUS}
                , "rowId = ?", new String[]{String.valueOf(rowId)}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getMaxRow(){
        Cursor C= mDB.query(DATABASE_TABLE_GROUP, new String[]
                { "max(gid) as rowId" }
                , null, null, null, null, null);
        //mDB.close();
        return C;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older StringDBs table if existed
        db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_GROUP);

        // create fresh StringDBs table
        this.onCreate(db);
    }

    public int updateGroup(long rowId, String groupName, String grouptype, String fav, String remarks, String uts) {
        // Drop older StringDBs table if existed
        ContentValues contentValues = new ContentValues();

        // Setting Update Timestamp in ContentValues
        contentValues.put(FIELD_GROUP, groupName);

        // Setting Effective Start Timestamp in ContentValues
        contentValues.put(FIELD_GROUPTYPE, grouptype);

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_FAV, fav);

        // Setting place in ContentValues
        contentValues.put(FIELD_RMRKS, remarks);

        // Setting date in ContentValues
        contentValues.put(FIELD_UTS, uts);

        // updating row
        return mDB.update(DATABASE_TABLE_GROUP, contentValues, FIELD_ROW_ID + " = ?",
                new String[] { String.valueOf(rowId) });
    }

    public Cursor getTobeInvitedContacts() {
        Cursor C = mDB.query(DATABASE_TABLE_CON, new String[]
                {CFIELD_ROW_ID, CFIELD_CON, CFIELD_NMBR, CFIELD_REGID, CFIELD_INVITE_STATUS}
                , "inviteStatus = ?", new String[]{"A"}, null, null, null, null);
        //mDB.close();
        return C;
    }
}
