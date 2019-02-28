package com.ack.familyfootprints.timelineTab;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;

import com.ack.familyfootprints.DBHelper.LocationDBHelper;
import com.ack.familyfootprints.GCMClientApp.QuickstartPreferences;
import com.ack.familyfootprints.MapsActivity;
import com.ack.familyfootprints.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class MapsActivityIntent extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public static boolean canBack = true;
    private String shareString;
    public static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity_intent);
        Intent intent = getIntent();
        ArrayList<LatLng> markerPoints;
        markerPoints = new ArrayList<LatLng>();
        if(markerPoints.size()>1){
            markerPoints.clear();
        }
        //Double Lat = Double.parseDouble(intent.getDoubleExtra("Lat"));

        Double Lat = intent.getDoubleExtra("Lat", 0.0);
        Double Lng = intent.getDoubleExtra("Lng", 0.0);
        Log.i(TAG, "Lat is " + Lat);
        Log.i(TAG, "Lng is " + Lng);
        String adr = intent.getStringExtra("Adr");
        String est = intent.getStringExtra("est");
        String eet = intent.getStringExtra("eet");
        String dateSel  = intent.getStringExtra("dateSel");
        //markerPoints = 'n
        // (dateSel);
        setUpMapIfNeeded(Lat, Lng);
        //setToolBar();
        /*Button addComm = (Button) findViewById(R.id.comment);
        addComm.setOnClickListener(new OnClickListener() {
            @Override

            public void onClick(View view) {
                addComment();
            }
        });*/
        final String text = getShareString(adr, dateSel, est, eet);
        Log.i(TAG, "text is " + text);
        Button btnCap = (Button) findViewById(R.id.share);
        btnCap.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {


                    CaptureMapScreen(text);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
              }

            }
        });
        /*Button share = (Button) findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callShare("Kharghar, Navi Mumbai India at 14:02");
                 takeScreenshot();

                //,mpath);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded(Lat, Lng);
    }

    private void addComment() {
        Toast.makeText(MapsActivityIntent.this, "Add Detail is Selected", Toast.LENGTH_SHORT).show();

    }

    private void callShare(File imageFile) {
        //}, String path) {
        Log.i(TAG, "Inside callshare");
        ////Toast.makeText(MapsActivityIntent.this, "Share is Selected", Toast.LENGTH_SHORT).show();
        Intent share_intent = new Intent(Intent.ACTION_SEND);

        /*share_intent.setType("image/jpeg");
        share_intent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        Uri myUri = Uri.parse("file://" + path);*/
        Uri uri = Uri.fromFile(imageFile);
        share_intent.setDataAndType(uri, "image/*");
        share_intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(share_intent);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and22
     * {@link com.googcallle.android.gms.maps.MaView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we
     * <p>
     * should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     * method in {@link #onResume()} to guarantee that it will be called.
     *
     * @param Lat
     * @param Lng
     */
    private void setUpMapIfNeeded(Double Lat, Double Lng) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);



            /* Check if we were successful in obtaining the map.
            if (mMap != null) {
                Log.i(TAG, Setting up Map ");

                setUpMap(Lat, Lng);
            } else {
                Log.i(TAG, "Null Map ");
                Toast.makeText(this,
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }*/
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(Double Lat, Double Lng) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(Lat, Lng)).title("My Marker"));
        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.target(new LatLng(Lat, Lng));
        builder.zoom(16.0f);
        CameraPosition cameraPosition = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);

    }
    private void setUpMapvoid() {

        ArrayList<LatLng> markerPoints ;
        // Initializing
        markerPoints = new ArrayList<LatLng>();
        // Already two locations
        if(markerPoints.size()>1){
            markerPoints.clear();
        }


        Intent intent = getIntent();

         Double LatCur = intent.getDoubleExtra("Lat", 0.0);
         Double LngCur = intent.getDoubleExtra("Lng", 0.0);
         String dateSel = intent.getStringExtra("dateSel");
        // Creating MarkerOptions
        //MarkerOptions options = new MarkerOptions();
        Log.i(TAG, "datesel  is " + dateSel);
        LocationDBHelper locDb = new LocationDBHelper(this);
        Cursor locationCursor = locDb.getTodayLocations(dateSel);

        Log.i(TAG, "get COunt");
        int locRow = locationCursor.getCount();
        if (locRow > 0) {
            if (locationCursor.moveToFirst()) {
                do {
                    Double Lat =  (locationCursor.getDouble(locationCursor.getColumnIndex("lat")));
                    Double Lng =  (locationCursor.getDouble(locationCursor.getColumnIndex("lng")));
                    Double est =  (locationCursor.getDouble(locationCursor.getColumnIndex("est")));
                    Log.i(TAG, "Lat is " + Lat);
                    Log.i(TAG, "Lng is " + Lng);
                    LatLng newLatLng = new LatLng(Lat, Lng);
                    markerPoints.add(newLatLng);
                    //options.position(newLatLng);
                    mMap.addMarker(new MarkerOptions().position(newLatLng).title(String.valueOf(est)));
                    //mMap.addMarker(options);
                } while (locationCursor.moveToNext());
            }
            Log.i(TAG, "No More Location Now");
        }
       // mMap.addMarker(options);
        //mMap.add//Marker(new MarkerOptions().position(new LatLng(Lat, Lng)).title("My Marker"));
        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.target(new LatLng(LatCur, LngCur));
        //builder.zoom(12.0f);
        CameraPosition cameraPosition = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_map_intent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

   /* private void setToolBar() {
        Toolbar actionBarToolBar = (Toolbar) findViewById(R.id.ToolBarView);
        setSupportActionBar(actionBarToolBar);
        //actionBarToolBar.setNavigationIcon(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
        getSupportActionBar().setHomeButtonEnabled(canBack);
        getSupportActionBar().setTitle("Location Details");
        //actionBarPrivate void setSupportActionBar(Toolbar ) {
        //actionBarToolBar.setNavigationContentDescription(getResources().getString(R.string.nav_desc));
        //actionBarToolBar.setBackground(R.drawable.plane);

        //actionBarToolBar.setLogo(R.drawable.logo);
        actionBarToolBar.setLogoDescription("Location Details");
    }*/

    public String getShareString(String adr, String dateSel, String est, String eet) {

        StringBuilder builder = new StringBuilder();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String user_name = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);

        if (user_name != null) {
            Log.d(TAG, "User name is" + user_name);

        }
        builder.append(user_name);
        builder.append(" was at ");
        builder.append(adr);
        if (dateSel != null) {
            builder.append(" on ");
            builder.append(dateSel);
        }
        if (est != null) {
            builder.append(" from ");
            builder.append(est);
        }
        if (eet != null) {
            builder.append(" till ");
            builder.append(eet);
        }
        return builder.toString();
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
            Log.i(TAG, "mpath is " + mPath);
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);

            Bitmap bitmap = Bitmap.createBitmap(v1.getWidth(),
                    v1.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            v1.draw(canvas);

            //Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            //v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            //return mPath;
            callShare(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
        //return mPath;
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public void CaptureMapScreen(final String text) {
        SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {// TODO Auto-generated method stub
                bitmap = snapshot;

                OutputStream fout = null;

                String filePath = System.currentTimeMillis() + ".jpeg";

                try {
                    fout = openFileOutput(filePath,
                            MODE_WORLD_READABLE);

                    // Write the string to the file
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
                    fout.flush();
                    fout.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    Log.d("ImageCapture", "FileNotFoundException");
                    Log.d("ImageCapture", e.getMessage());
                    filePath = "";
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d("ImageCapture", "IOException");
                    Log.d("ImageCapture", e.getMessage());
                    filePath = "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //openShareImageDialog(filePath);
                openShareImageDialog(filePath, text);
            }
        };
        mMap.snapshot(callback);
        // myMap is object of GoogleMap +> GoogleMap myMap;
        // which is initialized in onCreate() =>
        // myMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_pass_home_call)).getMap();
    }

    public void openShareImageDialog(String filePath, String text) {
        File file = this.getFileStreamPath(filePath);

        if (!filePath.equals("")) {
            Log.i(TAG, "Inside callshare");
            //Toast.makeText(MapsActivityIntent.this, "Share is Selected", Toast.LENGTH_SHORT).show();
            final ContentValues values = new ContentValues(2);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            final Uri contentUriFile = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, contentUriFile);


            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, text);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(intent, "Share Image"));
        } else {
            //This is a custom class I use to show dialogs...simply replace this with whatever you want to show an error message, Toast, etc.
            //Log.i(TAG, "Inside callshare");
            Toast.makeText(MapsActivityIntent.this, "Unable to share", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // Enabling MyLocation Layer of Google Map

        googleMap.setMyLocationEnabled(true);

        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            Log.i(TAG, "Setting up Map void");

            setUpMapvoid();
        } else {
            Log.i(TAG, "Null Map in onmapready");

        }

    }
    public ArrayList setupMarkerPoints(String selDate) {
        ArrayList<LatLng> markerPoints = null;

        LocationDBHelper locDb = new LocationDBHelper(this);
        Cursor locationCursor = locDb.getTodayLocations(selDate);

        int locRow = locationCursor.getCount();
        if (locRow > 0) {
            if (locationCursor.moveToFirst()) {
                try {
                    do {
                        int Lat = (int) (locationCursor.getDouble(locationCursor.getColumnIndex("lat")));
                        int Lng = (int) (locationCursor.getDouble(locationCursor.getColumnIndex("lng")));
                        LatLng newLatLng = new LatLng(Lat, Lng);
                        markerPoints.add(newLatLng);
                        Log.i(TAG, "Lat is " + Lat);
                        Log.i(TAG, "Lng is " + Lng);
                    } while (locationCursor.moveToNext());
                } finally {
                    if (locationCursor != null && !locationCursor.isClosed())
                        locationCursor.close();
                }
            }
            Log.i(TAG, "No More Location Now");
        }
        return markerPoints;
    }
}
