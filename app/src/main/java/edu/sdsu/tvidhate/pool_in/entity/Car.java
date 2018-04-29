package edu.sdsu.tvidhate.pool_in.entity;

public class Car {
    private String mModel,mBrand,mColor,mNumberPlate;

    public Car(String mModel, String mBrand, String mColor, String mNumberPlate) {
        this.mModel = mModel;
        this.mBrand = mBrand;
        this.mColor = mColor;
        this.mNumberPlate = mNumberPlate;
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
}
