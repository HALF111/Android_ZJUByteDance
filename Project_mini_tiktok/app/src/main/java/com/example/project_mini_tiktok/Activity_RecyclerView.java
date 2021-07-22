package com.example.project_mini_tiktok;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mini_tiktok.recycler.LinearItemDecoration;
import com.example.project_mini_tiktok.recycler.MyAdapter;
import com.example.project_mini_tiktok.recycler.TestData;
import com.example.project_mini_tiktok.recycler.TestDataSet;
import com.facebook.drawee.backends.pipeline.Fresco;

public class Activity_RecyclerView extends AppCompatActivity implements MyAdapter.IOnItemClickListener {
    private static final String TAG = "TAG";
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // It is very very important to initialize Fresco
        // and it should be initialized before setContentView
        // otherwise it will lead to shantui
        Fresco.initialize(Activity_RecyclerView.this);

        setContentView(R.layout.activity_recyclerview);
        Log.i(TAG, "RecyclerViewActivity onCreate");
        initView();
    }

    private void initView(){
        //获取实例
        recyclerView = findViewById(R.id.rv);
        //更改数据时不会变更宽高
        recyclerView.setHasFixedSize(true);
        //创建线性布局管理器
        layoutManager = new LinearLayoutManager(this);
        //创建格网布局管理器
        gridLayoutManager = new GridLayoutManager(this, 2);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //创建Adapter
        mAdapter = new MyAdapter(TestDataSet.getData());
        //设置Adapter每个item的点击事件
        // mAdapter.setOnItemClickListener(this);
        //设置Adapter
        recyclerView.setAdapter(mAdapter);
        //分割线
        LinearItemDecoration itemDecoration = new LinearItemDecoration(Color.BLUE);
        //recyclerView.addItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //动画
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(3000);
        recyclerView.setItemAnimator(animator);
    }

    @Override
    public void onItemCLick(int position, TestData data){
        Toast.makeText(Activity_RecyclerView.this, "Item Clicked", Toast.LENGTH_SHORT).show();
        Intent it = new Intent();
        it.setClass(Activity_RecyclerView.this, VideoDetailActivity.class);
        Activity_RecyclerView.this.startActivity(it);
    }


    @Override
    public void onItemLongCLick(int position, TestData data) {
        Toast.makeText(Activity_RecyclerView.this, "长按了第" + position + "条", Toast.LENGTH_SHORT).show();
//        mAdapter.removeData(position);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "RecyclerViewActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "RecyclerViewActivity onResume");
    }

}

