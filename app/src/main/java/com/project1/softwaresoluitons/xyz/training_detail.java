package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class training_detail extends AppCompatActivity {
    public TextView key_learning10,key_learning20,trainer10,trainer20,time_venue10,time_venue20,time_venue30,time_venue40,pre10;
    String id,title,k_l1,k_l2,k_l3,price,mobile_no,name,venue,category,available,from,to,date,description,pre;
    public ProgressDialog dialog;
    public RequestQueue queue;
    public Bitmap b;
    public ImageView img;
    public CollapsingToolbarLayout collapsingToolbar;
    public ArrayList<training_detail_item> items;
    public RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_detail);
        items=new ArrayList<>();
        Intent i=getIntent();
        id=String.valueOf(i.getIntExtra("training_id",0));
        img=(ImageView)findViewById(R.id.header);
        queue = Volley.newRequestQueue(getApplicationContext());
        Log.i("training_id",""+id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        recyclerView=(RecyclerView)findViewById(R.id.rview);
        fetch_trainings();

    }
    public void fetch_trainings(){
        dialog = new ProgressDialog(training_detail.this);
        dialog.setMessage("Please wait !!");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_ROOT,
                new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();

                            Log.i("response11",response);
                            try {
                                JSONObject res = new JSONObject(response);
                                JSONArray thread = res.getJSONArray("training_item");
                                    JSONObject obj = thread.getJSONObject(0);
                                    title = obj.getString("title");
                                    k_l1 =obj.getString("k_l1");
                                    k_l2 =obj.getString("k_l2");
                                    k_l3 =obj.getString("k_l3");
                                    price = obj.getString("price");
                                    name = obj.getString("name");
                                    mobile_no = obj.getString("contact");
                                    venue = obj.getString("venue");
                                    category = obj.getString("category");
                                    available = obj.getString("available");
                                    from = obj.getString("from");
                                    to = obj.getString("to");
                                    date= obj.getString("date");
                                    description = obj.getString("description");
                                    pre = obj.getString("pre");
                                    b= BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                                    if(k_l1.equals("null")||k_l2.equals("null")||k_l3.equals("null")){
                                        items.add(new training_detail_item("Key Learnings","null"));
                                    }
                                    else{
                                        items.add(new training_detail_item("Key Learnings","1. "+k_l1+"\n2. "+k_l2+"\n3. "+k_l3));
                                    }
                                    items.add(new training_detail_item("Description",description));
                                    items.add(new training_detail_item("Contact",name));
                                    items.add(new training_detail_item("Mobile No",mobile_no));
                                    items.add(new training_detail_item("Availability",available));
                                    items.add(new training_detail_item("Date",date));
                                    if(from.equals("null")||to.equals("null")){
                                        items.add(new training_detail_item("Timmings","null"));
                                    }
                                    else{
                                        items.add(new training_detail_item("Timmings",from+" - "+to));

                                    }
                                    items.add(new training_detail_item("Venue",venue));
                                    items.add(new training_detail_item("Pre-requisite",pre));

                                collapsingToolbar.setTitle(title);
                                img.setImageBitmap(b);
                                adapter adapter=new adapter(training_detail.this,items);

                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(training_detail.this));

                            } catch (JSONException e) {
                                Log.i("response111",response);
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
                    params.put("type","training_item");
                    params.put("training_id",id);

                    return params;
                }
            };
            queue.add(stringRequest);
        }
}

class adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<training_detail_item> trainings;
    Context context;
    LayoutInflater l;

    adapter(Context c, ArrayList<training_detail_item> t) {
        this.context = c;
        this.trainings = t;
        l=LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = l.inflate(R.layout.training_detail_item, parent,false);
        holder vh = new holder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        holder holder=(holder)holder1;
        holder.title.setText(trainings.get(position).title);
        if(!trainings.get(position).description.equals("null")){
            holder.description.setText(trainings.get(position).description);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return trainings.size();
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 120;
        int targetHeight = 120;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }


}

class holder extends RecyclerView.ViewHolder{

    TextView title;
    TextView description;


    holder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.title);
        description = (TextView) v.findViewById(R.id.description);
    }
}

class training_detail_item{
    String title;
    String description;
    training_detail_item(String t,String t1){
        title=t;
        description=t1;
    }
}
