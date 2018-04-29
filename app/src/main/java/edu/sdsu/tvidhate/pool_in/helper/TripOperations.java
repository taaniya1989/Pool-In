package edu.sdsu.tvidhate.pool_in.helper;

import edu.sdsu.tvidhate.pool_in.entity.Trip;

public class TripOperations implements SharedConstants{

    private Trip mCurrentTrip;

    public TripOperations() {
    }

    public boolean createTrip(Trip trip)
    {
        this.mCurrentTrip = trip;
        //Add code to post trip on firebase return SUCCESS on Successful post

        return FAILURE;
    }

    public boolean deleteTrip(Trip trip){

        this.mCurrentTrip = trip;
        //Add code to delete trip from firebase return SUCCESS on Successful post

        return FAILURE;
    }

    public Trip getmCurrentTrip() {
        return mCurrentTrip;
    }

    public void setmCurrentTrip(Trip mCurrentTrip) {
        this.mCurrentTrip = mCurrentTrip;
    }
}
