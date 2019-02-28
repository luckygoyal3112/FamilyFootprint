package com.ack.familyfootprints.timelineTab;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ack.familyfootprints.DBHelper.LocationDBHelper;
import com.ack.familyfootprints.DatePickerFragment;
import com.ack.familyfootprints.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//import com.google.android.gms.plus.PlusOneButton;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link TimelineFragment} interface
 * to handle interaction events.
 * Use the {@link TimelineFragment} factory method to
 * create an ingcm.send broadcaststance of this fragment.
 */
public class TimelineFragment extends Fragment implements AbsListView.OnItemClickListener{
    private AdView mAdView;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FusedLocationProviderClient mFusedLocationClient;
    private Context mContext;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // TODO: Rename and change types of parameters

    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int PERMISSION_REQUEST_CODE = 1;
    // The URL to +1.  Must be a valid URL.
    // private final String PLUS_ONE_URL = "http://developer.android.com";

    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private ListAdapter mAdapter;
    TextView dateView;
    public static final String TAG = TimelineFragment.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    int year, month, day;
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
    public static TimelineFragment newInstance(String param1, String param2) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getActivity(), TrackbookLocService.class);
        getActivity().startService(intent);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        dateView = view.findViewById(R.id.textView1);

        dateView.setText(getTodayDate());
        //setTodayMap(view);
        ImageButton calButton = view.findViewById(R.id.calButton);
        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelDate(view);
            }
        });
        //registerHandler(view);
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        return view;
    }

    private String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String selectedDate = dateFormat.format(new Date()); // Find todays date
        return selectedDate;
    }
    private String getPickerDate(String dateString) throws ParseException {
        //String dateString = "Fri, 19 Jul 2013 01:30:22 GMT";
        SimpleDateFormat simple = new SimpleDateFormat("MMM dd, yyyy");
        Date date;
        //try {
        date = new SimpleDateFormat("dd/MM/yyy")
                .parse(dateString);
        String selectedDate = simple.format(date);
        return selectedDate;

    }
    public void getSelDate(View view) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog,23
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
        date.show(getActivity().getFragmentManager(), "Date Picker");
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
            getMapList(selectedDate);
        }
    };
    private void getMapList(final String selDate) {
        LocationDBHelper locDb = new LocationDBHelper(getActivity());
        Cursor locationCursor = locDb.getTodayLocations(selDate);
        int locRow = locationCursor.getCount();
        String transit = null;
        if (locRow > 0 ) {
                if (locationCursor.moveToFirst()) {
                    do {
                        int Lat = (int) (locationCursor.getDouble(locationCursor.getColumnIndex("lat")));
                        int Lng = (int) (locationCursor.getDouble(locationCursor.getColumnIndex("lng")));
                        String est = locationCursor.getString(locationCursor.getColumnIndex("est"));
                        String eet = locationCursor.getString(locationCursor.getColumnIndex("eet"));
                        StringBuilder messages = new StringBuilder();
                        String padr = locationCursor.getString(locationCursor.getColumnIndex("address"));
                        String gadr = locationCursor.getString(locationCursor.getColumnIndex("geoAddress"));
                        transit = locationCursor.getString(locationCursor.getColumnIndex("transit"));
                        String transitTime = locationCursor.getString(locationCursor.getColumnIndex("transitTime"));
                        StringBuilder adr = new StringBuilder("PLACE ADDRESS");
                        adr.append(padr);
                        adr.append(" ");
                        adr.append("GEOCODE ADDRESS:");
                        adr.append(gadr);
                        adr.append(" ");

                        String place = locationCursor.getString(locationCursor.getColumnIndex("placeName"));
                    } while (locationCursor.moveToNext());
            }
        } else {
            Toast.makeText(
                    getActivity(), "No Location available for the day!",
                    Toast.LENGTH_SHORT).show();
        }
         /*   mAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.list_view_layout,
                    locationCursor,
                    new String[]{"placeName", "address", "est", "eet"},
                    new int[]{R.id.place, R.id.address, R.id.est, R.id.eet}, 0);
                    ListView  mListView = getView().findViewById(R.id.list_view);
                    //mListView.setAdapter(mAdapter);
                    //return;
        }*/
        mAdapter = new MyCustomAdapter(getActivity(),locationCursor,0 );
        /*if (transit != null){
            if (transit.equals("NA")) {
                mAdapter = new SimpleCursorAdapter(getActivity(),
                        R.layout.list_view_layout,
                        locationCursor,
                        new String[]{"placeName", "address", "est", "eet"},
                        new int[]{R.id.place, R.id.address, R.id.est, R.id.eet}, 0);

            } else {
                mAdapter = new SimpleCursorAdapter(getActivity(),
                        R.layout.list_view_layout,
                        locationCursor,
                        new String[]{"placeName", "address", "est", "eet", "transit", "transitTime"},
                        new int[]{R.id.place, R.id.address, R.id.est, R.id.eet, R.id.transit, R.id.transitTime}, 0);
            }
        }*/
        ListView  mListView = getView().findViewById(R.id.list_view);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                LocationDBHelper rowDb = new LocationDBHelper(getActivity());
                Cursor locationCursorb = rowDb.getRowLocations(String.valueOf(arg3));
                double Lng = 0;
                double Lat = 0;
                String adr = null;
                String est = null;
                String eet = null;
                String transit = null;
                String transitTime = null;
                if (locationCursorb != null) {
                    if (locationCursorb.moveToFirst()) {
                        Lat = locationCursorb.getDouble(locationCursorb.getColumnIndex("lat"));
                        Lng = locationCursorb.getDouble(locationCursorb.getColumnIndex("lng"));
                        est = locationCursorb.getString(locationCursorb.getColumnIndex("est"));
                        adr = locationCursorb.getString(locationCursorb.getColumnIndex("address"));
                    }
                }

                if (locationCursorb != null)
                            locationCursorb.close();
                Intent intent = new Intent(getActivity(), MapsActivityIntent.class);
                intent.putExtra("Lat", Lat);
                intent.putExtra("Lng", Lng);
                intent.putExtra("Adr", adr);
                intent.putExtra("est", est);
                intent.putExtra("dateSel", selDate);
                startActivity(intent);
            }
        });
    }
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

    private class MyCustomAdapter extends CursorAdapter {

        private ArrayList mData = new ArrayList();
        private LayoutInflater mInflater;

        public MyCustomAdapter(Context context, Cursor cursor, int flags) {
            //super();
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
            String address_data = cursor.getString( cursor.getColumnIndex("address"));
            address.setText(address_data);

            TextView est = (TextView) view.findViewById(R.id.est);
            String est_data = cursor.getString( cursor.getColumnIndex("est"));
            est.setText(est_data);

            TextView eet = (TextView) view.findViewById(R.id.eet);
            String eet_data = cursor.getString( cursor.getColumnIndex("eet"));
            eet.setText(eet_data);
            if (cursor.getString(cursor.getColumnIndex("transit")).equals("NA")) {
                Log.w(TAG, "Row for " + placeName + " doesnt has Transit data");
            } else {
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


