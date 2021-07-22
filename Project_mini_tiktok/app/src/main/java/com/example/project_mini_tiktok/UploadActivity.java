package com.example.project_mini_tiktok;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project_mini_tiktok.model.UploadResponse;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.project_mini_tiktok.Constants.STUDENT_ID;
import static com.example.project_mini_tiktok.Constants.USER_NAME;
import static com.example.project_mini_tiktok.Constants.token;

public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "UploadActivity";
    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final int REQUEST_CODE_VIDEO = 102;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private static final String VIDEO_TYPE = "video/*";
    private IApi api;
    private Uri coverImageUri;
    private Uri videoUri;

    private SimpleDraweeView coverSD;
    private VideoView videoSD;
    private Button btn_submit;
    private Button btn_submit_picture1;
    private Button btn_submit_picture2;
    private Button btn_submit_video1;
    private Button btn_submit_video2;
    private EditText contentEditText;

    private String takeImagePath;
    private String mp4Path = "";

    private int REQUEST_CODE_TAKE_PHOTO = 1001;
    private int REQUEST_CODE_TAKE_PHOTO_PATH = 1002;
    private int PERMISSION_REQUEST_CAMERA_CODE = 1003;
    private int PERMISSION_REQUEST_CAMERA_PATH_CODE = 1004;

    private final static int PERMISSION_REQUEST_CODE = 1005;
    private final static int REQUEST_CODE_RECORD = 1006;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        initNetwork();
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        videoSD = findViewById(R.id.sd_video);
        contentEditText = findViewById(R.id.et_content);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit_picture1 = findViewById(R.id.btn_submit_picture1);
        btn_submit_picture2 = findViewById(R.id.btn_submit_picture2);
        btn_submit_video1 = findViewById(R.id.btn_submit_video1);
        btn_submit_video2 = findViewById(R.id.btn_submit_video2);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        btn_submit_picture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });

        btn_submit_video2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_VIDEO, VIDEO_TYPE, "选择视频");
            }
        });

//        btn_submit_picture1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                takePhoto(v);
//            }
//        });
//
//        btn_submit_video1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                record(v);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) { // 取自相册已有的图片
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);
                if(videoSD != null) videoSD.start();

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }else if(REQUEST_CODE_VIDEO == requestCode) { // 取自相册已有的视频
            if (resultCode == Activity.RESULT_OK) {
                videoUri = data.getData();
                videoSD.setVideoURI(videoUri);
                videoSD.start();

                if (videoUri != null) {
                    Log.d(TAG, "pick video " + videoUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }
            } else {
                Log.d(TAG, "file pick fail");
            }
        }else if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) { // 取自相机拍摄的照片
            // todo 1.2 在 data 中直接获取 bitmap
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap)extras.get("data");
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
            coverImageUri = uri;
            coverSD.setImageURI(uri);

            if(videoSD != null) videoSD.start();

        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO_PATH && resultCode == RESULT_OK) { // 取自相机拍摄的照片（指定路径）
            // todo 1.4 通过图片地址构造 bitmap
            // 获取ImageView控件的宽高
            int targetWidth = coverSD.getWidth();
            int targetHeight = coverSD.getHeight();
            // 创建Options，设置InJustDecodeBounds为true，只解码图片宽高信息
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(takeImagePath, options);
            int photoWidth = options.outWidth;
            int photoHeight = options.outHeight;

            // todo 1.5 注意图片大小
            // 计算图片和控件的缩放比例，并设置给Options，然后inJustDecodeBounds置为false，解码真正的图片信息
            int scaleFactor = Math.min(photoWidth / targetWidth, photoHeight / targetHeight);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(takeImagePath, options);
            Bitmap rotateBitmap = PathUtils.rotateImage(bitmap, takeImagePath);
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), rotateBitmap, null,null));
            coverImageUri = uri;
            coverSD.setImageBitmap(rotateBitmap);

            if(videoSD != null) videoSD.start();

        }else if(requestCode == REQUEST_CODE_RECORD && resultCode == RESULT_OK){ // 取自相机拍摄的视频
            videoUri = data.getData();
            play();
        }
