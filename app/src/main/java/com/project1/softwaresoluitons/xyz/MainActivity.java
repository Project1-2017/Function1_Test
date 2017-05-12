package com.project1.softwaresoluitons.xyz;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements reg_train1.OnFragmentInteractionListener,reg_train.OnFragmentInteractionListener,profile_frag.OnFragmentInteractionListener
{

    public SlidingTabLayout mTabs;
    public ViewPager viewPager;
    public ActionBar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");
        mTabs=(SlidingTabLayout)findViewById(R.id.mtabs);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(new myAdapter(getSupportFragmentManager(),MainActivity.this));
        mTabs.setViewPager(viewPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    public static class MyFragment extends Fragment{
        public MyFragment() {
            super();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View layout=inflater.inflate(R.layout.frg_chk,container,false);
            return layout;
        }
    }
}

class myAdapter extends FragmentStatePagerAdapter {

    String[] str=null;
    Context c;
    public myAdapter(FragmentManager fm, Context c) {
        super(fm);
        this.c=c;
        str=c.getResources().getStringArray(R.array.tabs);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f=null;
        if(position==0){
            f=new profile_frag();
        }
        if(position==1){
            f=new reg_train();
        }
        if(position==2){
            f=new reg_train1();
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return str[position];
    }

    @Override
    public int getCount() {
        return 3;
    }

}

