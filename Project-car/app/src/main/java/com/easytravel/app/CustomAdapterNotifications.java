package com.easytravel.app;

import android.content.Context;
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

import static com.easytravel.app.MainActivity.context;
import static com.easytravel.app.MainActivity.ipServer;
import static com.easytravel.app.MainActivity.loggato;
import static com.easytravel.app.MainActivity.notifications;

public class CustomAdapterNotifications extends ArrayAdapter<Notification> implements View.OnClickListener,AsyncResponse,RatingBar.OnRatingBarChangeListener{
    private ArrayList<Notification> dataSet;
    Context mContext;
    @Override
    public void processFinish(String output) {
        try {
            JSONObject obj = new JSONObject(output);
            if(obj.getBoolean("IsError")) {
                Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_LONG).show();
                return;
            }
            switch (obj.getString("Function"))
            {
                case "deleteContact": {
                    break;
                }
            }
        }
        catch (Exception e){
            Log.e("Errore",e.getMessage());
        }
    }
    private static class ViewHolder {
        TextView txtName;
        TextView txtDate;
        RatingBar rbRating;
        ImageView delete;
    }
    public CustomAdapterNotifications(ArrayList<Notification> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
    }
    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Notification notification=(Notification)object;
        switch (v.getId())
        {
            case R.id.btndeletecontact:{
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String finaldate = sdf.format(notification.getDatetime());
                funcPHP("deleteContact", String.format("{\"caller\":\"%s\",\"receiver\":\"%s\",\"datetime\":\"%s\"}", notification.getMobile(),loggato.getMobile(), finaldate));
                dataSet.remove(position);
                this.notifyDataSetChanged();
                Snackbar.make(v, "Contatto eliminato", Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
            }
        }
    }
    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        int position=(Integer) ratingBar.getTag();
        Object object= getItem(position);
        Notification notification=(Notification)object;
        switch (ratingBar.getId())
        {
            case R.id.ntfrbrating:
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String finaldate = sdf.format(notification.getDatetime());
                funcPHP("setFeedback", String.format("{\"caller\":\"%s\",\"receiver\":\"%s\",\"datetime\":\"%s\",\"feedback\":\"%s\"}", notification.getMobile(),loggato.getMobile(), finaldate,(int)rating));
                Snackbar.make(ratingBar, "Feedback per "+notification.getName().toString()+": "+(int)rating, Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }
    public void funcPHP(String function,String json){
        CallAPI asyncTask = new CallAPI();
        asyncTask.delegate = this;
        asyncTask.execute(ipServer,function,json);
    }
    private int lastPosition = -1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtnamesurname);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtdatetime);
            viewHolder.rbRating = (RatingBar) convertView.findViewById(R.id.ntfrbrating);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.btndeletecontact);
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
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String finaldate = sdf.format(dataModel.getDatetime());
        viewHolder.txtDate.setText(finaldate);
        viewHolder.rbRating.setOnRatingBarChangeListener(this);
        viewHolder.rbRating.setTag(position);
        viewHolder.delete.setOnClickListener(this);
        viewHolder.delete.setTag(position);
        return convertView;
    }
}