package com.easytravel.app;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.easytravel.app.MainActivity.activity;
import static com.easytravel.app.MainActivity.context;
import static com.easytravel.app.MainActivity.ipServer;
import static com.easytravel.app.MainActivity.loggato;
import static com.easytravel.app.MainActivity.notifications;

public class CustomAdapterAutostoppisti extends ArrayAdapter<Autostoppista> implements View.OnClickListener{
    private ArrayList<Autostoppista> dataSet;
    Context mContext;
    private static class ViewHolder {
        TextView txtName;
        TextView txtFrom;
        TextView txtTo;
        ImageView options;
    }
    public CustomAdapterAutostoppisti(ArrayList<Autostoppista> data, Context context) {
        super(context, R.layout.row_item_autostoppista, data);
        this.dataSet = data;
        this.mContext=context;
    }
    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Autostoppista notification=(Autostoppista)object;
        switch (v.getId())
        {
            case R.id.btnoptions:{
                activity.openContextMenu(v);
                break;
            }
        }
    }
    private int lastPosition = -1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Autostoppista dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_autostoppista, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtnamesurname);
            viewHolder.txtFrom = (TextView) convertView.findViewById(R.id.txtfrom);
            viewHolder.txtTo = (TextView) convertView.findViewById(R.id.txtto);
            viewHolder.options = (ImageView) convertView.findViewById(R.id.btnoptions);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.txtName.setText(dataModel.getName()+" "+dataModel.getSurname());
        Geocoder geocoder;
        List<Address> addresses;
        try {
            geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(dataModel.getLatitude(), dataModel.getLongitude(), 1);
            viewHolder.txtFrom.setText("Da: "+addresses.get(0).getAddressLine(0));
            geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(dataModel.getDestlat(), dataModel.getDestlon(), 1);
            viewHolder.txtTo.setText("A: "+addresses.get(0).getAddressLine(0));
        }catch (Exception e){
            Log.e("Error",e.getMessage());
        }
        viewHolder.options.setOnClickListener(this);
        viewHolder.options.setTag(position);
        return convertView;
    }
}