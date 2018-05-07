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
import edu.sdsu.tvidhate.pool_in.entity.Request;
import edu.sdsu.tvidhate.pool_in.entity.User;


public class RequestsAdapter extends ArrayAdapter implements SharedConstants{
    private Activity context;
    private List<Request> userProperties;
    public RequestsAdapter(Activity context, int resource, List list) {
        super(context, resource, list);
        this.context = context;
        this.userProperties = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View view = inflater.inflate(R.layout.request_list_item, null);

        TextView requestorName = view.findViewById(R.id.request_list_requestor_name);
        TextView requestorContact = view.findViewById(R.id.request_list_requestor_contact);
        TextView status = view.findViewById(R.id.request_list_approval);

        /*requestorName.setText(userProperties.get(position).getRequestorName());
        requestorContact.setText(userProperties.get(position).getRequestorContact());*/

        User currentRequester = userProperties.get(position).getmJoinTripRequester();
        requestorName.setText(currentRequester.getFullName());
        requestorContact.setText(currentRequester.getmContactNumber());

        if(userProperties.get(position).isApprovalStatus()){
            status.setText(REQUEST_STATUS_APPROVED);
        }else{
            status.setText(REQUEST_STATUS_WAITING);
        }

        return view;
    }
}
