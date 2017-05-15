package com.project1.softwaresoluitons.xyz;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by root on 11/5/17.
 */
public class viewHolder extends RecyclerView.ViewHolder{
    ImageView img;
    TextView title;
    TextView location;
    TextView price;
    TextView contact;


    viewHolder(View v) {
        super(v);
        img = (ImageView) v.findViewById(R.id.imageView);
        title = (TextView) v.findViewById(R.id.title);
        location = (TextView) v.findViewById(R.id.location);
        price=(TextView)v.findViewById(R.id.price);
        contact=(TextView)v.findViewById(R.id.contact);
    }
}

