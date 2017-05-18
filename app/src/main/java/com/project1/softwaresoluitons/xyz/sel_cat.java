package com.project1.softwaresoluitons.xyz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class sel_cat extends Activity implements View.OnClickListener {
    public Spinner dropdown;
    public Button ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sel_cat);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setTitle("Choose Category");
        Intent i=getIntent();
        Bundle b=this.getIntent().getExtras();
        String[] categories=b.getStringArray("categories");
        dropdown = (Spinner)findViewById(R.id.spinner1);
        ok=(Button)findViewById(R.id.ok);
        ok.setOnClickListener(this);
        Log.i("cat",categories+"");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,categories);
        dropdown.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {
        if(v==ok){
            String c=dropdown.getSelectedItem().toString();
            Intent intent = new Intent();
            intent.putExtra("category", c);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
