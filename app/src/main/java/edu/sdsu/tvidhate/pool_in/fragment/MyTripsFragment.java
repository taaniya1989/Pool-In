package edu.sdsu.tvidhate.pool_in.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.activity.RideHistoryDetailsActivity;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.helper.MyRidesListAdapter;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;
import edu.sdsu.tvidhate.pool_in.helper.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyTripsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTripsFragment extends Fragment implements SharedConstants {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<String> uids = new ArrayList<>();
    List<Trip> myRideDetailsList = new ArrayList<>();
    private ListView myRidesListView;
    MyRidesListAdapter listadapter;
    private String myPhoneNo="";
    private OnFragmentInteractionListener mListener;
    private String hasUserDetails;

    public MyTripsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyTripsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTripsFragment newInstance(String param1, String param2) {
        MyTripsFragment fragment = new MyTripsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_trips, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        hasUserDetails = "User";
        Utilities utilities = new Utilities(getFragmentManager());
        utilities.checkProfile(hasUserDetails);
        myRidesListView = view.findViewById(R.id.my_rides_list_view);
        if(auth.getCurrentUser()!=null){
            myPhoneNo = auth.getCurrentUser().getDisplayName();
        }

        uids = getAllUidsFromCurrentRides(myPhoneNo);
        
        myRidesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Trip rideUid = (Trip) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(),RideHistoryDetailsActivity.class);
                intent.putExtra(UID,rideUid.getmTripId());
                startActivity(intent);

            }
        });

        return view;
    }

    private List getAllUidsFromCurrentRides(final String phNo) {
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " uids");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("TPV-NOTE","snapshot uid value: "+snapshot.getValue());
                    uids.add(snapshot.getValue().toString());
                }
                Log.d("TPV-NOTE","uid size: "+uids.size());
                if(uids!=null){
                    final Map<String,String> map = new HashMap<>();
                    for(int i=0;i<uids.size();i++){
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " people");
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    Trip myRideDetailsPOJO = dataSnapshot.getValue(Trip.class);
                                    myRideDetailsList.add(myRideDetailsPOJO);
                                    Collections.sort(myRideDetailsList, new Comparator<Trip>() {
                                        @Override
                                        public int compare(Trip o1, Trip o2) {
                                            Log.i("TPV-NOTE",o1.toString()+"----\n---"+o2.toString());
                                            return o1.getmCreationTimestamp().compareTo(o2.getmCreationTimestamp());
                                        }
                                    });
                                    Collections.reverse(myRideDetailsList);
                                    if(getActivity() != null){
                                        listadapter = new MyRidesListAdapter(getActivity(), 0, myRideDetailsList);
                                        myRidesListView.setAdapter(listadapter);
                                    }
                                    if (listadapter != null) {
                                        listadapter.notifyDataSetChanged();
                                    }
                                }else {
                                    if (listadapter != null) {
                                        listadapter.notifyDataSetChanged();
                                    }
                                    Log.d("TPV-NOTE","No Data yet");
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference people = database.getReference(FIREBASE_MY_RIDES).child(uids.get(i));
                        people.addValueEventListener(valueEventListener);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference people1 = database1.getReference(FIREBASE_CURRENT_RIDES).child(phNo);
        people1.addValueEventListener(valueEventListener1);
        return uids;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
