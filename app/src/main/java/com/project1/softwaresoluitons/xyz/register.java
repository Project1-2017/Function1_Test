package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.ContentValues;
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

public class register extends AppCompatActivity implements View.OnClickListener{
    public EditText f_name1,l_name1,location1,mobile1,email1,pswrd1,cpswrd1;
    public Button register;
    public ProgressDialog dialog;
    public RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getApplicationContext());
        setContentView(R.layout.activity_register);
        f_name1=(EditText)findViewById(R.id.f_name1);
        l_name1=(EditText)findViewById(R.id.l_name1);
        location1=(EditText)findViewById(R.id.location1);
        mobile1=(EditText)findViewById(R.id.mobile1);
        email1=(EditText)findViewById(R.id.email1);
        pswrd1=(EditText)findViewById(R.id.pswrd1);
        cpswrd1=(EditText)findViewById(R.id.cpswrd1);
        register=(Button)findViewById(R.id.reg);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==register){
            if(f_name1.getText()==null|| l_name1.getText()==null ||location1.getText()==null ||mobile1.getText()==null
                    ||pswrd1.getText()==null ||cpswrd1.getText()==null || email1.getText()==null  ){
                Toast.makeText(this, "Field values should not be left empty", Toast.LENGTH_LONG).show();
            }
            else if(!pswrd1.getText().toString().equals(cpswrd1.getText().toString())){
                Toast.makeText(this, "Confirmed password do not match", Toast.LENGTH_LONG).show();
            }
            else{
                register(f_name1.getText().toString(),l_name1.getText().toString(),location1.getText().toString(),
                        mobile1.getText().toString(),email1.getText().toString(),pswrd1.getText().toString());
            }
        }
    }
    public void register(final String fname,final String lname,final String location,final String mobile,final String email, final String password){

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
                            String status=res.getString("register_status");
                            if(status.equals("true")){
                                Toast.makeText(register.this,"registration successful",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(register.this,"registration failed",Toast.LENGTH_LONG).show();

                            }

                        } catch (JSONException e) {
                            Toast.makeText(register.this,"registration failed",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(register.this,"registration failed",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type","register");
                params.put("fname",fname);
                params.put("lname",lname);
                params.put("location",location);
                params.put("mobile",mobile);
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };
        queue.add(stringRequest);
    }

}
