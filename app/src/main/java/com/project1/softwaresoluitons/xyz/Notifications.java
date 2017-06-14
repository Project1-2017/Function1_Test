package com.project1.softwaresoluitons.xyz;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Notifications extends AppCompatActivity implements tra_not.OnFragmentInteractionListener ,e_not.OnFragmentInteractionListener{

    public SlidingTabLayout mTabs;
    public ViewPager viewPager;
    public ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        toolbar=getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notifications");
        mTabs=(SlidingTabLayout)findViewById(R.id.mtabs);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(new not_Adapter(getSupportFragmentManager(),Notifications.this));
        mTabs.setDistributeEvenly(true);
        mTabs.setViewPager(viewPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

class not_Adapter extends FragmentStatePagerAdapter {

    String[] str=null;
    Context c;
    public not_Adapter(FragmentManager fm, Context c) {
        super(fm);
        this.c=c;
        str=c.getResources().getStringArray(R.array.notifications);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f=null;
        if(position==0){
            f=new tra_not();
        }
        if(position==1){
            f=new e_not();
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return str[position];
    }

    @Override
    public int getCount() {
        return 2;
    }

}
