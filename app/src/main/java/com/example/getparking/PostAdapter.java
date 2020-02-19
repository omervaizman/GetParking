package com.example.getparking;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class PostAdapter extends ArrayAdapter<ParkingPost> {
    //data structure to create the list view in the find and manage parking activities.
    Context context;
    List<ParkingPost> objects;

    public PostAdapter(Context context , int resource , int textViewResourceId , List<ParkingPost> objects)
    {
        super(context, textViewResourceId , objects);
        this.context = context;
        this.objects = objects;
    }
    @Override
    public View getView(int position , View convertView , ViewGroup parent)
    {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.parking_post, parent, false);
        TextView tvLocation = (TextView) view.findViewById(R.id.tvLocation_parking_post);
        //ImageView ivPict = (ImageView) view.findViewById(R.id.iv_parking_post_parkicPic);
        TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone_parking_post);
        TextView tvName = (TextView) view.findViewById(R.id.tvName_parkig_post);
        TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice_parking_post);
        TextView tvToDate = (TextView) view.findViewById(R.id.tv_toDate_parking_post);
        TextView tvFromDate = (TextView) view.findViewById(R.id.tv_fromDate_parking_post);
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
