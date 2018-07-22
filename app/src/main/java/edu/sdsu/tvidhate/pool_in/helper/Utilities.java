package edu.sdsu.tvidhate.pool_in.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.fragment.UpdateProfileFragment;

public class Utilities implements SharedConstants
{
    private FragmentManager fragmentManager;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String phNo;
    private String updateData;

    public Utilities(FragmentManager fragmentManager) {

        this.fragmentManager = fragmentManager;
    }

    public void checkProfile(String updateDataRequested){
        Log.d("TPV-NOTE","in utilities class"+updateDataRequested);
        if(auth.getCurrentUser()!=null){
            phNo = auth.getCurrentUser().getDisplayName();
        }

        updateData = updateDataRequested;
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "in the personal details section utilities: "+dataSnapshot.getChildrenCount()+updateData);
                if(dataSnapshot.getChildrenCount()<1){
                    //updateData = "User";
                    Bundle bundle = new Bundle();
                    bundle.putString("updateData",updateData);
                    Fragment updateProfileFragment = new UpdateProfileFragment();
                    updateProfileFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.screen_area,updateProfileFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }
                else
                {
                    User currentUser = dataSnapshot.getValue(User.class);
//                    if (currentUser.getmCar() == null && updateData == "Car")
//                    {
//                        Log.i("TPV-NOTE","no car info"+updateData);
//                      //  updateData = "Car";
//                        Bundle bundle = new Bundle();
//                        bundle.putString("updateData",updateData);
//                        Fragment updateProfileFragment = new UpdateProfileFragment();
//                        updateProfileFragment.setArguments(bundle);
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.replace(R.id.screen_area,updateProfileFragment);
//                        fragmentTransaction.commitAllowingStateLoss();
//                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        Log.i("TPV-NOTE","Contact "+phNo);
        if(phNo != null) {
            DatabaseReference people1 = database1.getReference(FIREBASE_PERSONAL_DATA).child(phNo);
            people1.addValueEventListener(valueEventListener1);
        }
    }

}
