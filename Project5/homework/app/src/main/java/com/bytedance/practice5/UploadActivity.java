package com.bytedance.practice5;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.practice5.model.Message;
import com.bytedance.practice5.model.UploadResponse;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bytedance.practice5.Constants.BASE_URL;
import static com.bytedance.practice5.Constants.BOUNDARY;
import static com.bytedance.practice5.Constants.STUDENT_ID;
import static com.bytedance.practice5.Constants.USER_NAME;
import static com.bytedance.practice5.Constants.token;
import static com.bytedance.practice5.Constants.PRE_FIX;
import static com.bytedance.practice5.Constants.END_FIX;


public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private IApi api;
    private Uri coverImageUri;
    private SimpleDraweeView coverSD;
    private EditText toEditText;
    private EditText contentEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        toEditText = findViewById(R.id.et_to);
        contentEditText = findViewById(R.id.et_content);
        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });


        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        findViewById(R.id.btn_submit2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                submitMessageWithURLConnection();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
    }

    private void initNetwork() {
        //TODO 3
        // 创建Retrofit实例
        //
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-android-camp.bytedance.com/zju/invoke/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    // 在UI线程提示
    public void makeUIToast(Context context, String text, int duration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, duration).show();
            }
        });
    }

    private void submit() {
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 5
        // 使用api.submitMessage()方法提交留言
        // 如果提交成功则关闭activity，否则弹出toast
        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Part coverPart = MultipartBody.Part.createFormData("image", "cover.png",
                        RequestBody.create(MediaType.parse("multipart/form-data"), coverImageData));
                MultipartBody.Part fromPart = MultipartBody.Part.createFormData("from", USER_NAME);
                MultipartBody.Part toPart = MultipartBody.Part.createFormData("to", to);
                MultipartBody.Part contentPart = MultipartBody.Part.createFormData("content", content);

                Call<UploadResponse> upResponse = api.submitMessage(STUDENT_ID, "",
                        fromPart, toPart, contentPart, coverPart, token);
                upResponse.enqueue(new Callback<UploadResponse>(){
                    @Override
                    public void onResponse(final Call<UploadResponse> call, final Response<UploadResponse> response){
                        if(!response.isSuccessful()){
                            makeUIToast(UploadActivity.this, "收到回应失败", Toast.LENGTH_SHORT);
                            return;
                        }
                        UploadResponse upResponse = response.body();
                        if(upResponse == null){
                            makeUIToast(UploadActivity.this, "收到回应为空", Toast.LENGTH_SHORT);
                            return;
                        }

                        if(upResponse.success){
                            Log.d("UploadResponse", "Success.");
                            makeUIToast(UploadActivity.this, "发送成功！", Toast.LENGTH_SHORT);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UploadActivity.this.finish();
                                }
                            });
                        }
                        else{
                            Log.d("UploadResponse Error", upResponse.error);
                            makeUIToast(UploadActivity.this, "提交失败: " + upResponse.error, Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onFailure(final Call<UploadResponse> call, final Throwable t) {
                        t.printStackTrace();
                        makeUIToast(UploadActivity.this, "提交失败" + t.toString(), Toast.LENGTH_SHORT);
                    }

                });
            }
        }).start();
    }


    // TODO 7 选做 用URLConnection的方式实现提交
    private void submitMessageWithURLConnection(){
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String Query = String.format("?student_id=%s", STUDENT_ID);
                String urlStr = String.format("https://api-android-camp.bytedance.com/zju/invoke/messages%s", Query);
                InputStream is = null;
                OutputStream os = null;
                UploadResponse ResponseResult = null;
                try{
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("token", token);
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.connect();

                    os = new DataOutputStream(conn.getOutputStream());
                    // 写body
                    // 写入格式参考https://img-ask.csdn.net/upload/201805/09/1525863351_886440.png
                    StringBuilder contentBody = new StringBuilder();

                    contentBody.append(PRE_FIX);
                    contentBody.append("Content-Disposition: form-data; name=\"from\"" + "\r\n");
                    contentBody.append("\r\n");
                    contentBody.append(USER_NAME);

                    contentBody.append(PRE_FIX);
                    contentBody.append("Content-Disposition: form-data; name=\"to\"" + "\r\n");
                    contentBody.append("\r\n");
                    contentBody.append(to);

                    contentBody.append(PRE_FIX);
                    contentBody.append("Content-Disposition: form-data; name=\"content\"" + "\r\n");
                    contentBody.append("\r\n");
                    contentBody.append(content);

                    os.write(contentBody.toString().getBytes());

                    // 写文件
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append(PRE_FIX);
                    strBuf.append("Content-Disposition: form-data; name=\"image\"; filename=\"cover.jpg\"\r\n");
                    strBuf.append("Content-Type: image/jpeg" + "\r\n\r\n");
                    // strBuf.append(coverImageData); // 即coverImageData不加在strBuf中，而是直接写道os之中
                    os.write(strBuf.toString().getBytes());
                    os.write(coverImageData);

                    // body结束
                    os.write(END_FIX.getBytes());
                    os.flush();
                    // os.close();

                    if(conn.getResponseCode() == 200){
                        is = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                        ResponseResult = new Gson().fromJson(reader, new TypeToken<UploadResponse>(){}.getType());
                        //分析返回内容
                        if(ResponseResult.success){
                            Log.d("UploadResponse", "Success.");
                            makeUIToast(UploadActivity.this, "发送成功！", Toast.LENGTH_SHORT);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UploadActivity.this.finish();
                                }
                            });
                        }else{
                            Log.d(TAG, "上传失败" + ResponseResult.error);
                            makeUIToast(UploadActivity.this, "上传失败" + ResponseResult.error, Toast.LENGTH_SHORT);
                        }
                        reader.close();
                        is.close();
                    }else{
                        // 错误处理
                        Log.d(TAG, "HttpURLConnection Failed with code: " + conn.getResponseCode());
                        makeUIToast(UploadActivity.this, "收到回应失败！返回值" + conn.getResponseCode(), Toast.LENGTH_SHORT);
                    }
                    conn.disconnect();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "HttpURLConnection Failed with Network Exception: " + e.toString());
                    Toast.makeText(UploadActivity.this, "连接失败" + e.toString(), Toast.LENGTH_SHORT);
                }
            }
        }).start();
    }


    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
