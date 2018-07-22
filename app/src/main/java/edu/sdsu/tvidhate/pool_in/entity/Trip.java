package edu.sdsu.tvidhate.pool_in.entity;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;

public class Trip implements Serializable {

    private Long mCreationTimestamp;
    private String mTripId;
    private String mTripPlaceName,mTripCity,mTripPincode;
    private User mTripPoster;

    public Trip(String mTripId, String mTripPlaceName, String mTripCity, String mTripPincode,Long mCreationTimestamp,User mTripPoster) {
        this.mTripId = mTripId;
        this.mTripPlaceName = mTripPlaceName;
        this.mTripCity = mTripCity;
        this.mTripPincode = mTripPincode;
        this.mCreationTimestamp = mCreationTimestamp;
        this.mTripPoster = mTripPoster;
    }

    public Trip()
    {

    }

    @Override
    public String toString() {
        return "Trip{" +
                "mTripId='" + mTripId + '\'' +
                ", mTripPlaceName='" + mTripPlaceName + '\'' +
                ", mTripCity='" + mTripCity + '\'' +
                ", mTripPincode='" + mTripPincode + '\'' +
                ", mTripPoster=" + mTripPoster +
                ", mCreationTimestamp=" + mCreationTimestamp +
                '}';
    }

    public User getmTripPoster() {
        return mTripPoster;
    }

    public void setmTripPoster(User mTripPoster) {
        this.mTripPoster = mTripPoster;
    }

    public String getmTripId() {
        return mTripId;
    }

    public void setmTripId(String mTripId) {
        this.mTripId = mTripId;
    }

    public String getmTripPlaceName() {
        return mTripPlaceName;
    }

    public void setmTripPlaceName(String mTripPlaceName) {
        this.mTripPlaceName = mTripPlaceName;
    }

    public String getmTripCity() {
        return mTripCity;
    }

    public void setmTripCity(String mTripCity) {
        this.mTripCity = mTripCity;
    }

    public String getmTripPincode() {
        return mTripPincode;
    }

    public void setmTripPincode(String mTripPincode) {
        this.mTripPincode = mTripPincode;
    }

    public Long getmCreationTimestamp() {
        return mCreationTimestamp;
    }

    public void setmCreationTimestamp(Long mCreationTimestamp) {
        this.mCreationTimestamp = mCreationTimestamp;
    }
}
