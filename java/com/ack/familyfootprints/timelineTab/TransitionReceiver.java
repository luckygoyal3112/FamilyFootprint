package com.ack.familyfootprints.timelineTab;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ack.familyfootprints.DBHelper.LocationDBHelper;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransitionReceiver extends IntentService {
    private static final String TAG = "TransitionReceiver";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TransitionReceiver() {
        super("TransitionReceiver");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name the worker thread, important only for debugging.
     */

    @Override
    public void onHandleIntent(@Nullable Intent intent) {
        String rowIda = null;
        Log.d("hmm: ", "DriveBuddyTransitionReceiver - Enter");
        Log.d(TAG, "Transition Reciver - Entered");
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                String transitName = toActivityString(event.getActivityType());
                String transitionType = toTransitionType(event.getTransitionType());
                String dateSelected = getTodayDate();
                String uts = getTodayTime();
                String fulluts = getfullTodayTime();
                LocationDBHelper locDb = new LocationDBHelper(this);
                Cursor locationCursora = locDb.getMaxRow();
                if (locationCursora != null) {
                    if (locationCursora.moveToFirst()) {
                        try {
                            do {
                                rowIda = (locationCursora.getString(locationCursora.getColumnIndex("rowId")));
                            } while (locationCursora.moveToNext());
                        } finally {
                            if (locationCursora != null && !locationCursora.isClosed())
                                locationCursora.close();
                        }
                    }
                    Log.d(TAG, "transit type is" + transitionType);
                    if (transitionType.equals("ENTER")) {
                       locDb.updateLastLocTransitdata(rowIda, transitName,  "");
                        Log.d(TAG, "transit name is" + transitName);
                    } else if (transitionType.equals("EXIT")) {
                        try {
                            Log.d(TAG, "transit name is" + transitName);
                            locDb.updateLastLocTransitdata(rowIda, transitName, getTransittime(rowIda));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    //Log.d(TAG, "rowId is " + rowId);
                    //locDb.close();
                /*mLogFragment.getLogView()
                        .println("Transition: " + activity + " (" + transitionType + ")" + "   "
                                + new SimpleDateFormat("HH:mm:ss", Locale.US)
                                .format(new Date()));*/
                }
            }
        }
    }
    private static String toActivityString(int activity) {
        switch (activity) {
            case DetectedActivity.IN_VEHICLE:
                return "IN_VEHICLE";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            default:
                return "UNKNOWN";
        }
    }
    private static String toTransitionType(int transitionType) {
        switch (transitionType) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "ENTER";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "EXIT";
            default:
                return "UNKNOWN";
        }
    }
    public String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String selectedDate = dateFormat.format(new Date()); // Find todays date
        return selectedDate;
    }
    public String getTodayTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        //estFormatter.setTimeZone(TimeZone.getTimeZone("EST"));
        String uts = dateFormat.format(new Date()); // Find todays date
        return uts;
    }
    public String getfullTodayTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ZZ");

        //estFormatter.setTimeZone(TimeZone.getTimeZone("EST"));
        String uts = dateFormat.format(new Date()); // Find todays date
        return uts;
    }

    public String getTransittime(String rowId) throws ParseException {
        int timedif = 0;
        LocationDBHelper locDb = new LocationDBHelper(this);
        Cursor rowCursor = locDb.getRowLocations(rowId);
        if (rowCursor != null) {
            if (rowCursor.moveToFirst()) {
                String oldUte = (rowCursor.getString(rowCursor.getColumnIndex("fet")));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ZZ");
                Date oldDate = dateFormat.parse(oldUte);
                //Date nowDate = dateFormat.parse( dateFormat.format(new Date()));
                // DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                //Date date = format.parse(string);

                timedif = (int) (new Date().getTime() - oldDate.getTime());

                long diff = (new Date().getTime() - oldDate.getTime());
                timedif = (int) (diff / (60 * 1000) % 60);

            }
        }
        return String.valueOf(timedif) + "Min";
    }
}
