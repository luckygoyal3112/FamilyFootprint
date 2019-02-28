package com.ack.familyfootprints.trackerTab;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ack.familyfootprints.DBHelper.InviteDBHelper;
import com.ack.familyfootprints.DBHelper.TrackerDBHelper;
import com.ack.familyfootprints.DatePickerFragment;
import com.ack.familyfootprints.GCMClientApp.Globals;
import com.ack.familyfootprints.GCMClientApp.QuickstartPreferences;

import com.ack.familyfootprints.MapsActivity;
import com.ack.familyfootprints.R;
import com.ack.familyfootprints.Users.UsersAdapter;
import com.ack.familyfootprints.model.PingerKeys;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Lucky Goyal on 3/12/2016.
 */


public class trackContacts extends AppCompatActivity
        //implements OnMenuItemClickListener, OnMenuItemLongClickListener
{
    private AdView mAdView;
    AtomicInteger msgId = new AtomicInteger();
    String contactName;
    String mobilePhone;
    String regID;
    private ListView mListView;
    private CursorAdapter mAdapter;
    public static boolean canBack = true;

    TextView dateView;

    public static final String TAG = trackContacts.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap my_btmp = null;
        InputStream photo_stream;
        Bitmap round_btmp = null;
        setContentView(R.layout.activity_tracker_locations);
        //setToolBar();
        Intent intent = getIntent();

        contactName = intent.getStringExtra("contactName");
        mobilePhone = intent.getStringExtra("mobilePhone");
        regID = intent.getStringExtra("regID");
        Log.i(TAG, "contactName is " + contactName);
        Log.i(TAG, "mobilePhone is " + mobilePhone);
        Log.i(TAG, "regId is " + regID);
        long Contact_Id = getContactIDFromNumber(mobilePhone, this);

        ImageView profile = (ImageView) findViewById(R.id.user_profile_photo);
        Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(Contact_Id));
        try {

            //InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), my_contact_Uri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(
                        getContentResolver(), my_contact_Uri, true);
            } else {
                photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(
                        getContentResolver(), my_contact_Uri);
            }
            if (photo_stream != null) {
                BufferedInputStream buf = new BufferedInputStream(photo_stream);
                my_btmp = BitmapFactory.decodeStream(buf);
                round_btmp = getRoundedBitmap(my_btmp);
                profile.setImageBitmap(round_btmp);
                assert photo_stream != null;
                photo_stream.close();
            } else {
                Log.i(TAG, "Cannot Resolve Contact's image");
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_contact_picture_holo_light);
                profile.setImageDrawable(myDrawable);
                //profile.setImageDrawable(ActivityCompat.getDrawable(this, R.drawable.generatedID));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView invite_name = (TextView) findViewById(R.id.user_profile_name);
        invite_name.setText(contactName);

        /*Button image_btn = (Button) findViewById(R.id.image_btn);

        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(mobilePhone);
            }
        });*/
        setToolBar();
        //fragmentManager = getSupportFragmentManager();
        //initMenuFragment();
        //addFragment(new MainFragment(), true, R.id.container)
        //getMapList(todDate, mobilePhone);

        dateView = (TextView) findViewById(R.id.textView1);
        dateView.setText(getTodayDate());

        //FragmentManager manager=this.getSupportFragmentManager();
        ImageButton calButton = (ImageButton) findViewById(R.id.calButton);
        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTripData(view);
                //getMapList(selectedDate, view);
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /*private void selectImage(String mobilePhone) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        ImageView profile = (ImageView) findViewById(R.id.user_profile_photo);
         if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    profile.setImageURI(selectedImage);
                    //saveImageinDB(selectedImage, mobilePhone);
         }
    }*/

    private void setToolBar() {
        Toolbar actionBarToolBar = (Toolbar) findViewById(R.id.ToolBarViewT);
        setSupportActionBar(actionBarToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_triploc, menu);
        return true;
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_triploc, menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                return true;*/
            case R.id.menu_unfriend:
                showUnfriendConfirmDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showUnfriendConfirmDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you really want to Unfriend " + contactName +
                "? By choosing Yes, you wont be able to view each other's Timeline. ");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                sendUnfriendNotification();
            }
        });
        builder.setNegativeButton("No", null).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded(Lat, Lng);
    }

    private void getMapList(String date, final String mobilephone) {
        mListView = (ListView) findViewById(R.id.list_view);
        String transit = "NA";
        TrackerDBHelper locDb = new TrackerDBHelper(getApplicationContext());
        //SQLiteDatabase newDB = locDb.getWritableDatabase();

        Log.i(TAG, "Mobile num is " + mobilephone);
        Log.i(TAG, "Date is " + date);

        // Attach The Data From DataBase Into ListView Using Cursor Adapter
        Cursor locationCursor = locDb.getTodayLocations(date, mobilephone);

        if (locationCursor != null) {
            Log.i(TAG, "got loc");

            if (locationCursor.moveToFirst()) {
                do {
                  /*  String Id = locationCursor.getString(locationCursor
                            .getColumnIndex("FIELD_ROW_ID"));*/
                    String

                            dateSelected = locationCursor.getString(locationCursor.getColumnIndex("todayDate"));
                    String est = locationCursor.getString(locationCursor.getColumnIndex("est"));
                    String eet = locationCursor.getString(locationCursor.getColumnIndex("eet"));
                    String strAdrP = locationCursor.getString(locationCursor.getColumnIndex("placeName"));
                    String strAdrFull = locationCursor.getString(locationCursor.getColumnIndex("placeAddress"));
                    transit = locationCursor.getString(locationCursor.getColumnIndex("transit"));
                    String transitTime = locationCursor.getString(locationCursor.getColumnIndex("transitTime"));
                    Log.i(TAG, "Date  is " + dateSelected);

                    Log.i(TAG, "EST is " + est);
                    Log.i(TAG, "EET is " + eet);
                    Log.i(TAG, "Name is " + strAdrP);
                    Log.i(TAG, "address is " + strAdrFull);
                } while (locationCursor.moveToNext());
            }
            Log.i(TAG, "No More Location Now");
        } else {
            Toast.makeText(

                    this, "No Location available for the day!",

                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No Location for today");
           /* mAdapter = new SimpleCursorAdapter(getBaseContext(),
                    R.layout.list_view_layout,
                    locationCursor,
                    new String[]{"placeName", "address", "est", "eet"},
                    new int[]{R.id.place, R.id.address, R.id.est, R.id.eet}, 0);*/
            // mListView.setAdapter(mAdapter);
            //return;
        }
    mAdapter = new MyCustomTrackAdapter(getBaseContext(),locationCursor,0 );
        /*if (transit != null) {
            if (transit.equals("NA")) {
                mAdapter = new SimpleCursorAdapter(getBaseContext(),
                        R.layout.list_view_layout,
                        locationCursor,
                        new String[]{"placeName", "placeAddress", "est", "eet"},
                        new int[]{R.id.place, R.id.address, R.id.est, R.id.eet}, 0);
            } else {
                mAdapter = new SimpleCursorAdapter(getBaseContext(),
                        R.layout.list_view_layout,
                        locationCursor,
                        new String[]{"placeName", "address", "est", "eet", "transit", "transitTime"},
                        new int[]{R.id.place, R.id.address, R.id.est, R.id.eet, R.id.transit, R.id.transitTime}, 0);
            }
        }*/
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                //String selected = mListView.getItem(position);

                //String text1Value = (String) ((TextView)parent.getItemAtPosition(position)).getText();
                //TableRow tableRow = (TableRow) (mListView.getItemAtPosition(position));
                //String id = String.valueOf(item.getId());
                //Bitmap image = id item.getImg();

                Log.i(TAG, "Selected position" + position);
                //Log.i(TAG, "Selected  " + text1Value);
                Log.i(TAG, "Selected Row Data for " + arg3);
                TrackerDBHelper rowDb = new TrackerDBHelper(getApplicationContext());
                Cursor locationCursor = rowDb.getRowLocations(String.valueOf(arg3));
                double Lng = 0;
                double Lat = 0;
                String adr = null;
                String dateSel = null;

                if (locationCursor != null) {
                    if (locationCursor.moveToFirst()) {
                        Lat = locationCursor.getDouble(locationCursor.getColumnIndex("lat"));
                        Lng = locationCursor.getDouble(locationCursor.getColumnIndex("lng"));
                        dateSel = locationCursor.getString(locationCursor.getColumnIndex("todayDate"));
                        adr = locationCursor.getString(locationCursor.getColumnIndex("placeAddress"));
                        Log.i(TAG, "Lat is " + Lat);
                        Log.i(TAG, "Lng is " + Lng);
                        Log.i(TAG, "Uts is " + dateSel);
                        Log.i(TAG, "address is " + adr);
                    }
                }

                Intent intent = new Intent(getApplicationContext(), trackMapsActivityIntent.class);
                intent.putExtra("Lat", Lat);
                intent.putExtra("Lng", Lng);
                intent.putExtra("Adr", adr);
                intent.putExtra("dateSel", dateSel);
                intent.putExtra("contactMobile", mobilephone);
                intent.putExtra("contactName", contactName);
                startActivity(intent);
            }
        });
        // set selected date into datepicker also
        // picker.init(year, month + 1, day, null);i.putExtra("someKey", item);
        //return selecteddate;
    }

    private String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String selectedDate = dateFormat.format(new Date()); // Find todays date
        return selectedDate;
    }

    public void getTripData(View view) {
      /*  //setCurrentDateOnPicker(view);
        DialogFragment newFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("dateAsText",dateView.getText().toString());
        newFragment.setArguments(bundle);
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
    }
    private void showDatePicker() {*/
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(this.getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            String day = String.format("%02d", dayOfMonth);
            String month = String.format("%02d", monthOfYear + 1);

            String selectedDate = String.valueOf((new StringBuilder()
                    .append(day).append("/")
                    .append(month).append("/").append(year)));

            Log.i(TAG, "Date is " + selectedDate);

            try {
                dateView.setText(getPickerDate(selectedDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
           /* Toast.makeText(
                    getActivity(),selectedDate,
                    //String.valueOf(year) + "-" + String.valueOf(monthOfYear)
                      //      + "-" + String.valueOf(dayOfMonth),
                    Toast.LENGTH_LONG).show();*/
            getMapList(selectedDate, mobilePhone);
        }
    };

    private String getPickerDate(String dateString) throws ParseException {
        //String dateString = "Fri, 19 Jul 2013 01:30:22 GMT";
        SimpleDateFormat simple = new SimpleDateFormat("MMM dd, yyyy");
        Date date;
        //try {
        date = new SimpleDateFormat("dd/MM/yyy")
                .parse(dateString);
        Log.e("result", "Date:" + simple.format(date));
        String selectedDate = simple.format(date);
        return selectedDate;

    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static long getContactIDFromNumber(String contactNumber, Context context) {
        String UriContactNumber = Uri.encode(contactNumber);
        long phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, UriContactNumber),
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getLong(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();

        return phoneContactID;
    }

    private void sendUnfriendNotification() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.w(TAG, "Sending FootPrints Unfriend Notification to " + contactName + " from: " + token);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String profileName = sharedPreferences.getString(QuickstartPreferences.PROFILE_NAME, null);
        String profilePhone = sharedPreferences.getString(QuickstartPreferences.PROFILE_PHONE, null);
        String unfriend_uts = getfullTodayTime();

        FirebaseMessaging.getInstance().send(
                new RemoteMessage.Builder(Globals.GCM_SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId(Integer.toString(msgId.incrementAndGet()))
                        .addData(PingerKeys.ACTION, Globals.UNFRIEND_NOTIFICATION)
                        .addData(PingerKeys.SENDERREGID, FirebaseInstanceId.getInstance().getToken())
                        .addData(PingerKeys.SENDERNAME, profileName)
                        .addData(PingerKeys.SENDERPHONE, profilePhone)
                        .addData(PingerKeys.INVITEDREGID, regID)
                        .addData(PingerKeys.INVITEDNAME, contactName)
                        .addData(PingerKeys.INVITEDPHONE, mobilePhone)
                        .addData(PingerKeys.UNFRIEND_UTS, unfriend_uts)
                        .build());

        Toast.makeText(
                this, "FootPrints Unfriend Request sent to." + contactName,
                Toast.LENGTH_SHORT).show();
        unfriendLocal();
        //AnalyticsHelper.send(context, TrackingEvent.PING_SENT);

    }

    private void unfriendLocal() {
        //Unfriend invite & update user known table with ID as invStatus
        UsersAdapter cUserAdapter = new UsersAdapter(this);
        cUserAdapter.updatePingerStatus(mobilePhone, "SUF");
        cUserAdapter.UpdateInvitedPinger(mobilePhone, "SUF");

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        trackContacts.this.finish();
    }

    private String getfullTodayTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ZZ");

        //estFormatter.setTimeZone(TimeZone.getTimeZone("EST"));
        String uts = dateFormat.format(new Date()); // Find todays date
        return uts;
    }

    private class MyCustomTrackAdapter extends CursorAdapter {

        private ArrayList mData = new ArrayList();
        private LayoutInflater mInflater;

        public MyCustomTrackAdapter(Context context, Cursor cursor, int flags) {
            //super();
            //super();
            super(context, cursor, 0);
            mInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        private int getItemViewType(Cursor cursor) {
            String type = cursor.getString(cursor.getColumnIndex("transit"));
            if (type.equals("NA")) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Cursor cursor = (Cursor) getItem(position);
            return getItemViewType(cursor);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView place = (TextView) view.findViewById(R.id.place);
            String placeName = cursor.getString( cursor.getColumnIndex("placeName"));
            place.setText(placeName);

            TextView address = (TextView) view.findViewById(R.id.address);
            String address_data = cursor.getString( cursor.getColumnIndex("placeAddress"));
            address.setText(address_data);

            TextView est = (TextView) view.findViewById(R.id.est);
            String est_data = cursor.getString( cursor.getColumnIndex("est"));
            est.setText(est_data);

            if (cursor.getString(cursor.getColumnIndex("transit")).equals("NA")) {
                Log.w(TAG, "Row for " + placeName + " doesnt has Transit data");
                TextView eet = (TextView) view.findViewById(R.id.eet);
                String eet_data = cursor.getString(cursor.getColumnIndex("eet"));
                if (eet_data != null)
                    eet.setText(eet_data);
                else {
                    eet.setText("Now");
                    eet.setTextColor(getResources().getColor(R.color.blue));
                }
            }else {
                    TextView eet = (TextView) view.findViewById(R.id.eet);
                    String eet_data = cursor.getString( cursor.getColumnIndex("eet"));
                        eet.setText(eet_data);
                    TextView transit = (TextView) view.findViewById(R.id.transit);
                    String transit_data = cursor.getString(cursor.getColumnIndex("transit"));
                    transit.setText(transit_data);

                    TextView transitTime = (TextView) view.findViewById(R.id.transitTime);
                    String transitTime_data = cursor.getString(cursor.getColumnIndex("transitTime"));
                    transitTime.setText(transitTime_data);
                    Log.w(TAG, "Binding data for " + placeName + transit_data + transitTime_data);
            }
        }  
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            View v = null;


            if (cursor.getString(cursor.getColumnIndex("transit")).equals("NA")) {
                v = mInflater.inflate(R.layout.list_view_layout_na, parent, false);
            } else {
                v = mInflater.inflate(R.layout.list_view_layout, parent, false);
            }
            v.setTag(holder);
            return v;
        }                                                                                                                                                          }

    public static class ViewHolder {
        public TextView textView;
    }

}