//      else if(requestCode == 1){
//            //获取图片的uri，由上一个Activity传回来
//            String url = data.getStringExtra("PictureUrl");
//            Uri uri = Uri.parse(url);
//            coverImageUri = uri;
//            ContentResolver cr = this.getContentResolver();
//            try {
//                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
//                /* 将Bitmap设定到ImageView */
//                coverSD.setImageBitmap(bitmap);
//            } catch (FileNotFoundException e) {
//                Log.e("Exception", e.getMessage(),e);
//            }
//        }
    }

    private void play(){
        videoSD.setVideoPath(mp4Path);
        videoSD.start();
    }

//    public void systemTakePicture(View view) {
//        Intent intent = new Intent(UploadActivity.this, SystemCameraActivity.class);
//        startActivityForResult(intent, 1);
//    }
//
//    public void systemRecord(View view) {
//        Intent intent = new Intent(UploadActivity.this, SystemRecordActivity.class);
//        startActivityForResult(intent, 2);
//    }

    public void takePhoto(View view) {
        requestCameraPermission();
    }

    public void takePhotoUsePath(View view) {
        requestCameraAndSDCardPermission();
    }

    private void requestCameraAndSDCardPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission) {
            takePhotoUsePathHasPermission();
        } else {
            // 申请权限
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CAMERA_PATH_CODE);
        }
    }

    private void takePhotoUsePathHasPermission() {
        // todo 1.3 唤起拍照 intent 并设置图片文件地址
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takeImagePath = getOutputMediaPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, PathUtils.getUriForFile(this, takeImagePath));
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO_PATH);
        }
    }

    private String getOutputMediaPath() {
        // 创建需要存储图片的文件/路径
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".jpg");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CAMERA_CODE);
        } else {
            takePhotoHasPermission();
        }
    }

    private void takePhotoHasPermission() {
        // todo 1.1 直接唤起拍照 intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }


    public void record(View view) {
        requestPermission();
    }

    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {
            recordVideo();
        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    private void recordVideo() {
        // todo 2.1 唤起视频录制 intent 并设置视频地址
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mp4Path = getOutputMediaPath_video();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, PathUtils.getUriForFile(this, mp4Path));
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_RECORD);
        }
    }

    private String getOutputMediaPath_video() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".mp4");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    // 调用起相机进行拍摄
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoHasPermission();
            } else {
                Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_REQUEST_CAMERA_PATH_CODE) {
            boolean hasPermission = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false;
                    break;
                }
            }
            if (hasPermission) {
                takePhotoUsePathHasPermission();
            } else {
                Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == PERMISSION_REQUEST_CODE){
            boolean hasPermission = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false;
                    break;
                }
            }
            if (hasPermission) {
                recordVideo();
            } else {
                Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
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
        byte[] videoData = readDataFromUri(videoUri);
        if(videoData == null || videoData.length == 0){
            Toast.makeText(this, "视频不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
//        String to = toEditText.getText().toString();
//        if (TextUtils.isEmpty(to)) {
//            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
//            return;
//        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入视频标题", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( (videoData.length + coverImageData.length) >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 5
        // 使用api.submitMessage()方法提交留言
        // 如果提交成功则关闭activity，否则弹出toast
        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video", "video.mp4",
                        RequestBody.create(MediaType.parse("multipart/form-data"), videoData));
                MultipartBody.Part coverPart = MultipartBody.Part.createFormData("cover_image", "cover.png",
                        RequestBody.create(MediaType.parse("multipart/form-data"), coverImageData));

                Call<UploadResponse> upResponse = api.submitMessage(STUDENT_ID, USER_NAME,
                        "", coverPart, videoPart, token);
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
