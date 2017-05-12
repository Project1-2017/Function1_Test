package com.project1.softwaresoluitons.xyz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main3Activity extends AppCompatActivity implements View.OnClickListener{
    public Button profile_button;
    public Button trainings_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        profile_button=(Button)findViewById(R.id.pro_button);
        trainings_button=(Button)findViewById(R.id.train_button);
        trainings_button.setOnClickListener(this);
        profile_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==profile_button){
            startActivity(new Intent(this,MainActivity.class));
        }
        else if(v==trainings_button){
            startActivity(new Intent(this,Main2Activity.class));
        }
    }
}
