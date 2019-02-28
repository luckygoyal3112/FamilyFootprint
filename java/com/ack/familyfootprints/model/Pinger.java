/*
 * Copyright Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ack.familyfootprints.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A registered entity that is able to ping another entity.
 */
public class Pinger implements Parcelable {

    private final String mName;
    private final String mPhone;
    private final String mPictureUrl;
    private final String mRegistrationToken;
    private final String mInvitationRespFlag;

    public Pinger(String name, String Phone, String pictureUrl, String registrationToken, String invitationResponse) {
        mName = name;
        mPictureUrl = pictureUrl;
        mPhone = Phone;
        mRegistrationToken = registrationToken;
        mInvitationRespFlag = invitationResponse;
    }

    public String getName() {
        return mName;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public String getRegistrationToken() {
        return mRegistrationToken;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getInvitationRespFlag() {
        return mInvitationRespFlag;
    }

    /**
     * Creates a new {@link Pinger} from a provided {@link JSONObject}.
     *
     * @param jsonPinger The JSON representation of a {@link Pinger}.
     * @return The {@link Pinger} parsed out of the {@link JSONObject}.
     * @throws JSONException Thrown when parsing was not possible.
     */
    public static Pinger fromJson(JSONObject jsonPinger) throws JSONException {
        return new Pinger(jsonPinger.getString(PingerKeys.NAME),
                jsonPinger.getString(PingerKeys.PHONE),
                jsonPinger.getString(PingerKeys.PICTURE_URL),
                jsonPinger.getString(PingerKeys.REGISTRATION_TOKEN),
                jsonPinger.getString(PingerKeys.CONNECTION_STATUS));
    }

    /**
     * Creates a new {@link Pinger} from a provided {User Data}.
     *.
     * @return The {@link Pinger} parsed out of the {@link JSONObject}.
     * @throws JSONException Thrown when parsing was not possibl.e.
     */
    public static Pinger fromUser(String name, String phone, String photo, String regId, String invResp) throws JSONException {
        return new Pinger(name,phone,photo, regId, invResp);
    };
    ;
    protected Pinger(Parcel in) {
        mName = in.readString();
        mPhone = in.readString();
        mPictureUrl = in.readString();
        mRegistrationToken = in.readString();
        mInvitationRespFlag = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPhone);
        dest.writeString(mPictureUrl);
        dest.writeString(mRegistrationToken);
        dest.writeString(mRegistrationToken);
    }

    public static final Creator<Pinger> CREATOR = new Creator<Pinger>() {
        @Override
        public Pinger createFromParcel(Parcel in) {
            return new Pinger(in);
        }

        @Override
        public Pinger[] newArray(int size) {
            return new Pinger[size];
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

        Pinger pinger = (Pinger) o;

        if (!mName.equals(pinger.mName)) {
            return false;
        }
        if (!mPhone.equals(pinger.mPhone)) {
            return false;
        }
        if (!mPictureUrl.equals(pinger.mPictureUrl)) {
            return false;
        }
        return mRegistrationToken.equals(pinger.mRegistrationToken);

    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mPictureUrl.hashCode();
        result = 31 * result + mRegistrationToken.hashCode();
        return result;
    }
}
