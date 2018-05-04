package edu.sdsu.tvidhate.pool_in.entity;

import java.io.Serializable;

public class Car implements Serializable{
    private String mModel,mBrand,mColor,mNumberPlate;
    private String mCarId;

    public Car(String mCarId,String mModel, String mBrand, String mColor, String mNumberPlate) {
        this.mModel = mModel;
        this.mBrand = mBrand;
        this.mColor = mColor;
        this.mNumberPlate = mNumberPlate;
        this.mCarId = mCarId;
    }

    public Car() {
    }

    public String getmCarId() {
        return mCarId;
    }

    public void setmCarId(String mCarId) {
        this.mCarId = mCarId;
    }

    public String getmModel() {
        return mModel;
    }

    public void setmModel(String mModel) {
        this.mModel = mModel;
    }

    public String getmBrand() {
        return mBrand;
    }

    public void setmBrand(String mBrand) {
        this.mBrand = mBrand;
    }

    public String getmColor() {
        return mColor;
    }

    public void setmColor(String mColor) {
        this.mColor = mColor;
    }

    public String getmNumberPlate() {
        return mNumberPlate;
    }

    public void setmNumberPlate(String mNumberPlate) {
        this.mNumberPlate = mNumberPlate;
    }

    @Override
    public String toString() {
        return (mBrand+" "+mModel);
    }
}
