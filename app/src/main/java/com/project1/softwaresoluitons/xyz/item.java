package com.project1.softwaresoluitons.xyz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by root on 11/5/17.
 */
public class item {
        String title;
        String location;
        String price;
        String contact;
        Bitmap b;
        int id;

        item(int id, String title,String location,String price,Bitmap b) {
            this.title=title;
            this.location=location;
            this.price="Rs. "+price;
            this.contact="NULL";
            this.b = b;
            Log.i("b",b+"");
            this.id = id;
        }

}
