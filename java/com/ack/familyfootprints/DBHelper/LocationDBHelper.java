
package com.ack.familyfootprints.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucky Goyal on 8/1/2015.
 */
public class LocationDBHelper extends SQLiteOpenHelper {
    /** Database name */
    private static String DBNAME = "mtravellersqlite_locb";

    /** Version number of the database */
    private static int VERSION = 2;

    /** Field 1 of the table locations, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";

    /** Field 2 of the table locations, stores the latitude */
    public static final String FIELD_LAT = "lat";

    /** Field 3 of the table locations, stores the longitude*/
    public static final String FIELD_LNG = "lng";

    /** Field 4 of the table locations, stores the zoom level of map*/
    public static final String FIELD_ZOOM = "zom";

    /** Field 5 of the table locations, stores the current date and time*/
    public static final String FIELD_EST = "est";

    /** Field 6 of the table locations, stores the current date and time*/
    public static final String FIELD_EET = "eet";

    /** Field 7 of the table locations, stores the current date and time*/
    public static final String FIELD_LM = "lastMile";

    /** Field 7 of the table locations, stores the current date and time*/
    public static final String FIELD_ADR = "address";

    /** Field 8 of the table locations, stores the current date and time*/
    public static final String FIELD_DATE = "todayDate";

    /** Field 9 of the table locations, stores the Place name*/
    public static final String FIELD_PLACE = "placeName";

    /** Field 9 of the table locations, stores the Place name*/
    public static final String FIELD_GA = "geoAddress";

    /** Field 10 of the table locations,
     *
     *
     *
     *
     * stores the Place Id*/
    public static final String FIELD_PLACEID = "placeId";

    /** Field 11 of the table locations, stores the current date and time*/
    public static final String FIELD_EFST = "fst";

    /** Field 12 of the table locations, stores the current date and time*/
    public static final String FIELD_EFET = "fet";

    /** Field 13 of the table locations, stores the current date and time*/
    public static final String FIELD_TRANSIT = "transit";

    /** Field 14 of the table locations, stores the current date and time*/
    public static final String FIELD_TRANSIT_TIME = "transitTime";
    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE = "locations_ab";


    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;

    /** Constructor
     * @param context*/

