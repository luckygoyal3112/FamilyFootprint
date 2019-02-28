package com.ack.familyfootprints.trackerTab;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ack.familyfootprints.DBHelper.InviteDBHelper;
import com.ack.familyfootprints.GCMClientApp.GcmAction;
import com.ack.familyfootprints.GCMClientApp.Globals;
import com.ack.familyfootprints.R;
import com.ack.familyfootprints.Users.UsersAdapter;
import com.ack.familyfootprints.model.Pinger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import static com.ack.familyfootprints.trackerTab.trackContacts.getRoundedBitmap;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link TrackerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrackerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrackerFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener, AbsListView.OnItemClickListener{

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_INVITE = 0;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "TrackerFragment";
        //private static final int RESULT_OK = 1 ;
    private ListAdapter mAdapter;

    private SharedPreferences mDefaultSharedPreferences;
    private ListView mListView;

    //DatePicker picker;
    TextView dateView;
    String selectedDate;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrackerFragment newInstance(String param1, String param2) {
        TrackerFragment fragment = new TrackerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public TrackerFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        InputStream photo_stream;
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);


       // mRegistrationBroadcastReceiver = new GCMMessageBroadcastReceiver();
    }
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity())

                .unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(GcmAction.REGISTRATION_COMPLETE);
        filter.addAction(GcmAction.SEND_CLIENT_LIST);
        filter.addAction(GcmAction.BROADCAST_NEW_CLIENT);
        filter.addAction(GcmAction.PING_CLIENT);
        filter.addAction(GcmAction.INVITE_REQUEST);
        filter.addAction(GcmAction.REQUEST_CLIENT_LIST);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mRegistrationBroadcastReceiver, filter);
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layyout for this fragment

        View view = inflater.inflate(R.layout.fragment_track, container, false);

        //Find the +1 button
        Log.d(TAG, "Get Groups");
        mListView = (ListView) view.findViewById(R.id.list_view);
        getGroupList();
        FloatingActionButton btnFab = (FloatingActionButton) view.findViewById(R.id.btnFloatingAction_g);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPingerContacts();
            }
        });

        return view;
    }

    private void addPingerContacts() {
        Intent intent = new Intent(getActivity(), contactIntent.class);
        startActivity(intent);
    }

    private Fragment getCurrentFragment() {
        return this.getFragmentManager().findFragmentById(R.id.fragment);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override

    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    private void getGroupList() {
        Log.d(TAG, "Inside Get  group new");
        InviteDBHelper inviteDb = new InviteDBHelper(getActivity());
        Cursor inviteCursor = inviteDb.getAllAcceptedInvites();
        int row = inviteCursor.getCount();
        if (row > 0 ) {
                if (inviteCursor.moveToFirst()) {
                    do {
                        String name = inviteCursor.getString(inviteCursor.getColumnIndex("name"));
                        String phone = inviteCursor.getString(inviteCursor.getColumnIndex("phone"));
                        String regID = inviteCursor.getString(inviteCursor.getColumnIndex("regID"));
                        Log.i(TAG, "name is " + name);
                        Log.i(TAG, "phone is " + phone);
                        Log.i(TAG, "regID is " + regID);
                        //displayPhoto(phone);
                    } while (inviteCursor.moveToNext());
                }
            Log.i(TAG, "No More Invites Now");
        } else {
            Toast.makeText(
                    getActivity(), "You Got to Invite Contacts!!.",
                    Toast.LENGTH_LONG).show();
            Log.i(TAG, "No Contacts Invited");
        }
        if (row > 0 ) {
            /*mAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.list_view_contact,
                    //android.R.layout.simple_list_item_1,
                    inviteCursor,
                    new String[]{"name","img_contact"},

                    new int[]{R.id.contact_name, R.id.img_contact}, 0);

            mListView.setAdapter(mAdapter);*/
            mAdapter = new MyCustomConAdapter(getContext(),inviteCursor,0 );
            mListView.setAdapter(mAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                    Log.d(TAG, "Selected position for Tracker" + position);
                    //Log.i(TAG, "Selected  " + text1Value);
                    Log.d(TAG, "Selected Row Data for " + arg3);
                    InviteDBHelper inviteDb = new InviteDBHelper(getActivity());
                    Cursor groupCursor = inviteDb.getRowContacts(arg3);
                    String contactName = null;
                    String mobilePhone = null;
                    String regID = null;
                    if (groupCursor != null) {
                        if (groupCursor.moveToFirst()) {
                             contactName =  groupCursor.getString(groupCursor.getColumnIndex("name"));
                            mobilePhone =  groupCursor.getString(groupCursor.getColumnIndex("phone"));
                            regID = groupCursor.getString(groupCursor.getColumnIndex("regID"));
                            Log.d(TAG, "contactName is " + contactName);
                            Log.d(TAG, "mobilePhone is " + mobilePhone);
                            Log.d(TAG, "regId is " + regID);
                        }
                    }
                    Intent intent = new Intent(getActivity(), trackContacts.class);
                    intent.putExtra("contactName", contactName);
                    intent.putExtra("mobilePhone", mobilePhone);
                    intent.putExtra("regID", regID);
                    startActivity(intent);
                }
            });
        }
        // set selected date into datepicker also
        // picker.init(year, month + 1, day, null);i.putExtra("someKey", item);
        //return selecteddate;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK){
            String result=data.getStringExtra("result");
            getGroupList();
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }//onActivityResult

    public class GCMMessageBroadcastReceiver  extends BroadcastReceiver {
        private static final String TAG = "GCMMsgBroadcastReceiver";
        UsersAdapter cUserAdapter = new UsersAdapter (getActivity());

        @Override
        public void onReceive(Context context, Intent intent) {
            mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            switch (action) {
                case GcmAction.REGISTRATION_COMPLETE:
                    handleRegistrationComplete(context);
                    break;
                case GcmAction.BROADCAST_NEW_CLIENT:
                    Pinger pinger = intent.getParcelableExtra(Globals.NEW_PINGER);
                    String selfRegToken =
                            mDefaultSharedPreferences.getString(GcmAction.TOKEN, null);
                    if (selfRegToken != null &&
                            !selfRegToken.equals(pinger.getRegistrationToken())) {
                        cUserAdapter.addPinger(pinger);
                    }
                    break;
                /*case GcmAction.PING_CLIENT:
                    Ping ping = intent.getParcelableExtra(Globals.NEW_PING);
                    mPingerAdapter.moveToTop(ping.getFrom());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                            R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("FriendlyPing!");
                    builder.setMessage(ping.getBody());
                    builder.show();*/
            }
        }

        private void handleRegistrationComplete(Context context) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            boolean tokenHasBeenSent = sharedPreferences
                    .getBoolean(GcmAction.SENT_TOKEN_TO_SERVER, false);
            if (tokenHasBeenSent) {
                Log.d(TAG, "onReceive: Token has been sent");
            } else {
                Log.e(TAG, "onReceive: Couldn't send token");
            }
        }
    }
    private class MyCustomConAdapter extends CursorAdapter {

        private ArrayList mData = new ArrayList();
        private LayoutInflater mInflater;

        public MyCustomConAdapter(Context context, Cursor cursor, int flags) {

            //super();
            //super();
            super(context, cursor, 0);
            mInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Bitmap my_btmp = null;
            InputStream photo_stream;
            Bitmap round_btmp = null;
            TextView contactName = (TextView) view.findViewById(R.id.contact_name);
            String contact_name = cursor.getString( cursor.getColumnIndex("name"));
            contactName.setText(contact_name);
            String contact_phone = cursor.getString( cursor.getColumnIndex("phone"));
            long Contact_Id = getContactIDFromNumber(contact_phone, context);
            ImageView profile = (ImageView) view.findViewById(R.id.imageView1);
            ContentResolver cr = context.getContentResolver();
            Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(Contact_Id));
            try {
                //InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), my_contact_Uri);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(
                            cr, my_contact_Uri, true);
                } else {
                    photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(
                            cr, my_contact_Uri);
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
            } }
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = null;
            v = mInflater.inflate(R.layout.list_view_contact, parent, false);
            return v;
        }                                                                                                                                                          }

    public static class ViewHolder {
        public TextView textView;
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
}
