package com.example.project_mini_tiktok;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mini_tiktok.model.VideoMessage;
import com.example.project_mini_tiktok.model.VideoMessageListResponse;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.example.project_mini_tiktok.Constants.token;


public class Activity_RecyclerView_Internet extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private FeedAdapter adapter = new FeedAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // very very important to initialize the Fresco
        Fresco.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recylerview_internet);
        RecyclerView recyclerView = findViewById(R.id.rv_internet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        getData(null);

    }


    //TODO 2
    // 用HttpUrlConnection实现获取留言列表数据，用Gson解析数据，更新UI（调用adapter.setData()方法）
    // 注意网络请求和UI更新分别应该放在哪个线程中
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<VideoMessage> baseDetReposFromRemote(String studentId){
        String Query = "";
        if(studentId != null && !studentId.isEmpty()){
            Query = String.format("?student_id=%s", studentId);
        }
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
                Toast.makeText(Activity_RecyclerView_Internet.this, "收到回应失败！返回值" + conn.getResponseCode(), Toast.LENGTH_SHORT);
            }
            conn.disconnect(); // very important! Do not forget it!
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "网络异常" + e.toString(), Toast.LENGTH_SHORT).show();
        }

        return result.feeds;
    }

    private void getData(String studentId){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                List<VideoMessage> MessageList = baseDetReposFromRemote(studentId);
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