package com.project1.softwaresoluitons.xyz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class crt_training extends Activity implements View.OnClickListener{
    private EditText  name,location,price,duration,description,kl1,kl2,kl3,date;
    private Button crt_training;
    private Spinner category,availability;
    public ProgressDialog dialog;
    public RequestQueue queue;
    public CollapsingToolbarLayout collapsingToolbar;
    public ArrayList<String> categories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crt_training);
        queue= Volley.newRequestQueue(getApplicationContext());
        name=(EditText)findViewById(R.id.name1);
        location=(EditText)findViewById(R.id.location1);
        price=(EditText)findViewById(R.id.price1);
        duration=(EditText)findViewById(R.id.duration1);
        description=(EditText)findViewById(R.id.description1);
        crt_training=(Button)findViewById(R.id.rgstr);
        category=(Spinner)findViewById(R.id.category1);
        kl1=(EditText)findViewById(R.id.kl1_1);
        kl2=(EditText)findViewById(R.id.kl2_1);
        kl3=(EditText)findViewById(R.id.kl3_1);
        date=(EditText)findViewById(R.id.date_1);
        availability=(Spinner)findViewById(R.id.availability1);
        crt_training.setOnClickListener(this);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Create Training");
        categories=new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this,R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crt_training.this.finish();
                startActivity(new Intent(crt_training.this,HomeActivity.class));
            }
        });
        String[] availabilities=getResources().getStringArray(R.array.availability);
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, availabilities);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availability.setAdapter(dataAdapter1);
        fetch_categories();
    }


    @Override
    public void onClick(View v) {
        if(v==crt_training){
            if(name.getText().toString().equals("")||location.getText().toString().equals("")||price.getText().toString().equals("")
                    ||duration.getText().toString().equals("")||description.getText().toString().equals("")||category.getSelectedItem().toString().equals("")||
                    availability.getSelectedItem().toString().equals("")||date.getText().toString().equals("")){
                //do nothing;
                Toast.makeText(this,"only optional fields can be left empty",Toast.LENGTH_LONG).show();
            }
            else{
                Date d = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                    d = sdf.parse(date.getText().toString());
                    if (!date.getText().toString().equals(sdf.format(d))) {
                        d = null;
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                if (d == null) {
                    Toast.makeText(this,"Date format is invalid ",Toast.LENGTH_LONG).show();
                } else {
                    crt_training();
                }

            }
        }
    }

    public void fetch_categories(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait !!");
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_ROOT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        Log.i("response",response);
                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray thread = res.getJSONArray("categories");
                            for (int i = 0; i < thread.length(); i++) {
                                JSONObject obj = thread.getJSONObject(i);
                                String category = obj.getString("category");
                                categories.add(category);
                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(crt_training.this,
                                    android.R.layout.simple_spinner_item, categories);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            category.setAdapter(dataAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type","categories");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void crt_training(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait !!");
        dialog.show();
        final all_train_SQlite msqld=new all_train_SQlite(this);
        final SQLiteDatabase sqlite=msqld.getWritableDatabase();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_ROOT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            Toast.makeText(crt_training.this,"Training created",Toast.LENGTH_LONG).show();
                            Log.i("response",response);
                            try {
                                JSONObject res = new JSONObject(response);
                                int id = res.getInt("id");
                                ContentValues c=new ContentValues();
                                c.put("id",String.valueOf(id));
                                SharedPreferences s=getSharedPreferences("user",MODE_PRIVATE);
                                c.put("user_id",Integer.toString(s.getInt("usr_id",0)));
                                c.put("title",name.getText().toString());
                                c.put("price",price.getText().toString());
                                // c.put("img_base_64",img_base_64);
                                c.put("location",location.getText().toString());
                                c.put("category",category.getSelectedItem().toString());
                                sqlite.insert(msqld.TB_name,null,c);

                                msqld.close();
                                crt_training.this.finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(crt_training.this,"Training not created",Toast.LENGTH_LONG);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("type","create_training");
                    SharedPreferences s=getSharedPreferences("user",MODE_PRIVATE);
                    params.put("name",name.getText().toString());
                    params.put("user_id",String.valueOf(s.getInt("usr_id",0)));
                    params.put("location",location.getText().toString());
                    params.put("price",price.getText().toString());
                    params.put("duration",duration.getText().toString());
                    params.put("description",description.getText().toString());
                    params.put("category",category.getSelectedItem().toString());
                    params.put("availability",availability.getSelectedItem().toString());
                    params.put("kl1",kl1.getText().toString());
                    params.put("kl2",kl2.getText().toString());
                    params.put("kl3",kl3.getText().toString());
                    params.put("date",date.getText().toString());
                    return params;
                }
            };
            queue.add(stringRequest);
        }
}
