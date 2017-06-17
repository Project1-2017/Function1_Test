package com.project1.softwaresoluitons.xyz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link tra_not.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link tra_not#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tra_not extends Fragment {
    public RequestQueue queue;
    public ProgressDialog dialog;
    public RecyclerView recyclerView;
    public  static ArrayList<notification_item> notifications;
    public static n_adapter adapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public tra_not() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tra_not.
     */
    // TODO: Rename and change types and number of parameters
    public static tra_not newInstance(String param1, String param2) {
        tra_not fragment = new tra_not();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tra_not, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queue = Volley.newRequestQueue(getContext());
        recyclerView=(RecyclerView)view.findViewById(R.id.rview);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        notifications=new ArrayList<>();
        fetch_notifications();
    }

    public void fetch_notifications(){

        dialog = new ProgressDialog(getContext());
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
                                int response_status=obj.getInt("response_status");
                                notifications.add(new notification_item(d_t,name,message,email,contact,title,id,response_status));
                            }
                            adapter=new n_adapter(getContext(),notifications);
                            //recyclerView=(RecyclerView)getView().findViewById(R.id.reg_train);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                SharedPreferences sh=getContext().getSharedPreferences("user",Context.MODE_PRIVATE);
                params.put("type","notifications");
                params.put("training_id",sh.getInt("usr_id",0)+"");
                return params;
            }
        };
        queue.add(stringRequest);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

class notification_item{
    String date_time,name,message,email,contact,tr_title;
    int id,reply_status;
    notification_item(String date_time,String name,String message, String email,String contact,String tr_title,int id,int reply_status){
        this.date_time=date_time;
        this.contact=contact;
        this.email=email;
        this.name=name;
        this.message=message;
        this.tr_title=tr_title;
        this.id=id;
        this.reply_status=reply_status;
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
