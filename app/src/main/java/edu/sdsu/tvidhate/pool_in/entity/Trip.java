package edu.sdsu.tvidhate.pool_in.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Trip implements Serializable {
    private String mTripId,mSourceAddress,mDestinationAddress;
    private Long mCreationTimestamp,mEndTimestamp;
    private int mSeatsAvailable;
    private User mTripDriver;
    private Map<String,User> mTripPassengers;
    private boolean mTripVisible;
    private String mTripStatus;
    private String mStartTime,mStartDate;
    private Car mTripCar;

    public Trip()
    {

    }

    public Trip(String mTripId, String mSourceAddress, String mDestinationAddress,
                String mStartDate,String mStartTime, Long mCreationTimestamp,
                int mSeatsAvailable, User mTripDriver) {
        this.mTripId = mTripId;
        this.mSourceAddress = mSourceAddress;
        this.mDestinationAddress = mDestinationAddress;
        this.mStartDate = mStartDate;
        this.mStartTime = mStartTime;
        this.mCreationTimestamp = mCreationTimestamp;
        this.mEndTimestamp = null;
        this.mSeatsAvailable = mSeatsAvailable;
        this.mTripDriver = mTripDriver;
        this.mTripStatus = "Created";
        this.mTripPassengers = null;
        this.mTripVisible = true;
        this.mTripCar = mTripDriver.getmCar();
    }

    public Car getmTripCar() {
        return mTripCar;
    }

    public void setmTripCar(Car mTripCar) {
        this.mTripCar = mTripCar;
    }

    public Map<String,User> getmTripPassengers() {
        return mTripPassengers;
    }

    public void setmTripPassengers(Map<String,User> mTripPassengers) {
        this.mTripPassengers = mTripPassengers;
    }

    public String getmStartTime() {
        return mStartTime;
    }

    public void setmStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getmStartDate() {
        return mStartDate;
    }

    public void setmStartDate(String mStartDate) {
        this.mStartDate = mStartDate;
    }

    public String getmTripId() {
        return mTripId;
    }

    public void setmTripId(String mTripId) {
        this.mTripId = mTripId;
    }

    public String getmSourceAddress() {
        return mSourceAddress;
    }

    public void setmSourceAddress(String mSourceAddress) {
        this.mSourceAddress = mSourceAddress;
    }

    public String getmDestinationAddress() {
        return mDestinationAddress;
    }

    public void setmDestinationAddress(String mDestinationAddress) {
        this.mDestinationAddress = mDestinationAddress;
    }

    public Long getmCreationTimestamp() {
        return mCreationTimestamp;
    }

    public void setmCreationTimestamp(Long mCreationTimestamp) {
        this.mCreationTimestamp = mCreationTimestamp;
    }

    public Long getmEndTimestamp() {
        return mEndTimestamp;
    }

    public void setmEndTimestamp(Long mEndTimestamp) {
        this.mEndTimestamp = mEndTimestamp;
    }

    public int getmSeatsAvailable() {
        return mSeatsAvailable;
    }

    public void setmSeatsAvailable(int mSeatsAvailable) {
        this.mSeatsAvailable = mSeatsAvailable;
    }

    public User getmTripDriver() {
        return mTripDriver;
    }

    public void setmTripDriver(User mTripDriver) {
        this.mTripDriver = mTripDriver;
    }

    public boolean ismTripVisible() {
        return mTripVisible;
    }

    public void setmTripVisible(boolean mTripVisible) {
        this.mTripVisible = mTripVisible;
    }

    public String getmTripStatus() {
        return mTripStatus;
    }

    public void setmTripStatus(String mTripStatus) {
        this.mTripStatus = mTripStatus;
    }
}
