package edu.sdsu.tvidhate.pool_in.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.activity.TripDetailsActivity;
import edu.sdsu.tvidhate.pool_in.entity.Trip;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;
import edu.sdsu.tvidhate.pool_in.helper.TripDetailsAdapter;
import edu.sdsu.tvidhate.pool_in.helper.Utilities;


public class HomeFragment extends Fragment implements SharedConstants
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ProgressDialog progressDialog;
    private OnFragmentInteractionListener mListener;
    private ListView tripDetailsListView;
    private FirebaseAuth auth;
    TripDetailsAdapter listadapter;
    private String hasUserDetails;
    List<Trip> tripDataList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();

        hasUserDetails = "User";
        Utilities utilities = new Utilities(getFragmentManager());
        utilities.checkProfile(hasUserDetails);

        progressDialog = new ProgressDialog(getContext());
        Log.d("TPV-NOTE","inoncreate view");
        tripDetailsListView = view.findViewById(R.id.trip_list_home);
        getRideDetailsOntoTheList();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getRideDetailsOntoTheList() {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TPV-NOTE","in on data change home fragment");
                if(getContext()!=null){
                    tripDataList.clear();
                }
                Log.d("TPV-NOTE", "There are " + dataSnapshot.getChildrenCount() + " list items in home fragment");
                for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                    Trip currentTrip = msgSnapshot.getValue(Trip.class);
                    tripDataList.add(currentTrip);
                }

                Collections.sort(tripDataList, new Comparator<Trip>() {
                    @Override
                    public int compare(Trip o1, Trip o2) {
                        Log.i("TPV-NOTE",o1.toString()+"----\n---"+o2.toString());
                        return o1.getmCreationTimestamp().compareTo(o2.getmCreationTimestamp());
                    }
                });
                Collections.reverse(tripDataList);
                if(getActivity() != null){
                    listadapter = new TripDetailsAdapter(getActivity(), 0, tripDataList);
                    tripDetailsListView.setAdapter(listadapter);
                    progressDialog.dismiss();
                }
                if (listadapter != null) {
                    listadapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
                progressDialog.dismiss();
                Log.d("TPV-NOTE","No Data yet");
                tripDetailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Intent intent = new Intent(getActivity(), TripDetailsActivity.class);
                        Trip selectedTrip = (Trip) adapterView.getItemAtPosition(position);
                        intent.putExtra(TRIP_DETAILS_SERIALIZABLE,selectedTrip);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TPV-NOTE","database error: "+databaseError);
                Toast.makeText(getActivity(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(FIREBASE_TRIP_DETAILS);
        people.addValueEventListener(valueEventListener);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(LOADING);
        progressDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TPV-NOTE","in on resume");
        getRideDetailsOntoTheList();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d("TPV-NOTE","in onstart home fragment:"+user.getUid());
        } else {
            Log.d("TPV-NOTE","in onstart user is null in home fragment:");
        }
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
