package edu.sdsu.tvidhate.pool_in.helper;

import android.app.Activity;
import android.content.Intent;
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
import edu.sdsu.tvidhate.pool_in.fragment.UpdateProfileFragment;

public class Utilities implements SharedConstants
{
    private FragmentManager fragmentManager;
    public Utilities(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String phNo;

    public void checkProfile(){
        Log.d("rew","in utilities class");
        if(auth.getCurrentUser()!=null){
            phNo = auth.getCurrentUser().getDisplayName();
        }
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "in the personal details section utilities: "+dataSnapshot.getChildrenCount());
                if(dataSnapshot.getChildrenCount()<1){
                    Fragment updateProfileFragment = new UpdateProfileFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.screen_area,updateProfileFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference people1 = database1.getReference(FIREBASE_PERSONAL_DATA).child(phNo);
        people1.addValueEventListener(valueEventListener1);

    }

    public void checkForExistingRide(final Activity activity) {
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount()>0 && activity!=null){
                    Intent intent = activity.getIntent();
                    activity.finish();
                    activity.startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference people1 = database1.getReference(FIREBASE_TRIP_DETAILS).child(phNo);
        people1.addValueEventListener(valueEventListener1);
    }

}