    public LocationDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }    /** This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called


     * provided the database does not exists
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =     "create table " + DATABASE_TABLE + " ( " +
                FIELD_ROW_ID + " integer primary key autoincrement , " +
                FIELD_LNG + " double , " +
                FIELD_LAT + " double , " +
                FIELD_ZOOM + " text , " +
                FIELD_EST + " time ," +
                FIELD_EET + " time ," +
                FIELD_LM + " text ," +
                FIELD_ADR + " text ," +
                FIELD_DATE + " time ," +
                FIELD_PLACE + " text ," +
                FIELD_GA + " text ," +
                FIELD_PLACEID + " text ," +
                FIELD_EFST + " time ," +
                FIELD_EFET + " time ," +
                FIELD_TRANSIT + " text ," +
                FIELD_TRANSIT_TIME + " text " +
                " ) ";


        db.execSQL(sql);
        //db.close();
    }
    /** Inserts a new location to the table locations */
    public long insertLocation(double latitude, double longitude, String est,  String dateSelected,
                               String placeName, String strAdr, String geoAd, String placeId, String efst){
        // Creating an instance of ContentValues
        ContentValues contentValues = new ContentValues();

        // Setting latitude in ContentValues
        contentValues.put(FIELD_LAT, latitude );

        // Setting longitude in ContentValues
        contentValues.put(FIELD_LNG, longitude);

        // Setting zoom in ContentValues
        contentValues.put(FIELD_ZOOM, "10");

        // Setting Effective Start Timestamp in ContentValues
        contentValues.put(FIELD_EST, est);

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_EET, est);

        // Setting Update Timestamp in ContentValues
        contentValues.put(FIELD_LM, "N");

        // Setting Address in ContentValues
        contentValues.put(FIELD_ADR, strAdr);

        // Setting date in ContentValues
        contentValues.put(FIELD_DATE, dateSelected);

        // Setting place in ContentValues
        contentValues.put(FIELD_PLACE, placeName);

        // Setting place in ContentValues
        contentValues.put(FIELD_GA, geoAd);

        // Setting placeId in ContentValues
        contentValues.put(FIELD_PLACEID, placeId);

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_EFST, efst);

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_EFET, efst);

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
        Cursor C = mDB.query(DATABASE_TABLE, new String[]{FIELD_ROW_ID, FIELD_LAT, FIELD_LNG, FIELD_ZOOM, FIELD_EST, FIELD_EET, FIELD_ADR, FIELD_GA, FIELD_EFST, FIELD_EFET }, null, null, null, null, null, null);
        //mDB.close();
        return C;
    }
    /** Returns all the locations from the table
     * @param selectedDate*/
    public Cursor getTodayLocations(String selectedDate){
        Cursor C=  mDB.query(DATABASE_TABLE, new String[]
                { FIELD_ROW_ID,  FIELD_LAT , FIELD_LNG, FIELD_ZOOM, FIELD_EST,FIELD_EET,  FIELD_LM, FIELD_ADR, FIELD_DATE, FIELD_PLACE,FIELD_GA, FIELD_PLACEID, FIELD_EFST, FIELD_TRANSIT, FIELD_TRANSIT_TIME }
                , "todayDate = ?", new String[]{selectedDate}, null, null, "est asc", null);
        //mDB.close();
        return C;
    }
    public Cursor getTripLocations(String esd, String eed){
        Cursor C=  mDB.query(DATABASE_TABLE, new String[]
                { FIELD_ROW_ID,  FIELD_LAT , FIELD_LNG, FIELD_ZOOM, FIELD_EST,FIELD_EET,  FIELD_LM, FIELD_ADR, FIELD_DATE, FIELD_PLACE, FIELD_GA, FIELD_PLACEID, FIELD_EFST, FIELD_EFET  }
                , "todayDate >= ? and todayDate <= ?", new String[]{String.valueOf(esd), String.valueOf(eed)}, null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getRowLocations(String rowId){
        Cursor C =  mDB.query(DATABASE_TABLE, new String[]
                { FIELD_ROW_ID,  FIELD_LAT , FIELD_LNG, FIELD_ZOOM, FIELD_EST,FIELD_EET, FIELD_LM, FIELD_ADR, FIELD_DATE, FIELD_PLACE, FIELD_GA, FIELD_PLACEID , FIELD_EFST, FIELD_EFET,  FIELD_TRANSIT, FIELD_TRANSIT_TIME }
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
    public Cursor getLastLocations(long rowId){
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                { FIELD_ROW_ID,  FIELD_LAT , FIELD_LNG, FIELD_ZOOM, FIELD_EST, FIELD_EET, FIELD_LM, FIELD_ADR, FIELD_DATE,FIELD_PLACE, FIELD_GA, FIELD_PLACEID,FIELD_EFST, FIELD_EFET  }
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

    public int updateLastLoc(String rowId, String eet, String lastmile, String efet) {
        // Drop older StringDBs table if existed
        ContentValues contentValues = new ContentValues();

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_EET, eet);

        // Setting Update Timestamp in ContentValues
        contentValues.put(FIELD_LM, lastmile);

        // Setting Update Timestamp in ContentValues
        contentValues.put(FIELD_EFET, efet);

        // updating row
        return mDB.update(DATABASE_TABLE, contentValues, FIELD_ROW_ID + " = ?",
                new String[] { String.valueOf(rowId) });

    }

    public int updateLastLocTransitdata(String rowId, String transit, String transitTime) {
        // Drop older StringDBs table if existed
        ContentValues contentValues = new ContentValues();

        // Setting Ëffective transite type in ContentValues
        contentValues.put(FIELD_TRANSIT, transit);

        // Setting Update Transit Time in ContentValues
        contentValues.put(FIELD_TRANSIT_TIME, transitTime);

        // updating row
        return mDB.update(DATABASE_TABLE, contentValues, FIELD_ROW_ID + " = ?",
                new String[] { String.valueOf(rowId) });
    }
}