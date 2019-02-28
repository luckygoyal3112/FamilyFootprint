package com.ack.familyfootprints.notificationTab;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
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
import com.ack.familyfootprints.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import static com.ack.familyfootprints.trackerTab.trackContacts.getRoundedBitmap;

//import com.google.android.gms.plus.PlusOneButton;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link NotificationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment implements AbsListView.OnItemClickListener{
    //private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    //private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // The URL to +1.  Must be a valid URL.
   // private final String PLUS_ONE_URL = "http://developer.android.com";

    // The request code must be 0 or greater.

    //private PlusOneButton mPlusOneButton;

    private OnFragmentInteractionListener mListener;

    //SimpleCursorAdapter mAdapter;
    ListView mListView;
    private ListAdapter mAdapter;
    public static final String TAG = NotificationFragment.class.getSimpleName();
    public static boolean canBack = true;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_invitations, container, false);

        Log.d(TAG, "Get Pending Invitations");
        mListView = (ListView) view.findViewById(R.id.list_view);

        getPendingInvitationList(view);

        return view;
    }
    /*private void addTripData(){
            Log.d(TAG, "Add new Trip");
            Intent intent = new Intent(getActivity(), AddTrip.class);\\\\];;;;ppmm=pp
            startActivityForResult(intent, 0);
            getTripList();
    }*/
    private void getPendingInvitationList(View view) {
        Log.d(TAG, "Inside Get Pending Invitation Lists");
        InviteDBHelper inviteDb = new InviteDBHelper(getActivity());
        Cursor inviteCursor = inviteDb.getAllPendingInvites();
        int locRow = inviteCursor.getCount();
        if (locRow > 0 ) {
            if (inviteCursor.moveToFirst()) {
                do {
                    String name = inviteCursor.getString(inviteCursor.getColumnIndex("name"));
                    String phone = inviteCursor.getString(inviteCursor.getColumnIndex("phone"));
                    String regId = inviteCursor.getString(inviteCursor.getColumnIndex("regID"));
                    String rowId = inviteCursor.getString(inviteCursor.getColumnIndex("_id"));
                    Log.i(TAG, "Name is " + name);
                    Log.i(TAG, "Phone is " + phone);
                    Log.i(TAG, "regID is " + regId);
                    Log.i(TAG, "regID is " + rowId);

                    //setMapRoute(locationCursor.getDouble(locationCursor.getColumnIndex("lat")),
                    //locationCursor.getDouble(locationCursor.getColumnIndex("lng")), uts);
                    //results.add("LatL " + Lat + ",Long: " + Lng);
                }while (inviteCursor.moveToNext());
            } Log.i(TAG, "No More Pending Invites Now");
        } else {
            /*Toast.makeText(
                    getActivity(), "You have No Pending Invites.",
                    Toast.LENGTH_LONG).show();*/
            Log.i(TAG, "You have No Pending Invites");
        }

        if (locRow > 0 ) {
            /*mAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.list_view_contact,
                    //android.R.layout.simple_list_item_1,
                    inviteCursor,
                    new String[]{"name", "name"},
                    new int[]{R.id.contact_name, R.id.contact_name}, 0);

           Button accept_btn = (Button)view.findViewById(R.id.accept_btn);


            accept_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    acceptInvite(); //or some other task
                    notifyDataSetChanged();
                }
            });*/
            mAdapter = new MyCustomNotAdapter(getContext(), inviteCursor, 0);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                    Log.i(TAG, "Selected position" + position);
                    //Log.i(TAG, "Selected  " + text1Value);
                    Log.i(TAG, "Selected Row Data for " + arg3);
                    InviteDBHelper inviteDb = new InviteDBHelper(getActivity());
                    Cursor inviteCursor = inviteDb.getRowInvites(arg3);

                    String name = null;
                    String phone = null;
                    String rowId = null;
                    String regId = null;
                    if (inviteCursor != null) {
                        if (inviteCursor.moveToFirst()) {
                            name = inviteCursor.getString(inviteCursor.getColumnIndex("name"));
                            phone = inviteCursor.getString(inviteCursor.getColumnIndex("phone"));
                            rowId = inviteCursor.getString(inviteCursor.getColumnIndex("_id"));
                            regId = inviteCursor.getString(inviteCursor.getColumnIndex("regID"));
                            Log.i(TAG, "name is " + name);
                            Log.i(TAG, "phone is " + phone);
                        }
                    }
                    Intent intent = new Intent(getActivity(), NotificationIntent.class);
                    intent.putExtra("name", name);
                    intent.putExtra("phone", phone);
                    intent.putExtra("rowId", rowId);
                    intent.putExtra("regId", regId);
                    startActivity(intent);
                }
            });
        }
    }
    public void btnAcceptClick(View v)
    {
        Log.i(TAG, "Inside btnAcceptClick");

        final int position = mListView.getPositionForView((View) v.getParent());
        Toast.makeText(
                getActivity(), "Accepted position is" + position,
                Toast.LENGTH_LONG).show();
       // mListView.remove(position);
        //ItemAdapter.notifyDataSetChanged();
    }
    public void btnRejectClick(View v)
    {
        Log.i(TAG, "Inside btnAcceptClick");
    }
    private void acceptInvite() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                //getTripList();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
    }//onActivityResult

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public class MyCustomNotAdapter extends CursorAdapter {

        private ArrayList mData = new ArrayList();
        private LayoutInflater mInflater;

        public MyCustomNotAdapter(Context context, Cursor cursor, int flags) {

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
            String contact_name = cursor.getString(cursor.getColumnIndex("name"));
            contactName.setText(contact_name);

            TextView contactStatus = (TextView) view.findViewById(R.id.invite_status);
            String contact_status = cursor.getString(cursor.getColumnIndex("contactStatus"));
            contactStatus.setText(contact_status);

            TextView contactPhone= (TextView) view.findViewById(R.id.contact_phone);
            String contact_phone = cursor.getString(cursor.getColumnIndex("phone"));
            contactPhone.setText(contact_phone);

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
            }
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = null;
            v = mInflater.inflate(R.layout.list_view_contact, parent, false);
            return v;
        }
    }

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
