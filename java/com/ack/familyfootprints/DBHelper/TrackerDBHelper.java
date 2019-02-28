package com.ack.familyfootprints.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucky Goyal on 3/12/2016.
 */
public class TrackerDBHelper extends SQLiteOpenHelper{
    /** Database name */
    private static String DBNAME = "mtravellersqlite_trackerdb";

    /** Version number of the database */

    private static int VERSION = 3;

    /** Field 1 of the table locations, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";

    /** Field 2 of the table locations, which is the primary key */
    public static final String FIELD_CONTACT = "contactName";

    /** Field 3 of the table locations, which is the primary key */
    public static final String FIELD_MOBILE = "mobilePhone";

    /** Field 4 of the table locations, stores the current date and time*/
    public static final String FIELD_DATE = "todayDate";

    /** Field 5 of the table locations, stores the current date and time*/
    public static final String FIELD_EST = "est";

    /** Field 6 of the table locations, stores the current date and time*/
    public static final String FIELD_EET = "eet";

    /** Field 7 of the table locations, stores the current date and time*/
    public static final String FIELD_NAME = "placeName";

    /** Field 7 of the table locations, stores the current date and time*/
    public static final String FIELD_ADR = "placeAddress";

    /** Field 8 of the table locations, stores the latitude */
    public static final String FIELD_LAT = "lat";

    /** Field 9 of the table locations, stores the longitude*/
    public static final String FIELD_LNG = "lng";

    /** Field 10 of the table locations, stores the current date and time*/
    public static final String FIELD_LM = "lastMile";

    /** Field 10 of the table locations, stores the current date and time*/
    public static final String FIELD_MROWID = "mrowId";


    /** Field 10 of the table locations, stores the current date and time*/
    public static final String FIELD_TRANSIT = "transit";

    /** Field 10 of the table locations, stores the current date and time*/
    public static final String FIELD_TRANSIT_TIME = "transitTime";

    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE = "tracker_abcd";

    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;

    /** Constructor
     * @param context*/

