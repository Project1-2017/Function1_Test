package com.project1.softwaresoluitons.xyz;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class reply extends Activity implements View.OnClickListener {
    public EditText message;
    public Button send;
    public RequestQueue queue;
    public  ProgressDialog dialog;
    public String tr_title,name,email,contact,from_name,from_email;
    public int position,id;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        setTitle("Reply:-");
        Intent i=getIntent();
        SharedPreferences sh=getSharedPreferences("user",MODE_PRIVATE);
        from_name=sh.getString("name",null);
       position=i.getIntExtra("position",-1);
        from_email=sh.getString("email",null);
        tr_title=i.getStringExtra("tr_title");
        name=i.getStringExtra("to_name");
        contact=i.getStringExtra("to_contact");
        email=i.getStringExtra("to_email");
        id=i.getIntExtra("id",-1);
        Log.i("id",id+"");
        queue = Volley.newRequestQueue(getApplicationContext());
        message=(EditText)findViewById(R.id.message);
        send=(Button)findViewById(R.id.send);
        send.setOnClickListener(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {android.Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
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
                send_email(s);
            }
        }
    }

    public void send_email(final String s){
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
                        String ss="Hello "+name+" !\n"+"You have received a mail in response to your enquiry for\n\nTraining: "+tr_title+"\n"+"Trainer Name: "+from_name;
                       /*
                        SmsManager sms = SmsManager.getDefault();
                        ArrayList<String> parts = sms.divideMessage(ss);
                        sms.sendMultipartTextMessage(contact, null, parts, null,null);*/
                        tra_not.notifications.get(position).reply_status=1;
                        tra_not.adapter.notifyDataSetChanged();
                        Toast.makeText(reply.this,"Replied successfully through E-mail and SMS!!",Toast.LENGTH_LONG).show();
                        SharedPreferences sh=getSharedPreferences("user",MODE_PRIVATE);
                        SharedPreferences.Editor e=sh.edit();
                        e.putInt("count",sh.getInt("count",0)-1);
                        e.commit();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(reply.this,"Message not send!!",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences sh=getSharedPreferences("user",MODE_PRIVATE);
                params.put("type","email");
                   /* params.put("name",sh.getString("name",null));
                    params.put("email",sh.getString("email",null));
                    params.put("contact",sh.getString("contact",null));  */
                params.put("from_name",from_name);
                params.put("to_name",name);
                params.put("tr_title",tr_title);
                params.put("to_email",email);
                params.put("from_email",from_email);
                params.put("message",s);
                params.put("id",id+"");
                return params;
            }
        };
        queue.add(stringRequest);
    }

}
