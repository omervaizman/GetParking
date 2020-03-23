package com.example.getparking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class PostAdapter extends ArrayAdapter<ParkingPost>
{
    private Context context;
    private List<ParkingPost> objects;

    public PostAdapter(Context context ,  int textViewResourceId , List<ParkingPost> objects)
    {
        super(context, textViewResourceId , objects);
        this.context = context;
        this.objects = objects;
    }
    @Override
    public View getView(int position , View convertView , ViewGroup parent)
    {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.parking_post, parent, false);
        TextView tvLocation =  view.findViewById(R.id.tvLocation_parking_post);
        TextView tvPhone = view.findViewById(R.id.tvPhone_parking_post);
        TextView tvName =  view.findViewById(R.id.tvName_parkig_post);
        TextView tvPrice =  view.findViewById(R.id.tvPrice_parking_post);
        TextView tvToDate =  view.findViewById(R.id.tv_toDate_parking_post);
        TextView tvFromDate =view.findViewById(R.id.tv_fromDate_parking_post);
        ParkingPost temp = objects.get(position);
        tvFromDate.setText(temp.getFromDate());
        tvName.setText(temp.getName());
        tvPhone.setText(temp.getPhone());
        tvPrice.setText(temp.getPrice());
        tvLocation.setText(temp.getLocation());
        tvToDate.setText(temp.getToDate());
        return view;
    }


}
