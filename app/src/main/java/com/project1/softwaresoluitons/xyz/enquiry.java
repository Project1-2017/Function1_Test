package com.project1.softwaresoluitons.xyz;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class enquiry extends Activity implements View.OnClickListener {
    public EditText message;
    public Button send;
    public RequestQueue queue;
    public  ProgressDialog dialog;
    public int training_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);
        setTitle("Enquiry:-");
        Intent i=getIntent();
        training_id=Integer.parseInt(i.getStringExtra("training_id"));
        queue = Volley.newRequestQueue(getApplicationContext());
        message=(EditText)findViewById(R.id.enquiry);
        send=(Button)findViewById(R.id.send);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==send){
            String s=message.getText().toString();
            if(s.length()>100){
                Toast.makeText(this,"message length exceeded maximum limit",Toast.LENGTH_LONG).show();
            }
            else if(s.length()==0){
                Toast.makeText(this,"message field epmty",Toast.LENGTH_LONG).show();
            }
            else{
                send_enquiry(s);
            }
        }
    }

    public void send_enquiry(final String s){
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait !!");
            dialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_ROOT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            Log.i("response",response);
                            finish();
                            Toast.makeText(enquiry.this,"Message send!!",Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            finish();
                            Toast.makeText(enquiry.this,"Message not send!!",Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    SharedPreferences sh=getSharedPreferences("user",MODE_PRIVATE);
                    params.put("type","enquiry");
                   /* params.put("name",sh.getString("name",null));
                    params.put("email",sh.getString("email",null));
                    params.put("contact",sh.getString("contact",null));  */
                    params.put("message",s);
                    params.put("training_id",training_id+"");
                    params.put("user_id",sh.getInt("usr_id",0)+"");
                    return params;
                }
            };
            queue.add(stringRequest);
    }

}
