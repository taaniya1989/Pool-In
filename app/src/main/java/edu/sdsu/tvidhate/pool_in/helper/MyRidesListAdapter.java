package edu.sdsu.tvidhate.pool_in.helper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.Trip;


public class MyRidesListAdapter extends ArrayAdapter implements SharedConstants{

    private Activity context;
    private List<Trip> userProperties;

    public MyRidesListAdapter(Activity context, int resource, List list) {
        super(context, resource, list);
        this.context = context;
        this.userProperties = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.my_rides_list_item, null);

        TextView source = view.findViewById(R.id.my_ride_source);
        TextView destination = view.findViewById(R.id.my_ride_destination);
        TextView date = view.findViewById(R.id.my_ride_date);
        TextView time = view.findViewById(R.id.my_ride_time);
        TextView uid = view.findViewById(R.id.my_ride_uid);
        TextView status = view.findViewById(R.id.my_ride_status);

        source.setText(userProperties.get(position).getmSourceAddress());
        destination.setText(userProperties.get(position).getmDestinationAddress());
        date.setText(userProperties.get(position).getmStartDate());
        time.setText(userProperties.get(position).getmStartTime());
        uid.setText(userProperties.get(position).getmTripId());
/*        if(userProperties.get(position).isApprovalStatus()){
            status.setText(RIDE_STATUS_COMPLETED);
        }else{
            status.setText(RIDE_SCHEDULED);
        }*/
        status.setText(userProperties.get(position).getmTripStatus());

        return view;
    }




}
