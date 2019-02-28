package com.ack.familyfootprints.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lucky Goyal on 7/20/2016.
 */
public class PingerLocation {
    private final String mName;
    private final String mPhone;
    private final String mRegistrationToken;
    private final String mPlaceName;
    private final String mAddress;
    private final String mDateSelected;
    private final String mUts;
    private final Double mLat;
    private final Double mLng;
    private final String mOldUts;
    private final String mOldrowId;
    private final String mOtransit;
    private final String mOtransitTime;
    private final String mODate;
    private final String mOest;


    public PingerLocation(String name, String Phone, String registrationToken,String dateSelected, String uts, String placeName, String address, Double lat, Double lng, String oldUts,
                          String orowId, String otransit, String otransitTime, String oDate, String oest) {
        mName = name;
        mPhone = Phone;
        mRegistrationToken = registrationToken;
        mDateSelected =dateSelected;
        mUts = uts;
        mPlaceName = placeName;
        mAddress = address;
        mLat = lat;
        mLng = lng;
        mOldUts = oldUts;
        mOldrowId = orowId;
        mOtransit = otransit;
        mOtransitTime = otransitTime;
        mODate = oDate;
        mOest = oest;
    }

    public String getName() {
        return mName;
    }

    public String getRegistrationToken() {
        return mRegistrationToken;
    }
    public String getDateSelected() {
        return mDateSelected;
    }
    public String getUts() {
        return mUts;
    }

    public String getPhone() {
        return mPhone;
    }
    public String getPlaceName() {
        return mPlaceName;
    }
    public String getAddress() {
        return mAddress;
    }
    public double getLat() {
        return mLat;
    }
    public double getLng() {
        return mLng;
    }
    public String getOldUts() {
        return mOldUts;
    }
    public String getOldrowId() {
        return mOldrowId;
    }
    public String getOldtransit() {
        return mOtransit;
    }
    public String getOldransitTime() {
        return mOtransitTime;
    }
    public String getOldDate() {
        return mODate;
    }
    public String getOldest() {
        return mOest;
    }
    /**
     * Creates a new {@link Pinger} from a provided {@link JSONObject}.
     *
     * @param jsonPinger The JSON representation of a {@link Pinger}.
     * @return The {@link Pinger} parsed out of the {@link JSONObject}.
     * @throws JSONException Thrown when parsing was not possible.
     */
    public static PingerLocation fromJson(JSONObject jsonPinger) throws JSONException {
        return new PingerLocation(jsonPinger.getString(PingerKeys.NAME),
                jsonPinger.getString(PingerKeys.PHONE),
                jsonPinger.getString(PingerKeys.REGISTRATION_TOKEN),
                jsonPinger.getString(PingerKeys.DATESELECTED),
                jsonPinger.getString(PingerKeys.UTS),
                jsonPinger.getString(PingerKeys.PLACENAME),
                jsonPinger.getString(PingerKeys.PLACEADR),
                jsonPinger.getDouble(PingerKeys.LAT) ,
                jsonPinger.getDouble(PingerKeys.LNG),
                jsonPinger.getString(PingerKeys.OUTS),
                jsonPinger.getString(PingerKeys.OROWID),
                jsonPinger.getString(PingerKeys.OTRANSIT),
                jsonPinger.getString(PingerKeys.OTRANSITTIME),
                jsonPinger.getString(PingerKeys.ODATE),
                jsonPinger.getString(PingerKeys.OEST));
    }

    /**
     * Creates a new {@link Pinger} from a provided {User Data}.
     *.
     * @return The {@link Pinger} parsed out of the {@link JSONObject}.
     * @throws JSONException Thrown when parsing was not possible.
     */
    public static PingerLocation fromUser(String name, String phone,String regId, String dateSelected, String uts, String placeName,
                                          String address,Double lat, Double lng, String oldUts, String orowId, String transit, String transitTime, String oDate, String oest) throws JSONException {
        return new PingerLocation(name,phone,regId, dateSelected, uts,placeName, address, lat, lng,oldUts, orowId, transit, transitTime, oDate, oest );
    }

    protected PingerLocation(Parcel in) {
        mName = in.readString();
        mPhone = in.readString();
        mRegistrationToken = in.readString();
        mDateSelected = in.readString();
        mUts = in.readString();
        mPlaceName = in.readString();
        mAddress = in.readString();
        mLat = in.readDouble();
        mLng = in.readDouble();
        mOldUts = in.readString();
        mOldrowId = in.readString();
        mOtransit = in.readString();
        mOtransitTime = in.readString();
        mODate= in.readString();
        mOest= in.readString();
    }


    public static final Parcelable.Creator<PingerLocation> CREATOR = new Parcelable.Creator<PingerLocation>() {
        @Override
        public PingerLocation createFromParcel(Parcel in) {
            return new PingerLocation(in);
        }

        @Override
        public PingerLocation[] newArray(int size) {
            return new PingerLocation[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pinger)) {
            return false;
        }

        PingerLocation pingerLoc = (PingerLocation) o;

        if (!mName.equals(pingerLoc.mName)) {
            return false;
        }
        if (!mPhone.equals(pingerLoc.mPhone)) {
            return false;
        }
        return mRegistrationToken.equals(pingerLoc.mRegistrationToken);
    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mRegistrationToken.hashCode();
        return result;
    }
}