    public  TrackerDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }    /** This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called

     * provided the database does not exists
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =     "create table " + DATABASE_TABLE + " ( " +
                FIELD_ROW_ID + " integer primary key autoincrement , " +
                FIELD_CONTACT + " text , " +
                FIELD_MOBILE + " text , " +
                FIELD_DATE + " time ," +
                FIELD_EST + " time ," +
                FIELD_EET + " time ," +
                FIELD_NAME + " text ," +
                FIELD_ADR + " text ," +
                FIELD_LNG + " double , " +
                FIELD_LAT + " double , " +
                FIELD_LM + " text , " +
                FIELD_TRANSIT + " text , " +
                FIELD_TRANSIT_TIME + " text " +
                " ) ";


        db.execSQL(sql);
        //db.close();
    }

    /** Inserts a new location to the table locations */
    public long insertTrackerLoc(String contactName, String mobile, String dateSelected, String est,  String strAdrP,String strAdrFull, double latitude, double longitude) {
        // Creating an instance of ContentValues
        ContentValues contentValues = new ContentValues();

        // Setting contact name in ContentValues
        contentValues.put(FIELD_CONTACT, contactName );

        // Setting phone in ContentValues
        contentValues.put(FIELD_MOBILE, mobile );

        // Setting Date in ContentValues
        contentValues.put(FIELD_DATE, dateSelected );

        // Setting est in ContentValues
        contentValues.put(FIELD_EST, est );

        // Setting Address in ContentValues
        contentValues.put(FIELD_NAME, strAdrP );

        // Setting Address in ContentValues
        contentValues.put(FIELD_ADR, strAdrFull );

        // Setting latitude in ContentValues
        contentValues.put(FIELD_LAT, latitude );

        // Setting latitude in ContentValues
        contentValues.put(FIELD_LNG, longitude );

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_TRANSIT, "NA");

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_TRANSIT_TIME, "NA");

        // Creating an instance of LocationInsertTask
        long rowID = mDB.insert(DATABASE_TABLE, null, contentValues);
        //mDB.close();
        return rowID;

    }

    /** Deletes all locations from the table */
    public int del(){
        int cnt = mDB.delete(DATABASE_TABLE, null, null);
        //mDB.close();
        return cnt;
    }

    /** Returns all the locations from the table */
    public Cursor getAllLocations(){
        Cursor C = mDB.query(DATABASE_TABLE, new String[]{FIELD_ROW_ID,FIELD_CONTACT, FIELD_MOBILE, FIELD_DATE, FIELD_EST, FIELD_NAME, FIELD_ADR, FIELD_LAT, FIELD_LNG , FIELD_TRANSIT, FIELD_TRANSIT_TIME},
                 null, null, null, null, null, null);
        //mDB.close();
        return C;
    }
    /** Returns all the locations from the table
     * @param selectedDate*/
    public Cursor getTodayLocations(String selectedDate, String contactPhone){
        Cursor C=  mDB.query(DATABASE_TABLE, new String[]
                { FIELD_ROW_ID,FIELD_CONTACT, FIELD_MOBILE, FIELD_DATE, FIELD_EST, FIELD_EET, FIELD_NAME, FIELD_ADR, FIELD_LAT, FIELD_LNG, FIELD_TRANSIT, FIELD_TRANSIT_TIME }
                , "todayDate = ? and mobilePhone = ?", new String[]{selectedDate,contactPhone }, null, null,  " est asc " +
                        "", null);
        //mDB.close();

        return C;
    }
    public Cursor getTripLocations(String esd, String eed){
        Cursor C=  mDB.query(DATABASE_TABLE, new String[]
                { FIELD_ROW_ID,FIELD_CONTACT, FIELD_MOBILE, FIELD_DATE, FIELD_EST, FIELD_NAME, FIELD_ADR, FIELD_LAT, FIELD_LNG, FIELD_TRANSIT, FIELD_TRANSIT_TIME }
                , "todayDate >= ? and todayDate <= ?", new String[]{String.valueOf(esd), String.valueOf(eed)}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getRowLocations(String rowId){
        Cursor C =  mDB.query(DATABASE_TABLE, new String[]
                { FIELD_ROW_ID,FIELD_CONTACT, FIELD_MOBILE, FIELD_DATE, FIELD_EST, FIELD_NAME, FIELD_ADR, FIELD_LAT, FIELD_LNG,FIELD_TRANSIT, FIELD_TRANSIT_TIME  }
                , "rowId = ?", new String[]{String.valueOf(rowId)}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getMaxRow(){
        Cursor C= mDB.query(DATABASE_TABLE, new String[]
                { "max(rowId) as rowId" }
                , null, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getMaxPhoneRow(String contactPhone){
        Cursor C= mDB.query(DATABASE_TABLE, new String[]
                 { "max(rowId) as rowId" }
                , "mobilePhone = ?", new String[]{contactPhone }, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getMasterPhoneRow(String contactPhone, String orowId){
        Cursor C= mDB.query(DATABASE_TABLE, new String[]
                        { "max(rowId) as rowId" }
                , "mobilePhone = ? and rowId = ?", new String[]{contactPhone, orowId }, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getDateTimePhoneRow(String todayDate, String est){
        Cursor C= mDB.query(DATABASE_TABLE, new String[]
                        { "max(rowId) as rowId" }
                , "todayDate = ? and est = ?", new String[]{todayDate, est }, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getLastLocations(long rowId){
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                { FIELD_ROW_ID,FIELD_CONTACT, FIELD_MOBILE, FIELD_DATE, FIELD_EST, FIELD_EET, FIELD_NAME, FIELD_ADR, FIELD_LAT, FIELD_LNG, FIELD_TRANSIT, FIELD_TRANSIT_TIME }
                , "rowId = ?", new String[]{String.valueOf(rowId)}, null, null, null, null);
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

    public int updateLastLoc(long rowId,  String fet, String lastmile,
                             String transit, String transitTime) {
        // Drop older StringDBs table if existed
        ContentValues contentValues = new ContentValues();

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_EET, fet);

        // Setting Update Timestamp in ContentValues
        contentValues.put(FIELD_LM, lastmile);

        // Setting Update Timestamp in ContentValues
        contentValues.put(FIELD_TRANSIT, transit);

        // Setting Update Timestamp in ContentValues
        contentValues.put(FIELD_TRANSIT_TIME, transitTime);
        // updating row
        return mDB.update(DATABASE_TABLE, contentValues, FIELD_ROW_ID + " = ?",
                new String[] { String.valueOf(rowId) });
    }
}

