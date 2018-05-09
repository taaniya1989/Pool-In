package edu.sdsu.tvidhate.pool_in.entity;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;

public class Trip implements Serializable {
    private String mTripId,mSourceAddress,mDestinationAddress;
    private String mSourcePin,mSourceNeighbordhood;
    private String mDestinationPin,mDestinationNeighbordhood;
    private Long mCreationTimestamp,mEndTimestamp;
    private Date mStartTimestamp;
    private int mSeatsAvailable;
    private User mTripDriver;
    private Map<String,User> mTripPassengers;
    private boolean mTripVisible;
    private String mTripStatus;
    private String mStartTime,mStartDate;
    private Car mTripCar;

    public Date getmStartTimestamp() {
        return mStartTimestamp;
    }

    public void setmStartTimestamp(Date mStartTimestamp) {
        this.mStartTimestamp = mStartTimestamp;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "mTripId='" + mTripId + '\'' +
                ", mSourceAddress='" + mSourceAddress + '\'' +
                ", mDestinationAddress='" + mDestinationAddress + '\'' +
                ", mSourcePin='" + mSourcePin + '\'' +
                ", mSourceNeighbordhood='" + mSourceNeighbordhood + '\'' +
                ", mDestinationPin='" + mDestinationPin + '\'' +
                ", mDestinationNeighbordhood='" + mDestinationNeighbordhood + '\'' +
                ", mCreationTimestamp=" + mCreationTimestamp +
                ", mEndTimestamp=" + mEndTimestamp +
                ", mStartTimestamp=" + mStartTimestamp +
                ", mSeatsAvailable=" + mSeatsAvailable +
                ", mTripDriver=" + mTripDriver +
                ", mTripPassengers=" + mTripPassengers +
                ", mTripVisible=" + mTripVisible +
                ", mTripStatus='" + mTripStatus + '\'' +
                ", mStartTime='" + mStartTime + '\'' +
                ", mStartDate='" + mStartDate + '\'' +
                ", mTripCar=" + mTripCar +
                '}';
    }

    public Trip()
    {

    }

    public String getmSourcePin() {
        return mSourcePin;
    }

    public void setmSourcePin(String mSourcePin) {
        this.mSourcePin = mSourcePin;
    }

    public String getmSourceNeighbordhood() {
        return mSourceNeighbordhood;
    }

    public void setmSourceNeighbordhood(String mSourceNeighbordhood) {
        this.mSourceNeighbordhood = mSourceNeighbordhood;
    }

    public String getmDestinationPin() {
        return mDestinationPin;
    }

    public void setmDestinationPin(String mDestinationPin) {
        this.mDestinationPin = mDestinationPin;
    }

    public String getmDestinationNeighbordhood() {
        return mDestinationNeighbordhood;
    }

    public void setmDestinationNeighbordhood(String mDestinationNeighbordhood) {
        this.mDestinationNeighbordhood = mDestinationNeighbordhood;
    }

    public Trip(String mTripId, String mSourceAddress, String mDestinationAddress, String mSourcePin,
                String mSourceNeighbordhood, String mDestinationPin, String mDestinationNeighbordhood,
                Long mCreationTimestamp, Date mStartTripTimestamp, int mSeatsAvailable, User mTripDriver, String mStartTime, String mStartDate) {
        this.mTripId = mTripId;
        this.mSourceAddress = mSourceAddress;
        this.mDestinationAddress = mDestinationAddress;
        this.mSourcePin = mSourcePin;
        this.mSourceNeighbordhood = mSourceNeighbordhood;
        this.mDestinationPin = mDestinationPin;
        this.mDestinationNeighbordhood = mDestinationNeighbordhood;
        this.mCreationTimestamp = mCreationTimestamp;
        this.mSeatsAvailable = mSeatsAvailable;
        this.mTripDriver = mTripDriver;
        this.mStartTime = mStartTime;
        this.mStartDate = mStartDate;
        this.mTripCar = mTripDriver.getmCar();
        this.mEndTimestamp = null;
        this.mTripStatus = "Created";
        this.mTripPassengers = null;
        this.mTripVisible = true;
        this.mStartTimestamp = mStartTripTimestamp;
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
