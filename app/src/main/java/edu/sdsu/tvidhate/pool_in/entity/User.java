package edu.sdsu.tvidhate.pool_in.entity;

import java.io.Serializable;

import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class User implements SharedConstants,Serializable
{
    private String mFirstName,mLastName,mContactNumber,mEmailAddress;
    private String mHomeAddress,mWorkAddress;
    private Car mCar;

    public User()
    {

    }

    public User(String mFirstName, String mLastName, String mContactNumber, String mEmailAddress, String mHomeAddress) {
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mContactNumber = mContactNumber;
        this.mEmailAddress = mEmailAddress;
        this.mHomeAddress = mHomeAddress;
        this.mCar = null;
        this.mWorkAddress = null;
    }

    public boolean hasACar(){
        if(this.getmCar() == null)
            return FAILURE;
        else
            return SUCCESS;
    }

    public String getFullName(){
        return (this.mFirstName+" "+this.mLastName);
    }

    public Car getmCar() {
        return mCar;
    }

    public void setmCar(Car mCar) {
        this.mCar = mCar;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
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

    public String getmHomeAddress() {
        return mHomeAddress;
    }

    public void setmHomeAddress(String mHomeAddress) {
        this.mHomeAddress = mHomeAddress;
    }

    public String getmWorkAddress() {
        return mWorkAddress;
    }

    public void setmWorkAddress(String mWorkAddress) {
        this.mWorkAddress = mWorkAddress;
    }
}
