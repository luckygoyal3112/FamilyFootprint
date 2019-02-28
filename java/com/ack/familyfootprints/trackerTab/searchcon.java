package com.ack.familyfootprints.trackerTab;


import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.ack.familyfootprints.DBHelper.GroupDBHelper;
import com.ack.familyfootprints.ImageLoader;
import com.ack.familyfootprints.R;
import com.ack.familyfootprints.SendInvite;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class searchcon extends AppCompatActivity {
    public static boolean canBack = true;
    private ListAdapter mAdapter;
    ListView mListView;
    private static final String TAG = searchcon.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    private ImageView mImageView;
    private ImageLoader mImageLoader; // Handles loading the contact image in a background thread
    ArrayAdapter<String> adapter;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Phone = "phoneKey";
    public static final String Email = "emailKey";
    SharedPreferences sharedpreferences;

    ArrayList<HashMap<String, String>> listItems;

    private List<Map<String, Object>> Data;
    private ListView listView = null;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final String group_get = intent.getStringExtra("group");
        setContentView(R.layout.activity_searchcon);
        setToolBar();
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);

        CardView addContact = (CardView) findViewById(R.id.addMoreContacts);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "Sending Invites2");
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            }
        });
        CardView Next = (CardView) findViewById(R.id.Next);
        Next.setEnabled(false);
    }
    private void setToolBar() {
        Toolbar actionBarToolBar = (Toolbar) findViewById(R.id.ToolBarView);
        setSupportActionBar(actionBarToolBar);
        //actionBarToolBar.setNavigationIcon(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
        getSupportActionBar().setHomeButtonEnabled(canBack);
        //actionBarToolBar.setLogo(R.drawable.logo);
        //actionBarTool
        // Bar.setLogoDescription(getResources().getString(R.string.logo_desc));
    }
    private void sendInvite(String Name, String Phone) {
        //Log.d(TAG, "Sending Invites 3");

        Intent intent = new Intent(this,SendInvite.class);
        intent.putExtra("Name", Name);
        intent.putExtra("Phone", Phone);

        startActivity(intent);
    }

    private long insertContact(String Name, String Phone) {
        GroupDBHelper groupDb = new GroupDBHelper(getBaseContext());
        String uts = getTodayTime();
        long rowId = 0;

        rowId = groupDb.insertContact(null, Name, Phone, null,"Awaiting Acceptance", uts, uts);
     //   Log.d(TAG, "Contact rowId is " + rowId);
        return rowId;
    }
    private String getTodayTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String uts = dateFormat.format(new Date()); // Find todays date
        return uts;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            String Name = retrieveContactName();
            Log.d(TAG, "Name: " + Name);
            String Phone = retrieveContactNumber();
            Bitmap pic = retrieveContactPhoto();
            Log.d(TAG, "Name: " + Name);
            long rowid = 0;
            if (Phone != null) {
                rowid = insertContact(Name, Phone);
                sendInvite(Name, Phone);
                if (rowid > 0) {
                    populateList();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Cannot add as Contact does not has a Valid Phone.", Toast.LENGTH_SHORT).show();
            }
           /* List<HashMap<String, String>> list= new ArrayList<HashMap<String,String>>();

            ListView listView = (ListView) findViewById(R.id.list_con);
            listItems = new ArrayList<>();
            //listItems.add(Name);
            listItems.add(putData(Name,Phone));
           // adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, listItems);
            SimpleAdapter mAdapter =  new SimpleAdapter(this, listItems,
                    R.layout.list_view_contact,
                    //android.R.layout.simple_list_item_2,
                    new String[]{"name"},
                    new int[]{ R.id.contact_name});
            listView.setAdapter(mAdapter);
            //mAdapter.notifyDataSetChanged();*/
        }
    }

    private void populateList() {
        Log.d(TAG, "Populate List");
        GroupDBHelper groupDb = new GroupDBHelper(this);
        Cursor groupCursor = groupDb.getTobeInvitedContacts();
        int row = groupCursor.getCount();
        if (row > 0 ) {
                if (groupCursor.moveToFirst()) {
                    do {
                  /*  String Id = locationCursor.getString(locationCursor
                            .getColumnIndex("FIELD_ROW_ID"));*/
                        String contactName = groupCursor.getString(groupCursor.getColumnIndex("contactName"));
                        String contactNumber =  groupCursor.getString(groupCursor.getColumnIndex("contactNumber"));

                        //setMapRoute(locationCursor.getDouble(locationCursor.getColumnIndex("lat")),
                        //locationCursor.getDouble(locationCursor.getColumnIndex("lng")), uts);
                        //results.add("LatL " + Lat + ",Long: " + Lng);
                    }while (groupCursor.moveToNext());
                }
            Log.i(TAG, "No More Groups Now");
        }
        if (row > 0 ) {
            mAdapter = new SimpleCursorAdapter(this,
                    R.layout.list_view_contact,
                    //android.R.layout.simple_list_item_1,
                    groupCursor,
                    new String[]{"contactName"},
                    new int[]{R.id.contact_name}, 0);

            /*mAdapter = new CursorRecyclerViewAdapter(getActivity(), tripCursor) {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return null;
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {

                }
                ListView  mListView = (ListView)q getView().findViewById(R.id.list_view);
            mRecyclerView.setAdapter(mAdapter);

            mListView = (ListView) findViewById(R.id.list_con);
            mListView.setAdapter(mAdapter);
            CardView Next = (CardView) findViewById(R.id.Next);
            Next.setEnabled(true);
            Next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendInvite();
                }
            });
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
                    GroupDBHelper rowDb = new GroupDBHelper(this;
                    Cursor groupCursor = rowDb.getRowGroups(arg3);
                    String esd = null;
                    String eed = null;
                    if (groupCursor != null) {
                        if (groupCursor.moveToFirst()) {
                            String name = groupCursor.getString(groupCursor.getColumnIndex("contactName"));
                            String phone = groupCursor.getString(groupCursor.getColumnIndex("contactNumber"));
                            Log.i(TAG, "Name is " + name);
                            Log.i(TAG, "phone is " + phone);
                        }
                    }
                    Intent intent = new Intent(this, groupTrack.class);
                    intent.putExtra("_id", esd);
                    intent.putExtra("groupName", eed);
                    startActivity(intent);
                }
            });*/
        }

    }

    private HashMap<String, String> putData(String name, String phone) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("name", name);
        item.put("phone", phone);
        return item;
    }
    private Bitmap retrieveContactPhoto() {
        Bitmap photo = null;
        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                photo =  BitmapFactory.decodeStream(inputStream);
                //ImageView imageView = (ImageView) findViewById(R.id.img_contact);
                ////imageView.setImageBitmap(photo);
                assert inputStream != null;
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }

    private String retrieveContactNumber() {
        String contactNumber = null;
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorID.close();
        Log.d(TAG, "Contact ID: " + contactID);
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{contactID},
                null);
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        Log.d(TAG, "Contact Phone Number: " + contactNumber);
        // myAwesomeTextView = (TextView)findViewById(R.id.contact_phone);
        //myAwesomeTextView.setText(contactNumber);
        return contactNumber;
    }
    private String retrieveContactName() {
        String contactName = null;
        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        //TextView myAwesomeTextView = (TextView)findViewById(R.id.contact_name);
        //myAwesomeTextView.setText(contactName);
        return contactName;
    }
    /*
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(1,1,intent);
    }
    protected void handleIntent(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            String Name = retrieveContactName();
            Log.d(TAG, "Name: " + Name);
            String Phone = retrieveContactNumber();
            Bitmap pic = retrieveContactPhoto();
            Log.d(TAG, "Name: " + Name);
            List<HashMap<String, String>> list= new ArrayList<HashMap<String,String>>();

            ListView listView = (ListView) findViewById(R.id.list_con);
            listItems = new ArrayList<>();
            //listItems.add(Name);
            listItems.add(putData(Name,Phone));
            // adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, listItems);
            SimpleAdapter mAdapter =  new SimpleAdapter(this, listItems,
                    R.layout.list_view_contact,
                    //android.R.layout.simple_list_item_2,
                    new String[]{"name"},
                    new int[]{ R.id.contact_name});
            listView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }*/
}
