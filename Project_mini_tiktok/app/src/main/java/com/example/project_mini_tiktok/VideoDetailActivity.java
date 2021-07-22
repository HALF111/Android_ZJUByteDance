package com.example.project_mini_tiktok;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class VideoDetailActivity extends AppCompatActivity {
    private LottieAnimationView animationView;
    private VideoView videoView;
    private ImageView video_detail_like;
    private ImageView video_detail_portrait;
    private ImageView video_detail_comment;
    private ImageView video_detail_share;
    private TextView video_detail_username;
    private TextView video_detail_intro;

    static int times = 0;

    //String mockUrl = "https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        final Intent it = getIntent();
        String VideoURL = it.getStringExtra("VideoURL");
        int position = it.getIntExtra("position", 0);
        String userName = it.getStringExtra("userName");
        String student_id = it.getStringExtra("student_id");

        animationView = findViewById(R.id.animation_view);
        animationView.playAnimation();

        video_detail_like = findViewById(R.id.video_detail_like);
        video_detail_portrait = findViewById(R.id.video_detail_portrait);
        video_detail_comment = findViewById(R.id.video_detail_comment);
        video_detail_share = findViewById(R.id.video_detail_share);
        video_detail_username = findViewById(R.id.video_detail_username);
        video_detail_intro = findViewById(R.id.video_detail_intro);

        video_detail_username.setText("@" + userName);
        video_detail_intro.setText(String.format("vlog：%d", position));

        video_detail_like.setAlpha(0f);
        video_detail_portrait.setAlpha(0f);
        video_detail_comment.setAlpha(0f);
        video_detail_share.setAlpha(0f);
        video_detail_username.setAlpha(0f);
        video_detail_intro.setAlpha(0f);

        RequestOptions cropOptions = new RequestOptions();
        cropOptions.centerCrop().circleCrop();
        Glide.with(VideoDetailActivity.this)
                .load(R.mipmap.tang)
                .placeholder(R.drawable.loading)
                .apply(cropOptions)
                .transition(withCrossFade())
                .into(video_detail_portrait);

        videoView = findViewById(R.id.vv_detail);
        videoView.setVideoURI(Uri.parse(VideoURL));
        videoView.setMediaController(new MediaController(this));
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ObjectAnimator disappear = ObjectAnimator.ofFloat(animationView, "alpha", 1.0f, 0f);
                disappear.setDuration(1000);
                disappear.start();

                video_detail_like.setAlpha(1f);
                video_detail_portrait.setAlpha(1f);
                video_detail_comment.setAlpha(1f);
                video_detail_share.setAlpha(1f);
                video_detail_username.setAlpha(1f);
                video_detail_intro.setAlpha(1f);
            }
        });

        video_detail_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (times % 2){
                    case 0: video_detail_like.setImageResource(R.drawable.like); break;
                    case 1: video_detail_like.setImageResource(R.drawable.heart); break;
                }
                times++;
            }
        });

        video_detail_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(VideoDetailActivity.this, ZhuyeActivity_Taren.class);
                it.putExtra("userName", userName);
                it.putExtra("student_id", student_id);
                VideoDetailActivity.this.startActivity(it);
            }
        });
    }
}
