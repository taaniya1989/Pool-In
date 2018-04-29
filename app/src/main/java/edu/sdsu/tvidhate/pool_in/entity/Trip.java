package edu.sdsu.tvidhate.pool_in.entity;

import java.sql.Timestamp;
import java.util.ArrayList;

import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class Trip implements SharedConstants{
    private String mTripId,mSourceAddress,mDestinationAddress;
    private Timestamp mStartTimestamp,mCreationTimestamp,mEndTimestamp;
    private int mSeatsAvailable;
    private User mTripDriver;
    private ArrayList<User> mTripPassegers;
    private boolean mTripVisible;
    private String mTripStatus;

    public Trip(String mTripId, String mSourceAddress, String mDestinationAddress, Timestamp mStartTimestamp, Timestamp mCreationTimestamp,
                int mSeatsAvailable, User mTripDriver) {
        this.mTripId = mTripId;
        this.mSourceAddress = mSourceAddress;
        this.mDestinationAddress = mDestinationAddress;
        this.mStartTimestamp = mStartTimestamp;
        this.mCreationTimestamp = mCreationTimestamp;
        this.mEndTimestamp = mEndTimestamp;
        this.mSeatsAvailable = mSeatsAvailable;
        this.mTripDriver = mTripDriver;
        this.mTripStatus = TRIP_CREATED;
        this.mTripPassegers = null;
        this.mTripVisible = true;
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

    public Timestamp getmStartTimestamp() {
        return mStartTimestamp;
    }

    public void setmStartTimestamp(Timestamp mStartTimestamp) {
        this.mStartTimestamp = mStartTimestamp;
    }

    public Timestamp getmCreationTimestamp() {
        return mCreationTimestamp;
    }

    public void setmCreationTimestamp(Timestamp mCreationTimestamp) {
        this.mCreationTimestamp = mCreationTimestamp;
    }

    public Timestamp getmEndTimestamp() {
        return mEndTimestamp;
    }

    public void setmEndTimestamp(Timestamp mEndTimestamp) {
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

    public ArrayList<User> getmTripPassegers() {
        return mTripPassegers;
    }

    public void setmTripPassegers(ArrayList<User> mTripPassegers) {
        this.mTripPassegers = mTripPassegers;
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
