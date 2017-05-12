package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link reg_train1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link reg_train1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reg_train1 extends Fragment {
    public ArrayList<String> training_ids;
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

    public reg_train1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment reg_train1.
     */
    // TODO: Rename and change types and number of parameters
    public static reg_train1 newInstance(String param1, String param2) {
        reg_train1 fragment = new reg_train1();
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
        trainings=new ArrayList<item>();
        queue = Volley.newRequestQueue(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reg_train, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=(RecyclerView)view.findViewById(R.id.reg_train);
        context=getActivity().getApplicationContext();
        training_ids=new ArrayList<String>();
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
                                JSONArray thread = res.getJSONArray("training_ids");
                                for (int i = 0; i < thread.length(); i++) {
                                    JSONObject obj = thread.getJSONObject(i);
                                    int id = obj.getInt("id");
                                    training_ids.add(String.valueOf(id));
                                }
                                if(training_ids.size()!=0){
                                all_train_SQlite msqld=new all_train_SQlite(getContext());
                                SQLiteDatabase sqlite=msqld.getWritableDatabase();
                                String query="id in ("+makePlaceholders()+")";
                                Cursor c=sqlite.query(msqld.TB_name,null,query,null,null,null,"id");
                                if(c.getCount()!=0) {
                                    c.moveToFirst();
                                    while (!c.isAfterLast()) {
                                        int id = c.getInt(c.getColumnIndex("id"));
                                        int userId = c.getInt(c.getColumnIndex("user_id"));
                                        String title = c.getString(c.getColumnIndex("title"));
                                        String price = String.valueOf(c.getFloat(c.getColumnIndex("price")));
                                        String location = c.getString(c.getColumnIndex("location"));
                                        //  String img_base_64 = c.getString(c.getColumnIndex("img_base_64"));
                                        //byte[] decodedString = Base64.decode(img_base_64, Base64.DEFAULT);
                                        // Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        Bitmap b = BitmapFactory.decodeResource(reg_train.context.getResources(), R.mipmap.ic_launcher);
                                        item object = new item(id, title, location, price, b);

                                        trainings.add(object);
                                        c.moveToNext();
                                    }
                                    msqld.close();

                                }
                                    Myadapter adapter = new Myadapter(getContext(), trainings);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                            }
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
                    params.put("type","trainings_rgst");
                    params.put("user_id",String.valueOf(2));
                    return params;
                }
            };
            queue.add(stringRequest);
        }
    public String makePlaceholders() {
            StringBuilder sb = new StringBuilder(training_ids.size() * 2 - 1);
            sb.append(training_ids.get(0));
            for (int i = 1; i < training_ids.size(); i++) {
                sb.append(","+training_ids.get(i));
            }
            return sb.toString();

    }
    }

