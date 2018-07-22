package edu.sdsu.tvidhate.pool_in.entity;

import java.io.Serializable;

import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class User implements SharedConstants,Serializable
{
    private String mUserName,mContactNumber,mEmailAddress;

    public User()
    {

    }

    public User(String mFirstName, String mContactNumber, String mEmailAddress) {
        this.mUserName = mFirstName;
        this.mContactNumber = mContactNumber;
        this.mEmailAddress = mEmailAddress;
    }

    public String getmContactNumber() {
        return mContactNumber;
    }

    public void setmContactNumber(String mContactNumber) {
        this.mContactNumber = mContactNumber;
    }

    public String getmEmailAddress() {
        return mEmailAddress;
    }

    public void setmEmailAddress(String mEmailAddress) {
        this.mEmailAddress = mEmailAddress;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    @Override
    public String toString() {
        return "User{" +
                "mUserName='" + mUserName + '\'' +
                ", mContactNumber='" + mContactNumber + '\'' +
                ", mEmailAddress='" + mEmailAddress + '\'' +
                '}';
    }
}
