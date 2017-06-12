package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public ArrayList<item> trainings,items;
    public ProgressDialog dialog;
    public RequestQueue queue;
    public RecyclerView recyclerView;
    public myadapter adapter;
    public static String cat_nam;
    public ArrayList<item> sorted_by_category;
    public ArrayList<String> categories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reg_train);
        trainings=new ArrayList<item>();
        categories=new ArrayList<>();
        queue = Volley.newRequestQueue(getApplicationContext());
        recyclerView=(RecyclerView)findViewById(R.id.reg_train);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent i = new Intent(Main2Activity.this, training_detail.class);
                        i.putExtra("training_id", items.get(position).id);
                        i.putExtra("calling_activity",1);
                        Log.i("training_id", items.get(position).id+"");
                        startActivity(i);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Intent i = new Intent(Main2Activity.this, training_detail.class);
                        i.putExtra("training_id", items.get(position).id);
                        i.putExtra("calling_activity",1);
                        startActivity(i);
                    }
                }));
        getSupportActionBar().setTitle("Search Trainings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        fetch_trainings();
        //registerForContextMenu(recyclerView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                if (data == null) {
                    Toast.makeText(this, "No category choosen", Toast.LENGTH_LONG);
                } else {
                    String c = data.getStringExtra("category");
                    sorted_by_category=new ArrayList<>();
                    for(int i=0;i<trainings.size();i++){
                        if(trainings.get(i).category.equals(c)){
                            sorted_by_category.add(trainings.get(i));
                        }
                    }
                    items=sorted_by_category;
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.srt_by_price:
                // app icon in action bar clicked; go home
                Collections.sort(items, new Comparator<item>() {
                    @Override
                    public int compare(item lhs, item rhs) {
                        Float l=Float.parseFloat(lhs.price);
                        Float r=Float.parseFloat(rhs.price);
                        return l.compareTo(r);
                    }
                });
                adapter.notifyDataSetChanged();
                Toast.makeText(this,"Highest price first",Toast.LENGTH_LONG);
                return true;
            case R.id.srt_by_name:
                // app icon in action bar clicked; go home
                Collections.sort(items, new Comparator<item>() {
                    @Override
                    public int compare(item lhs, item rhs) {
                        return lhs.title.toLowerCase().compareTo(rhs.title.toLowerCase());
                    }
                });
                adapter.notifyDataSetChanged();
                return true;
            case R.id.srt_by_category:
                // app icon in action bar clicked; go home
               fetch_categories();
                return true;
            case R.id.all_trainings:
                // app icon in action bar clicked; go home
                items=trainings;
                adapter.notifyDataSetChanged();
                Toast.makeText(this,"Highest price first",Toast.LENGTH_LONG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        adapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        return true;
    }

    public void fetch_categories(){
        dialog = new ProgressDialog(Main2Activity.this);
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
                                String[] cat = new String[categories.size()];
                                cat = categories.toArray(cat);
                                Bundle b=new Bundle();
                                b.putStringArray("categories", cat);
                                Intent i=new Intent(Main2Activity.this, sel_cat.class);
                                i.putExtras(b);

                                startActivityForResult(i, 1);

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

    public void fetch_trainings(){
        dialog = new ProgressDialog(Main2Activity.this);
        dialog.setMessage("Please wait !!");
        dialog.show();
        final all_train_SQlite msqld=new all_train_SQlite(getApplicationContext());
        final SQLiteDatabase sqlite=msqld.getWritableDatabase();
        Cursor c=sqlite.query(msqld.TB_name,null,null,null,null,null,"id");
        if(c.getCount()!=0){
            dialog.dismiss();
            c.moveToFirst();
            while(!c.isAfterLast()){
                int id = c.getInt(c.getColumnIndex("id"));
                int userId = c.getInt(c.getColumnIndex("user_id"));
                String title = c.getString(c.getColumnIndex("title"));
                String price = String.valueOf(c.getFloat(c.getColumnIndex("price")));
                String location = c.getString(c.getColumnIndex("location"));
                String category = c.getString(c.getColumnIndex("category"));
                //  String img_base_64 = c.getString(c.getColumnIndex("img_base_64"));
                //byte[] decodedString = Base64.decode(img_base_64, Base64.DEFAULT);
                // Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Bitmap b= BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                item object = new item(id, title, location, price,b ,category);
                trainings.add(object);
                c.moveToNext();
            }
            msqld.close();
            adapter=new myadapter(getApplicationContext(),trainings);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        }
        else {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_ROOT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();

                            Log.i("response",response);
                            try {
                                JSONObject res = new JSONObject(response);
                                JSONArray thread = res.getJSONArray("trainings");
                                for (int i = 0; i < thread.length(); i++) {
                                    JSONObject obj = thread.getJSONObject(i);
                                    int id = obj.getInt("id");
                                    int userId = obj.getInt("user_id");
                                    String title = obj.getString("title");
                                    String price =obj.getString("price");
                                    String location = obj.getString("location");
                                    String category = obj.getString("category");
//                                    String img_base_64 = obj.getString("img_base_64");
                                    //                                  byte[] decodedString = Base64.decode(img_base_64, Base64.DEFAULT);
                                    //                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                    Bitmap b=BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                                    item object = new item(id, title, location, price,b ,category);

                                    trainings.add(object);


                                    ContentValues c=new ContentValues();
                                    c.put("id",String.valueOf(id));
                                    c.put("user_id",userId);
                                    c.put("title",title);
                                    c.put("price",price);
                                    // c.put("img_base_64",img_base_64);
                                    c.put("location",location);
                                    c.put("category",category);

                                    sqlite.insert(msqld.TB_name,null,c);
                                }
                                msqld.close();
                                adapter=new myadapter(getApplicationContext(),trainings);
                                //recyclerView=(RecyclerView)getView().findViewById(R.id.reg_train);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));


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
                    params.put("type","trainings");
                    return params;
                }
            };
            queue.add(stringRequest);
        }
    }
    class myadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<item> trainings;
        Context context;
        LayoutInflater l;

        myadapter(Context c, ArrayList<item> t) {
            this.context = c;
            this.trainings = t;
            items=new ArrayList<item>();
            items=t;
            l=LayoutInflater.from(context);
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = l.inflate(R.layout.training_item, null,false);
            viewHolder vh = new viewHolder(v);
            return vh;

        }

        /** This will be invoked when an item in the listview is long pressed */


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
            viewHolder holder=(viewHolder)holder1;
            holder.title.setText(items.get(position).title);

            holder.price.setText("Rs. "+items.get(position).price);
            Bitmap y = getRoundedShape(items.get(position).b);
            holder.img.setImageBitmap(y);
            holder.location.setText(items.get(position).location);
            holder.contact.setText(items.get(position).contact);

        }

        public void filter(String text) {
            ArrayList<item> temp=new ArrayList<item>();
            if(text.isEmpty()){
                for(int i=0;i < trainings.size();i++){
                    temp.add(trainings.get(i));
                }
                items=temp;
            } else{
                text = text.toLowerCase();
                for(int i=0;i < trainings.size();i++){
                    if(trainings.get(i).title.toLowerCase().contains(text)){
                        temp.add(trainings.get(i));
                    }
                }
                items=temp;
            }
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return items.size();
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

    }}


