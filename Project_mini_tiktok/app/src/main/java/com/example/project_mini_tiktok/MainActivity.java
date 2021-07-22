package com.example.project_mini_tiktok;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.project_mini_tiktok.model.VideoMessage;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements FeedAdapter.IOnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // It is very very important to initialize Fresco
        // and it should be initialized before setContentView
        // otherwise it will lead to shantui
        Fresco.initialize(MainActivity.this);

        setContentView(R.layout.activity_main);

        // TODO: ex3-1. 添加 ViewPager 和 Fragment 做可滑动界面
        ViewPager pager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0: return new PlaceholderFragment();
                    case 1: return new UploadFragment();
                    case 2: return new ZhuyeFragment();
                }
                return null;
            }
            @Override
            public int getCount() {
                return 3;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position){
                    case 0: return "视频";
                    case 1: return "发布";
                    case 2: return "个人主页";
                    default: break;
                }
                return null;
            }
        });

        tabLayout.setupWithViewPager(pager);
        // TODO: ex3-2, 添加 TabLayout 支持 Tab

    }

    @Override
    public void onItemCLick(int position, VideoMessage data){
        Toast.makeText(MainActivity.this, "Item Clicked", Toast.LENGTH_SHORT).show();
        Intent it = new Intent();
        it.setClass(MainActivity.this, VideoDetailActivity.class);
        it.putExtra("VideoURL", data.getVideoUrl());
        it.putExtra("position", position);
        it.putExtra("userName", data.getUserName());
        it.putExtra("student_id", data.getStudentId());
        MainActivity.this.startActivity(it);
    }

    @Override
    public void onItemLongCLick(int position, VideoMessage data) {
        // Toast.makeText(MainActivity.this, "长按了第" + position + "条", Toast.LENGTH_SHORT).show();
//        mAdapter.removeData(position);
    }


}