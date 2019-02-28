package com.ack.familyfootprints.timelineTab;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ack.familyfootprints.CurrentPlace;
import com.ack.familyfootprints.DBHelper.LocationDBHelper;
import com.ack.familyfootprints.DBHelper.PlaceDBHelper;
import com.ack.familyfootprints.GCMClientApp.QuickstartPreferences;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationGetIntent extends BroadcastReceiver {
    private static final int DIFF_MINUTES = 1000 * 60 ;
    private static final double DIFF_DIS = 0.2;
    private static final double INTRANS_DIS = 0.05;
    private static Location currentBestLocation = null;
    private static Location lastknownlocation = null;
    public static final String TAG = LocationGetIntent.class.getSimpleName();
    private Object transitdata;
    private List<ActivityTransition> transitions;
    private ActivityRecognitionClient activityRecognitionClient;
    private PendingIntent transitionPendingIntent;
    private static ActivityTransitionRequest transitionRequest;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received following Location Fix from TrackbookLoc Service");
        if (LocationResult.hasResult(intent)) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location =
                    locationResult.getLastLocation();
            Log.d(TAG, "Lat is" + location.getLatitude());
            Log.d(TAG, "Long is" + location.getLongitude());
            if (location != null) {
                if (currentBestLocation != null) {
                    if (getIsFirstCheck(context)) {
                        try {
                            onHandleIntent(currentBestLocation, context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (isBetterLocation(context, location, currentBestLocation)) {
                            updateendtime(context, "Y");
                            //currentBestLocation = location;
                            if (stationary(context, location, lastknownlocation)) {
                                Log.d(TAG, "Not In transit. Should store");
                                try {
                                    currentBestLocation = location;
                                    //updateendtime(context, "Y");
                                    //stoptransitdata();
                                    onHandleIntent(currentBestLocation, context);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, "Probably In transit. Capture transit data!!");
                                requestTransitdata(context);
                            }
                        } else {
                            Log.d(TAG, "isBetterLocation returned false");
                            updateendtime(context, "N");
                        }
                    }
                } else {
                    currentBestLocation = location;
                    if (getIsFirstCheck(context)) {
                        try {
                            onHandleIntent(currentBestLocation, context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (location != null)
                    lastknownlocation = location;
            }
        }
        // Location location = (Location) intent.getExtras().get(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
        //Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);

    }

    private boolean updateendtime(Context context, String lastmile) {
        LocationDBHelper locDb = new LocationDBHelper(context);
        String finishTime = getTodayTime();
        String rowId = null;
        String getlastMile = "";
        int locationCursorLast = 0;
        Cursor locationCursor = locDb.getMaxRow();
            if (locationCursor != null) {
                if (locationCursor.moveToFirst()) {
                    try {
                        do {
                            rowId = (locationCursor.getString(locationCursor.getColumnIndex("rowId")));
                        } while (locationCursor.moveToNext());
                    }
                    finally {
                        if (locationCursor != null && !locationCursor.isClosed())
                            locationCursor.close();
                    }
                }
                Cursor rowCursor = locDb.getRowLocations(rowId);
                if (rowCursor != null) {
                    try {if (rowCursor.moveToFirst()) {
                        getlastMile = (rowCursor.getString(rowCursor.getColumnIndex("lastMile")));
                    }
                    }finally {
                        if (rowCursor != null && !rowCursor.isClosed())
                            rowCursor.close();
                    }
                }
                //locationCursorLast = locDb.updateLastLoc(rowId, finishTime, lastmile, getfullTodayTime());
                if (getlastMile.equals("Y")) {
                    Log.d(TAG, "Last known loc alreay ended");
                } else {
                    locationCursorLast = locDb.updateLastLoc(rowId, finishTime, lastmile, getfullTodayTime());
                }
                 if (locationCursorLast == 1) {
                     return true;
                 }
                return false;
            } else {
                return false;
            }
        }

    private boolean stationary(Context context, Location location, Location lastknownlocation) {
        if (lastknownlocation == null) {
            // A new location is always better than no location
            return false;

        }
        long timeDelta;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            timeDelta = location.getElapsedRealtimeNanos() - lastknownlocation.getElapsedRealtimeNanos();
            Log.d(TAG, "New Elapsed Nano Time  is " +location.getElapsedRealtimeNanos());
            Log.d(TAG, "Old Elapsed Nano Time  is " +lastknownlocation.getElapsedRealtimeNanos());
        } else {
            timeDelta = location.getTime() - lastknownlocation.getTime();
            Log.d(TAG, "New  Time  is " +location.getTime());
            Log.d(TAG, "Old  Time  is " +lastknownlocation.getTime());
        }
        double distance = getDistance(location.getLatitude(), location.getLongitude(), lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), "K");

        boolean hasTravelled = distance >= INTRANS_DIS;
        boolean hasSufficientTimePassed = timeDelta >  60 * 2 * 1000000000;
        Log.d(TAG, "timeDelta is " + timeDelta);
        Log.d(TAG, "distance is " + distance);
        Log.d(TAG, "location.hasSpeed() is " + location.hasSpeed());
        Log.d(TAG, "location.getSpeed() is " + location.getSpeed());
        Log.d(TAG, "hasSufficientTimePassed flag is " + hasSufficientTimePassed);

        if (hasTravelled || location.hasSpeed() || location.getSpeed()>0)
            //|| hasSufficientTimePassed)
        {
            return false;
        } else  {
            return true;
        }
    }
    private void onHandleIntent(Location currentBestLocation, Context context) throws Exception {
        Log.d(TAG, "Gotcha!");
        long rowId = 0;
        String rowIda = null;
        String oldDate = null;
        String oldest = null;
        String oldTransit = null;
        String oldTransitTime = null;
        double lat = currentBestLocation.getLatitude();
        double lng = currentBestLocation.getLongitude();
        String placeId = null;
        String oldUte = null;
        Cursor placeCursor = (isknownplace(context, lat, lng));
            if (placeCursor.moveToFirst()) {
                placeId = (placeCursor.getString(placeCursor.getColumnIndex("placeId")));
            }
        if (placeId == null) {
            Log.d(TAG, "PLaces not cached");
            getPlaces(currentBestLocation, context);
        } else {
            String dateSelected = getTodayDate();
            String uts = getTodayTime();
            String fulluts = getfullTodayTime();
            String strAdrP = placeCursor.getString(placeCursor.getColumnIndex("placeNameAdr"));
            String strAdrFull = placeCursor.getString(placeCursor.getColumnIndex("placeNameAdr"));
            /*Log.d(TAG, "Place is" + strAdrP);
            Log.d(TAG, "Address is" + strAdrFull);
            Log.d(TAG, "Date is " + dateSelected);
            Log.d(TAG, "Time is " + uts);
            Log.d(TAG, "Full Time is " + fulluts);*/
            if (strAdrP != null) {
                LocationDBHelper locDb = new LocationDBHelper(context);
                String geoAd = getAddress(context, lat, lng);

                Cursor locationCursora = locDb.getMaxRow();
                if (locationCursora != null) {
                    if (locationCursora.moveToFirst()) {
                        try {
                            do {
                                rowIda = (locationCursora.getString(locationCursora.getColumnIndex("rowId")));
                            } while (locationCursora.moveToNext());
                        }  finally {
                            if (locationCursora != null && !locationCursora.isClosed())
                                locationCursora.close();
                        }
                    }
                    Cursor rowCursor = locDb.getRowLocations(rowIda);
                    if (rowCursor != null) {
                        if (rowCursor.moveToFirst()) {
                            oldUte = (rowCursor.getString(rowCursor.getColumnIndex("fet")));
                            oldDate = (rowCursor.getString(rowCursor.getColumnIndex("todayDate")));
                            oldest = (rowCursor.getString(rowCursor.getColumnIndex("est")));
                            oldTransit = (rowCursor.getString(rowCursor.getColumnIndex("transit")));
                            oldTransitTime = (rowCursor.getString(rowCursor.getColumnIndex("transitTime")));
                            String oldplaceId = (rowCursor.getString(rowCursor.getColumnIndex("placeId")));
                            if (oldplaceId.equals(placeId)) {
                                Cursor locationCursor = locDb.getTodayLocations(dateSelected);
                                if (locationCursor.getCount() == 0) {
                                    updateendtime(context, "Y");
                                    Log.d(TAG, "First Entry for the day. Continue and insert");
                                } else {
                                    Log.d(TAG, "Still in same place, Update last mile again and exit");
                                    updateendtime(context, "N");
                                    return;
                                }
                            } else {
                                Log.d(TAG, "New place found, proceeding");
                            }
                        } else {
                            Log.d(TAG, "sending OTNF");
                            oldUte = "OTNF";
                            oldDate = "NA";
                            oldest = "NA";
                            rowIda = "NA";
                            oldTransit = "NA";
                            oldTransitTime = "NA";
                        }
                    } else {
                        Log.d(TAG, "No Old Row Found");
                    }
                } else {
                    Log.d(TAG, "No Old Row Found");
                }
                //oldUte = locDb.getLastLocationsUte(0);
                rowId = locDb.insertLocation(lat, lng, uts, dateSelected, strAdrP, strAdrFull,geoAd, placeId, fulluts);
                Log.d(TAG, "rowId is " + rowId);
                locDb.close();
            } else {
                Log.d(TAG, "Address not found via Geocoding");
            }
            if (rowId > 0) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                String contactName = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);
                String MobilePhone = sharedPreferences.getString(QuickstartPreferences.PROFILE_PHONE, null);
                Log.d(TAG, "About to send GCM broadcast for the new location");
                sendNotificationNewLoc sendLoc = new sendNotificationNewLoc(context);
                sendLoc.handleNewloc(context, contactName, MobilePhone, dateSelected, fulluts, strAdrP, strAdrFull, lat, lng, oldUte, rowIda, oldTransit, oldTransitTime, oldDate, oldest);
            }
         }
    }
    private Cursor isknownplace(Context context, double lat, double lng) throws IOException {
        PlaceDBHelper placeDb = new PlaceDBHelper(context);
            Cursor placeCursor = placeDb.getCachePlace(lat, lng);
        return placeCursor;

    }
    private void getPlaces(Location currentBestLocation, Context context) {
        CurrentPlace loc = new CurrentPlace(currentBestLocation, context);
        loc.getPlace(currentBestLocation, context);
        //return place;
    }

    public String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        //, Locale);.getDefault());
        StringBuilder strReturnedAddress = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && addresses.get(0) != null) {
                Address returnedAddress = addresses.get(0);
                strReturnedAddress = new StringBuilder();
                Log.d(TAG, "feature name is" + returnedAddress.getFeatureName());
                Log.d(TAG, "feature name is" + returnedAddress.getThoroughfare());
                Log.d(TAG, "feature name is" + returnedAddress.getSubThoroughfare());
                        //strReturnedAddress.append(returnedAddress.getFeatureName());
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                if (!returnedAddress.getFeatureName().isEmpty())
                    strReturnedAddress.append(returnedAddress.getFeatureName()).append(",");
                if (!returnedAddress.getLocality().isEmpty())
                    strReturnedAddress.append(returnedAddress.getLocality()).append(",");
                if (!returnedAddress.getAdminArea().isEmpty())
                    strReturnedAddress.append(returnedAddress.getAdminArea()).append(",");
                if (!returnedAddress.getCountryName().isEmpty())
                    strReturnedAddress.append(returnedAddress.getCountryName()).append(",");
                return strReturnedAddress.toString();
            } else {
                Log.d(TAG, "No Address returned!");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "Canont get Address!");
        }

        return null;
    }
    private String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String selectedDate = dateFormat.format(new Date()); // Find todays date
        return selectedDate;
    }
    private String getTodayTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        //estFormatter.setTimeZone(TimeZone.getTimeZone("EST"));
        String uts = dateFormat.format(new Date()); // Find todays date
        return uts;
    }
    private String getfullTodayTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ZZ");

        //estFormatter.setTimeZone(TimeZone.getTimeZone("EST"));
        String uts = dateFormat.format(new Date()); // Find todays date
        return uts;
    }
    protected boolean isBetterLocation(Context context, Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            Log.d(TAG, "Current Location null");
            // A new location is always better than no location
            return true;
        }
        /*if (location == null) {
            Log.d(TAG, "New Location null");
            // Mo new location
            return false;
        }*/

        // Check whether the new location fix is newer or older
        long timeDelta;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            timeDelta = location.getElapsedRealtimeNanos() - currentBestLocation.getElapsedRealtimeNanos();
            Log.d(TAG, "New Elapsed Time  is " +location.getElapsedRealtimeNanos());
            Log.d(TAG, "Old Elapsed Time  is " +currentBestLocation.getElapsedRealtimeNanos());
        } else {
            timeDelta = location.getTime() - currentBestLocation.getTime();
            Log.d(TAG, "New Time  is " +location.getTime());
            Log.d(TAG, "Old Time  is " +currentBestLocation.getTime());
        }
        boolean vel = location.hasSpeed();
        double distance = getDistance(location.getLatitude(), location.getLongitude(), currentBestLocation.getLatitude(), currentBestLocation.getLongitude(), "K");
        boolean isFirst = getIsFirstCheck(context);
        Log.d(TAG, "Dis is " + distance);
        Log.d(TAG, "isFirst is " + isFirst);

        boolean isSignificantlyNewer = timeDelta > DIFF_MINUTES;
        boolean isSignificantlyOlder = timeDelta < DIFF_MINUTES;
        boolean hasTravelled = distance > DIFF_DIS;
        boolean isNewer = timeDelta > 0;

        Log.d(TAG, "New Lat is " + location.getLatitude());
        Log.d(TAG, "New Lon is " +  location.getLongitude());

        Log.d(TAG, "old Lat is " + currentBestLocation.getLatitude());
        Log.d(TAG, "old Lat is " + currentBestLocation.getLongitude());
                        Log.d(TAG, "hasTravelled is " + hasTravelled);
        Log.d(TAG, "isSignificantlyNewer is " + isSignificantlyNewer);
        Log.d(TAG, "isSignificantlyOlder is " + isSignificantlyOlder);
        Log.d(TAG, "timeDelta is " + timeDelta);

        //If this is the first location of the day
        if (isFirst){
            Log.d(TAG, "First Location of the day");
            return true;

        }
        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        //if (isSignificantlyNewer && hasTravelled) {
        /*if (hasTravelled) {
            Log.d(TAG, "Got a new loc");
            return true;
            // If the new location is more than two minutes older, it must be worse
            //} else if (isSignificantlyOlder) {
        } else {
            return false;
        }*/

        //Check whether the new location fix is more or less accurate
        int accuracyDelta;
        accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean hasSpeed = location.hasSpeed();
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        //Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate && hasTravelled) {
            Log.d(TAG, "More accurate");
            return true;
        } else if (isNewer && hasTravelled && !isSignificantlyLessAccurate) {
            Log.d(TAG, "Not Sigificantly Less accurate but new and moved");
            return true;
        } else if (hasLastLocEndedafterTransit(context)) {
            return true;
        }
        return false;
    }
    private boolean hasLastLocEndedafterTransit(Context context) {
        LocationDBHelper locDb = new LocationDBHelper(context);
        String rowId = null;
        String getlastMile = "";
        String getTransit = "";
        Cursor locationCursor = locDb.getMaxRow();
        if (locationCursor != null) {
            if (locationCursor.moveToFirst()) {
                try {
                    do {
                        rowId = (locationCursor.getString(locationCursor.getColumnIndex("rowId")));
                    } while (locationCursor.moveToNext());
                } finally {
                    if (locationCursor != null && !locationCursor.isClosed())
                        locationCursor.close();
                }
            }
            Cursor rowCursor = locDb.getRowLocations(rowId);
            if (rowCursor != null) {
                try {
                    if (rowCursor.moveToFirst()) {
                        getlastMile = (rowCursor.getString(rowCursor.getColumnIndex("lastMile")));
                        getTransit = (rowCursor.getString(rowCursor.getColumnIndex("transit")));
                        Log.d(TAG, "getlastMile flag is "+ getlastMile);
                        Log.d(TAG, "getTransit flag is "+ getTransit);
                    }
                } finally {
                    if (rowCursor != null && !rowCursor.isClosed())
                        rowCursor.close();
                }
            }
            if (getlastMile.equals("Y") & getTransit != null) {
                Log.d(TAG, "Last loc ended after ended. Must Insert");
                return true;
            }
        }
        return false;
    }
   /* private double getDistance(Location location, Location currentBestLocation) {
        double dis = distanceBetween(location.getLatitude(), location.getLongitude(), currentBestLocation.getLatitude(), currentBestLocation.getLongitude(), "K");
        return dis;
    }*/

    private double getDistance(double lat1, double lon1, double lat2, double lon2, String unit)
    {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        //if (unit == "K") {
            dist = dist * 1.609344;
        //} else if (unit == "M") {
          //  dist = dist * 0.8684;
        //}
        Log.d(TAG, "Distance is: " + dist);
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg)
    {
        return (deg * Math.PI / 180.0);
    }
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad)
    {
        return (rad * 180.0 / Math.PI);
    }
    public boolean getIsFirstCheck(Context context) {
        String dateSelected = getTodayDate();
        LocationDBHelper locDb = new LocationDBHelper(context);
        Cursor locationCursor = locDb.getTodayLocations(dateSelected);
        if (locationCursor.getCount() == 0) {
            locationCursor.close();
            locDb.close();
            return true;
        }
        locationCursor.close();
        locDb.close();
        return false;
    }
    public void requestTransitdata(Context context) {

        Intent intent_act = new Intent(context, TransitionReceiver.class);
        PendingIntent transitionPendingIntent = PendingIntent.getService(context, 100, intent_act, PendingIntent.FLAG_UPDATE_CURRENT);

        transitions = new ArrayList<>();

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());


        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        ActivityTransitionRequest activityTransitionRequest
                = new ActivityTransitionRequest(transitions);

        Task<Void> task = ActivityRecognition.getClient(context).requestActivityTransitionUpdates(activityTransitionRequest,transitionPendingIntent );

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Captured Transit data");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed in Capturing Transit data");
                e.printStackTrace();
            }
        });
    }
    private void stoptransitdata() {
        Task<Void> task = activityRecognitionClient.removeActivityTransitionUpdates(transitionPendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                transitionPendingIntent.cancel();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}
