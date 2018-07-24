package edu.sdsu.tvidhate.pool_in.entity;

import android.net.Uri;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;

public class Trip implements Serializable {

    private Long mCreationTimestamp;
    private String mTripId;
    private String mTripPlaceName,mTripCity,mTripPincode;
    private User mTripPoster;
    private String mTripImagePath;
    private String imageDownloadUrl;

    public Trip() {
    }

    public Trip(Long mCreationTimestamp, String mTripId, String mTripPlaceName, String mTripCity, String mTripPincode, User mTripPoster, String mTripImagePath, String imageDownloadUrl) {
        this.mCreationTimestamp = mCreationTimestamp;
        this.mTripId = mTripId;
        this.mTripPlaceName = mTripPlaceName;
        this.mTripCity = mTripCity;
        this.mTripPincode = mTripPincode;
        this.mTripPoster = mTripPoster;
        this.mTripImagePath = mTripImagePath;
        this.imageDownloadUrl = imageDownloadUrl;
    }

    public Long getmCreationTimestamp() {
        return mCreationTimestamp;
    }

    public void setmCreationTimestamp(Long mCreationTimestamp) {
        this.mCreationTimestamp = mCreationTimestamp;
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

    public User getmTripPoster() {
        return mTripPoster;
    }

    public void setmTripPoster(User mTripPoster) {
        this.mTripPoster = mTripPoster;
    }

    public String getmTripImagePath() {
        return mTripImagePath;
    }

    public void setmTripImagePath(String mTripImagePath) {
        this.mTripImagePath = mTripImagePath;
    }

    public String getImageDownloadUrl() {
        return imageDownloadUrl;
    }

    public void setImageDownloadUrl(String imageDownloadUrl) {
        this.imageDownloadUrl = imageDownloadUrl;
    }
}
