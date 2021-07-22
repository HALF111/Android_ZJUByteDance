package com.example.project_mini_tiktok;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemCameraActivity extends AppCompatActivity {
    private int REQUEST_CODE_TAKE_PHOTO = 1001;
    private int REQUEST_CODE_TAKE_PHOTO_PATH = 1002;
    private int PERMISSION_REQUEST_CAMERA_CODE = 1003;
    private int PERMISSION_REQUEST_CAMERA_PATH_CODE = 1004;

    private ImageView imageView;
    private String takeImagePath;
    private Button btn_return_picture;

//    public static void startUI(Context context) {
//        Intent intent = new Intent(context, SystemCameraActivity.class);
//        context.startActivity(intent);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_camera);
        imageView = findViewById(R.id.iv_img);

        btn_return_picture = findViewById(R.id.btn_return_picture);
        btn_return_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(SystemCameraActivity.this, UploadActivity.class);
                Bitmap bitmap =((BitmapDrawable)imageView.getDrawable()).getBitmap();
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                it.putExtra("PictureUrl", uri.toString());
                setResult(01, it);
                finish();
            }
        });

    }

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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            // todo 1.2 在 data 中直接获取 bitmap
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap)extras.get("data");
            imageView.setImageBitmap(bitmap);
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO_PATH && resultCode == RESULT_OK) {
            // todo 1.4 通过图片地址构造 bitmap
            // 获取ImageView控件的宽高
            int targetWidth = imageView.getWidth();
            int targetHeight = imageView.getHeight();
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
            imageView.setImageBitmap(rotateBitmap);
        }
    }
}