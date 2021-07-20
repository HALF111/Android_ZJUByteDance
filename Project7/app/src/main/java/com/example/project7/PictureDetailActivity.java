package com.example.project7;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class PictureDetailActivity extends AppCompatActivity {

    String mockUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/64/Android_logo_2019_%28stacked%29.svg/400px-Android_logo_2019_%28stacked%29.svg.png";
    String Url1 = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201611%2F01%2F20161101131048_UCE8A.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1629349883&t=737d8f2033baaf8607e1ab22cafdd745";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_detail);

        ImageView imageView = findViewById(R.id.iv_detail);

        RequestOptions cropOptions = new RequestOptions();
        cropOptions.centerCrop().circleCrop();
        Glide.with(this)
                .load(Url1)
                .placeholder(R.mipmap.laba)
                .error(R.drawable.ic_close_black_24dp)
                .apply(cropOptions)
                .transition(withCrossFade())
                .into(imageView);
    }
}
