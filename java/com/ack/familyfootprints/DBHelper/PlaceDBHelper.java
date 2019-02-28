package com.ack.familyfootprints.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucky Goyal on 11/2/2015.
 */
public class PlaceDBHelper extends SQLiteOpenHelper{
    private static String DBNAME = "mtravellersqlite_pl_1";

    /** Version number of the database */
    private static int VERSION = 1;

    /** Field 1 of the table locations, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";

    /** Field 2 of the table locations, stores the Place Id*/
    public static final String FIELD_PLACEID = "placeId";

    /** Field 3 of the table locations, stores the latitude */
    public static final String FIELD_LAT = "lat";

    /** Field 4 of the table locations, stores the longitude*/
    public static final String FIELD_LNG = "lng";

    /** Field 5 of the table locations, stores the current date and time*/
    public static final String FIELD_DATE = "todayDate";

    /** Field 6 of the table locations, stores the Place name*/
    public static final String FIELD_PLACE = "placeName";

    /** Field 6 of the table locations, stores the Place name*/
    public static final String FIELD_PLACE_ADR = "placeNameAdr";

    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE = "place_n";

    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase pDB;

    /** Constructor
     * @param context*/

    public PlaceDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.pDB = getWritableDatabase();
    }    /** This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called

         * provided the database does not exists
         * */
    @Override
    public void onCreate(SQLiteDatabase db) {
         String sql =     "create table " + DATABASE_TABLE + " ( " +
                    FIELD_ROW_ID + " integer primary key autoincrement , " +
                    FIELD_PLACEID + " text , " +
                    FIELD_LAT + " double , " +
                    FIELD_LNG + " double , " +
                    FIELD_DATE + " time ," +
                    FIELD_PLACE + " text ," +
                    FIELD_PLACE_ADR + " text " +
                    " ) ";
          db.execSQL(sql);
            //db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older StringDBs table if existed
       db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE);

        // create fresh StringDBs table
        this.onCreate(db);
    }

    /** Inserts a new location to the table locations */
    public long insertPlace(String placeId, double latitude, double longitude,
                                   String dateSelected, String placeName, String placeNameAdr) {
            // Creating an instance of ContentValues
            ContentValues contentValues = new ContentValues();

            // Setting date in ContentValues
            contentValues.put(FIELD_PLACEID, placeId);

            // Setting latitude in ContentValues
            contentValues.put(FIELD_LAT, latitude );

            // Setting longitude in ContentValues
            contentValues.put(FIELD_LNG, longitude);

            // Setting date in ContentValues
            contentValues.put(FIELD_DATE, dateSelected);

            // Setting date in ContentValues
            contentValues.put(FIELD_PLACE, placeName);

        // Setting date in ContentValues
        contentValues.put(FIELD_PLACE_ADR, placeNameAdr);


            // Creating an instance of LocationInsertTask
            long rowID = pDB.insert(DATABASE_TABLE, null, contentValues);
            //mDB.close();
            return rowID;
    }
    public Cursor getCachePlace(double lat, double lng) {
        Cursor C=  pDB.query(DATABASE_TABLE, new String[]
                { FIELD_PLACEID, FIELD_LAT , FIELD_LNG, FIELD_DATE, FIELD_PLACE, FIELD_PLACE_ADR },
                "lat = ? and lng = ?", new String[]{String.valueOf(lat), String.valueOf(lng)}
                //{ FIELD_PLACEID, FIELD_LAT , FIELD_DATE, FIELD_PLACE },
                //"lat = ? ", new String[]{String.valueOf(lat)}
                ,null, null, null,null);

        //mDB.close();
        return C;
    }
}
