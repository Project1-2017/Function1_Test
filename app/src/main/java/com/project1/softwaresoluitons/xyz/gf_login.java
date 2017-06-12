package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class gf_login extends AppCompatActivity implements View.OnClickListener{
    public EditText location1,mobile1;
    public Button submit;
    public ProgressDialog dialog;
    public RequestQueue queue;
    public int user_id;
    public String name,email;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getApplicationContext());
        getSupportActionBar().setTitle("Your Details");
        setContentView(R.layout.activity_gf_login);
        SharedPreferences sh=getSharedPreferences("user",MODE_PRIVATE);
        user_id=sh.getInt("usr_id",0);
        Intent i=getIntent();
        name=i.getStringExtra("name");
        email=i.getStringExtra("email");
        location1=(EditText)findViewById(R.id.location1);
        mobile1=(EditText)findViewById(R.id.mobile1);
        submit=(Button)findViewById(R.id.reg);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==submit){
            if(location1.getText()==null ||mobile1.getText()==null){
                Toast.makeText(this, "Field values should not be left empty", Toast.LENGTH_LONG).show();
            }
            else{
                register(location1.getText().toString(),mobile1.getText().toString());
            }
        }
    }
    public void register(final String location,final String mobile){

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
                            int id=res.getInt("usr_id");
                            SharedPreferences user=getSharedPreferences("user",MODE_PRIVATE);
                            SharedPreferences.Editor editor=user.edit();
                            editor.putString("contact",mobile);
                            editor.putInt("usr_id",id);
                            editor.putString("name",name);
                            editor.putString("email",email);
                            editor.putString("location",location);
                            editor.commit();
                            finish();
                            startActivity(new Intent(gf_login.this,HomeActivity.class));
                            Toast.makeText(gf_login.this,"Welcome !!",Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Toast.makeText(gf_login.this,"Login Unsuccessful!!",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(gf_login.this,"Login failed",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type","gf_register");
                params.put("location",location);
                params.put("mobile",mobile);
                params.put("name",name);
                params.put("email",email);
                return params;
            }
        };
        queue.add(stringRequest);
    }

}
