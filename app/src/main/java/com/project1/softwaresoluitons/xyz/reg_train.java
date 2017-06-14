package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link reg_train.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link reg_train#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reg_train extends Fragment {

    public ArrayList<item> trainings;
    public ProgressDialog dialog;
    public RequestQueue queue;
    public RecyclerView recyclerView;
    public static Context context;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public reg_train() {


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment reg_train.
     */
    // TODO: Rename and change types and number of parameters
    public static reg_train newInstance(String param1, String param2) {
        reg_train fragment = new reg_train();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        trainings = new ArrayList<item>();

        queue = Volley.newRequestQueue(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_reg_train, container, false);

        return layout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.reg_train);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent i = new Intent(context, training_detail.class);
                        i.putExtra("training_id", trainings.get(position).id);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Intent i = new Intent(context, training_detail.class);
                        i.putExtra("training_id", trainings.get(position).id);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                }));
        context = getActivity().getApplicationContext();
        fetch_trainings();
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void fetch_trainings(){
        final all_train_SQlite msqld=new all_train_SQlite(getContext());
        final SQLiteDatabase sqlite=msqld.getWritableDatabase();
        SharedPreferences sh=getContext().getSharedPreferences("user",Context.MODE_PRIVATE);
        Cursor c=sqlite.query(msqld.TB_name,null,"user_id=?",new String[]{String.valueOf(sh.getInt("usr_id",-1))},null,null,"id");
        if(c.getCount()!=0){
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
                Bitmap b=BitmapFactory.decodeResource(reg_train.context.getResources(), R.mipmap.ic_launcher);
                item object = new item(id, title, location, price ,b,category );

                trainings.add(object);
                c.moveToNext();
            }
            msqld.close();
            Myadapter adapter=new Myadapter(getContext(),trainings);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        }
        else {
            dialog = new ProgressDialog(getContext());
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
                                    Bitmap b=BitmapFactory.decodeResource(reg_train.context.getResources(), R.mipmap.ic_launcher);
                                    item object = new item(id, title, location, price,b ,category);
                                    if(userId==3) {
                                        trainings.add(object);
                                    }

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
                                Myadapter adapter=new Myadapter(getContext(),trainings);
                                //recyclerView=(RecyclerView)getView().findViewById(R.id.reg_train);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));


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
}








