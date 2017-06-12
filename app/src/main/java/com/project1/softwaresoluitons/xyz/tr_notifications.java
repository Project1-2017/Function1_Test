package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class tr_notifications extends AppCompatActivity {
    public RequestQueue queue;
    public ProgressDialog dialog;
    public RecyclerView recyclerView;
    public  static ArrayList<notification_item> notifications;
    public static n_adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tr_notifications);
        queue = Volley.newRequestQueue(getApplicationContext());
        recyclerView=(RecyclerView)findViewById(R.id.rview);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        getSupportActionBar().setTitle("Trainer Notifications");
        notifications=new ArrayList<>();
        fetch_notifications();
    }
    public void fetch_notifications(){

            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait !!");
            dialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_ROOT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("response",response);
                            try {
                                JSONObject res = new JSONObject(response);
                                JSONArray thread = res.getJSONArray("notifications");
                                for (int i = 0; i < thread.length(); i++) {
                                    JSONObject obj = thread.getJSONObject(i);
                                    int id = obj.getInt("id");
                                    String name = obj.getString("name");
                                    String contact =obj.getString("contact");
                                    String email = obj.getString("email");
                                    String d_t = obj.getString("date_time");
                                    String message = obj.getString("message");
                                    String title = obj.getString("title");
                                    notifications.add(new notification_item(d_t,name,message,email,contact,title,id));
                                }
                                adapter=new n_adapter(tr_notifications.this,notifications);
                                //recyclerView=(RecyclerView)getView().findViewById(R.id.reg_train);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(tr_notifications.this));
                                dialog.dismiss();

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
                    SharedPreferences sh=getSharedPreferences("user",MODE_PRIVATE);
                    params.put("type","notifications");
                    params.put("training_id",sh.getInt("usr_id",0)+"");
                    return params;
                }
            };
            queue.add(stringRequest);
        }

}

class notification_item{
    String date_time,name,message,email,contact,tr_title;
    int id,reply_status;
    notification_item(String date_time,String name,String message, String email,String contact,String tr_title,int id){
        this.date_time=date_time;
        this.contact=contact;
        this.email=email;
        this.name=name;
        this.message=message;
        this.tr_title=tr_title;
        this.id=id;
    }
}

class n_holder extends RecyclerView.ViewHolder{

    TextView name,message,date_time,contact,email,tr_title;
    Button reply;
    n_holder(View v) {
        super(v);
        name = (TextView) v.findViewById(R.id.name);
        message= (TextView) v.findViewById(R.id.message);
        contact= (TextView) v.findViewById(R.id.contact);
        email= (TextView) v.findViewById(R.id.email);
        date_time= (TextView) v.findViewById(R.id.date_time);
        tr_title=(TextView) v.findViewById(R.id.tr_title);
        reply=(Button) v.findViewById(R.id.reply);
    }
}
class n_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<notification_item> notifications;
    Context context;
    LayoutInflater l;
    ProgressDialog dialog;
    RequestQueue queue;

    n_adapter(Context c, ArrayList<notification_item> t) {
        this.context = c;
        this.notifications = t;
        l= LayoutInflater.from(context);
        queue=Volley.newRequestQueue(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = l.inflate(R.layout.notification_item, parent,false);
         n_holder vh = new n_holder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
        n_holder holder=(n_holder)holder1;
        holder.name.setText(notifications.get(position).name);
        holder.message.setText(notifications.get(position).message);
        holder.date_time.setText(notifications.get(position).date_time);
        holder.contact.setText(notifications.get(position).contact);
        holder.email.setText(notifications.get(position).email);
        holder.tr_title.setText(notifications.get(position).tr_title);
        if(notifications.get(position).reply_status==1){
            holder.reply.setText("Already Replied");
            int whiteColor = Color.parseColor("#FFFFFF");
            int greenColor = Color.parseColor("#99cc00");
            holder.reply.setBackgroundColor(greenColor);
            holder.reply.setTextColor(whiteColor);
            holder.reply.setEnabled(false);
        }
        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, reply.class);
                i.putExtra("tr_title", notifications.get(position).tr_title);
                i.putExtra("to_email", notifications.get(position).email);
                i.putExtra("to_name",notifications.get(position).name);
                i.putExtra("to_contact",notifications.get(position).contact);
                i.putExtra("id",notifications.get(position).id);
                i.putExtra("position",position);
                context.startActivity(i);
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

}
