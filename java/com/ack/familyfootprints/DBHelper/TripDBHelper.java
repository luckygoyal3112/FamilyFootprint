package com.ack.familyfootprints.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucky Goyal on 8/1/2015.
 */
public class TripDBHelper extends SQLiteOpenHelper {
    /** Database name */
    private static String DBNAME = "mtravellersqlite_trip";

    /** Version number of the database */
    private static int VERSION = 4;

    /** Field 1 of the table locations, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";


    /** Field 2 of the table locations, stores the trip Name*/
    public static final String FIELD_TRIP = "tripName";
    /** Field 3 of the table locations, stores the Trip start date and time*/
    public static final String FIELD_ESD = "esd";

    /** Field 4 of the table locations, stores the Trip end date and time*/
    public static final String FIELD_EED = "eed";

    /** Field 5 of the table locations, stores the current date and time*/
    public static final String FIELD_RMRKS = "remarks";

    /** Field 6 of the table locations, stores the time on which trip was added*/
    public static final String FIELD_UTS = "updateTime";


    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE = "trips_a";

    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;

    /** Constructor
     * @param context*/

    public TripDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }    /** This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called


     * provided the database does not exists
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =     "create table " + DATABASE_TABLE + " ( " +
                FIELD_ROW_ID + " integer primary key autoincrement , " +
                FIELD_TRIP + " text ," +
                FIELD_ESD + " time ," +
                FIELD_EED + " time ," +
                FIELD_RMRKS + " text ," +
                FIELD_UTS + " time " +
                " ) ";


        db.execSQL(sql);
        //db.close();
    }

    /** Inserts a new location to the table locations */
    public long insertTrip(String trip, String esd, String eed, String remarks, String uts) {

        // Creating an instance of ContentValues
        ContentValues contentValues = new ContentValues();

        // Setting Update Timestamp in ContentValues
        contentValues.put(FIELD_TRIP, trip);

        // Setting Effective Start Timestamp in ContentValues
        contentValues.put(FIELD_ESD, esd);

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_EED, eed);

       // Setting place in ContentValues
        contentValues.put(FIELD_RMRKS, remarks);

        // Setting date in ContentValues
        contentValues.put(FIELD_UTS, uts);

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
    public Cursor getAllTrips(){
        Cursor C = mDB.query(DATABASE_TABLE, new String[]{FIELD_ROW_ID, FIELD_TRIP, FIELD_ESD, FIELD_EED, FIELD_RMRKS, FIELD_UTS}, null, null, null, null, null, null);
        //mDB.close();
        return C;
    }
    /** Returns all the locations from the table
     * @param selectedDate*/
    public Cursor getTodayTrips(String selectedDate){
        Cursor C=  mDB.query(DATABASE_TABLE, new String[]
                { FIELD_ROW_ID,  FIELD_TRIP, FIELD_ESD, FIELD_EED, FIELD_RMRKS, FIELD_UTS}
                , "eed =< ? and esd >= ?", new String[]{String.valueOf(selectedDate), String.valueOf(selectedDate)}
                , null, null, null, null);
        //mDB.close();
        return C;
    }
    public Cursor getRowTrips(long rowId) {
        Cursor C = mDB.query(DATABASE_TABLE, new String[]
                {FIELD_ROW_ID, FIELD_TRIP, FIELD_ESD, FIELD_EED, FIELD_RMRKS, FIELD_UTS}
                , "rowId = ?", new String[]{String.valueOf(rowId)}, null, null, null, null);
        //mDB.close();
        return C;
    }

    public Cursor getMaxRow(){
        Cursor C= mDB.query(DATABASE_TABLE, new String[]
                { "max(_id) as rowId" }
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

    public int updateTrip(long rowId, String trip, String esd, String eed, String remarks, String uts) {
        // Drop older StringDBs table if existed
        ContentValues contentValues = new ContentValues();

        // Setting Update Timestamp in ContentValues
        contentValues.put(FIELD_TRIP, trip);

        // Setting Effective Start Timestamp in ContentValues
        contentValues.put(FIELD_ESD, esd);

        // Setting Ëffective End Timestamp in ContentValues
        contentValues.put(FIELD_EED, eed);

        // Setting place in ContentValues
        contentValues.put(FIELD_RMRKS, remarks);

        // Setting date in ContentValues
        contentValues.put(FIELD_UTS, uts);

        // updating row
        return mDB.update(DATABASE_TABLE, contentValues, FIELD_ROW_ID + " = ?",
                new String[] { String.valueOf(rowId) });
    }
}

