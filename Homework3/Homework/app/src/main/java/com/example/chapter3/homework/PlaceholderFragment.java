package com.example.chapter3.homework;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieListener;


public class PlaceholderFragment extends Fragment {
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        context = view.getContext();
        //获取实例
        recyclerView = view.findViewById(R.id.rv);
        //更改数据时不会变更宽高
        recyclerView.setHasFixedSize(true);
        //创建线性布局管理器
        layoutManager = new LinearLayoutManager(context);
        //创建格网布局管理器
        gridLayoutManager = new GridLayoutManager(context, 2);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //创建Adapter
        mAdapter = new MyAdapter(TestDataSet.getData());
        //设置Adapter
        recyclerView.setAdapter(mAdapter);
        //分割线
        LinearItemDecoration itemDecoration = new LinearItemDecoration(Color.BLUE);
        //recyclerView.addItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        //动画
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(3000);
        recyclerView.setItemAnimator(animator);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        animationView = getView().findViewById(R.id.animation_view);
        animationView.playAnimation();

        recyclerView = getView().findViewById(R.id.rv);
        recyclerView.setAlpha(0);

        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 这里会在 5s 后执行
                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
                ObjectAnimator disappear = ObjectAnimator.ofFloat(animationView, "alpha", 1.0f, 0f);
                disappear.setDuration(1000);
                disappear.start();

                ObjectAnimator appear = ObjectAnimator.ofFloat(recyclerView, "alpha", 0f, 1.0f);
                appear.setDuration(1000);
                appear.start();
            }
        },5000);
    }
}
