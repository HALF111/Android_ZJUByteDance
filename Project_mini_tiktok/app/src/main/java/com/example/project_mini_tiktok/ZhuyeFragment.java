package com.example.project_mini_tiktok;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project_mini_tiktok.model.VideoMessage;
import com.example.project_mini_tiktok.model.VideoMessageListResponse;
import com.example.project_mini_tiktok.recycler.LinearItemDecoration;
import com.example.project_mini_tiktok.recycler.MyAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static android.os.Looper.getMainLooper;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.example.project_mini_tiktok.Constants.STUDENT_ID;
import static com.example.project_mini_tiktok.Constants.token;

public class ZhuyeFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private Context context;

    private static final String TAG = "PlaceholderFragement";
    private FeedAdapter adapter = new FeedAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        View view = inflater.inflate(R.layout.fragment_zhuye, container, false);
        context = view.getContext();
        //获取实例
        recyclerView = view.findViewById(R.id.video_geren_recycler);
        //更改数据时不会变更宽高
        recyclerView.setHasFixedSize(true);
        //创建线性布局管理器
        layoutManager = new LinearLayoutManager(context);
        //创建格网布局管理器
        gridLayoutManager = new GridLayoutManager(context, 2);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //创建Adapter
//        mAdapter = new MyAdapter(TestDataSet.getData());
        adapter = new FeedAdapter();
        adapter.setOnItemClickListener((FeedAdapter.IOnItemClickListener)getActivity());
        //设置Adapter
//        recyclerView.setAdapter(mAdapter);
        recyclerView.setAdapter(adapter);
        //分割线
        LinearItemDecoration itemDecoration = new LinearItemDecoration(Color.BLUE);
        //recyclerView.addItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        //动画
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(3000);
        recyclerView.setItemAnimator(animator);

        ImageView imageView = view.findViewById(R.id.portrait);
        RequestOptions cropOptions = new RequestOptions();
        cropOptions.centerCrop().circleCrop();
        Glide.with(this)
                .load(R.mipmap.tang)
                .placeholder(R.drawable.loading)
                .apply(cropOptions)
                .transition(withCrossFade())
                .into(imageView);


        getData(STUDENT_ID, null);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        animationView = getView().findViewById(R.id.animation_view);
//        animationView.playAnimation();

        recyclerView = getView().findViewById(R.id.rv);

//        getView().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 这里会在 5s 后执行
//                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
//                ObjectAnimator disappear = ObjectAnimator.ofFloat(animationView, "alpha", 1.0f, 0f);
//                disappear.setDuration(1000);
//                disappear.start();
//
//                ObjectAnimator appear = ObjectAnimator.ofFloat(recyclerView, "alpha", 0f, 1.0f);
//                appear.setDuration(1000);
//                appear.start();
//            }
//        },5000);
    }

    //TODO 2
    // 用HttpUrlConnection实现获取留言列表数据，用Gson解析数据，更新UI（调用adapter.setData()方法）
    // 注意网络请求和UI更新分别应该放在哪个线程中
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<VideoMessage> baseDetReposFromRemote(String studentId, String userName){
        String Query = "";
        if(studentId != null && !studentId.isEmpty()){
            Query = String.format("?student_id=%s", studentId);
        }
//        if(userName != null && !userName.isEmpty()){
//            Query = String.format("?user_name=%s", userName);
//        }
        String urlStr = String.format("https://api-android-camp.bytedance.com/zju/invoke/video%s", Query);
        VideoMessageListResponse result = null;
        try{
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(6000);

            conn.setRequestMethod("GET");

            conn.setRequestProperty("token", token);

            if(conn.getResponseCode() == 200){
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                result = new Gson().fromJson(reader, new TypeToken<VideoMessageListResponse>(){}.getType());
                reader.close();
                in.close();
            }else{
                // 错误处理
                Log.d(TAG, "HttpURLConnection Failed with code: " + conn.getResponseCode());
                Toast.makeText(context, "收到回应失败！返回值" + conn.getResponseCode(), Toast.LENGTH_SHORT);
            }
            conn.disconnect(); // very important! Do not forget it!
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(context, "网络异常" + e.toString(), Toast.LENGTH_SHORT).show();
        }

        return result.feeds;
    }

    private void getData(String studentId, String userName){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                List<VideoMessage> MessageList = baseDetReposFromRemote(studentId, userName);
                if(MessageList != null && !MessageList.isEmpty()){
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setData(MessageList);
                        }
                    });
                }
            }
        }).start();
    }

}
