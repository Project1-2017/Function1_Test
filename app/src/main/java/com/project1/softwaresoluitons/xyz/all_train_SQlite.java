package com.project1.softwaresoluitons.xyz;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class all_train_SQlite extends SQLiteOpenHelper {
    public static String DB_name="trainings";
    public static String TB_name="trainings";
    public static int version=1;
    public static Context context;
    public static String create_query="CREATE TABLE "+TB_name+" (id int primary key,title varchar(50),category varchar(50),location varchar(100),price float,img_base_64 mediumtext,user_id int);" ;
    public static String drop="Drop table if exists "+TB_name;
    public all_train_SQlite(Context c){
        super(c,DB_name,null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(create_query);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(drop);
            onCreate(db);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}


