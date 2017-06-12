package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    //Signin button
    public Button google_login,r_button,l_button;
    public EditText u_name,pswrd;
    //Signing Options
    private GoogleSignInOptions gso;

    //google api client
    private GoogleApiClient mGoogleApiClient;
    public ProgressDialog dialog;
    public RequestQueue queue;
    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

    //TextViews

    //Image Loader
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing Views


        //Initializing google signin option
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Initializing signinbutton
        queue = Volley.newRequestQueue(getApplicationContext());
        google_login = (Button) findViewById(R.id.google);
        r_button = (Button) findViewById(R.id.register);
        l_button = (Button) findViewById(R.id.login);
        u_name=(EditText)findViewById(R.id.textView);
        pswrd=(EditText)findViewById(R.id.textView2);
        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        //Setting onclick listener to signing button
        google_login.setOnClickListener(this);
        r_button.setOnClickListener(this);
        l_button.setOnClickListener(this);

    }


    //This function will option signing intent
    private void signIn() {

        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }


    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        dialog.dismiss();
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            String fname=acct.getDisplayName();
            String email=acct.getEmail();
            gf_login(fname,email);
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == google_login) {
            //Calling signin
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait !!");
            dialog.show();
            signIn();
        }
        if(v==l_button){
            if(u_name.getText()==null || pswrd.getText()==null){
                Toast.makeText(this, "Field values should not be left empty", Toast.LENGTH_LONG).show();
            }
            else{
                login(u_name.getText().toString(),pswrd.getText().toString());
            }
        }
        if(v==r_button){
            Intent i=new Intent(this,register.class);
            startActivity(i);
        }
    }
    public void login(final String username, final String password){

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
                            String status=res.getString("login_status");
                            int count=res.getInt("count");
                            if(status.equals("true")){

                                SharedPreferences user=getSharedPreferences("user",MODE_PRIVATE);
                                SharedPreferences.Editor editor=user.edit();
                                editor.putInt("usr_id",res.getInt("usr_id"));
                                editor.putString("name",res.getString("name"));
                                editor.putString("email",res.getString("email"));
                                editor.putString("contact",res.getString("contact"));
                                editor.putString("location",res.getString("location"));
                                editor.putInt("count",count);
                                editor.commit();
                                Toast.makeText(Login.this,"Welcome !!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this,HomeActivity.class));
                            }
                            else{
                                Toast.makeText(Login.this,"login failed",Toast.LENGTH_LONG).show();

                            }

                        } catch (JSONException e) {
                            Toast.makeText(Login.this,"login failed",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this,"login failed",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type","login");
                params.put("e-mail",username);
                params.put("password",password);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void gf_login(final String name,final String email){
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

                            int first_time=res.getInt("first_time");
                            if(first_time==0){
                                SharedPreferences user=getSharedPreferences("user",MODE_PRIVATE);
                                SharedPreferences.Editor editor=user.edit();
                                int id=res.getInt("usr_id");
                                editor.putInt("usr_id",id);
                                editor.putString("name",name);
                                editor.putString("email",email);
                                editor.putString("contact",res.getString("contact"));
                                editor.putString("location",res.getString("location"));
                                editor.putInt("count",res.getInt("count"));
                                editor.commit();
                                Toast.makeText(Login.this,"Welcome !!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this,HomeActivity.class));
                                finish();
                            }
                            else if(first_time==1){
                                Intent i=new Intent(Login.this,gf_login.class);
                                i.putExtra("name",name);
                                i.putExtra("email",email);
                                startActivity(i);
                                finish();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Login.this,"login failed",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this,"login failed",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type","gf_login");
                params.put("fname",name);
                params.put("email",email);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}