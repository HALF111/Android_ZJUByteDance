package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    static int times = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn1 = findViewById(R.id.btn1);
        final TextView tv1 = findViewById(R.id.tv1);

        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(times%5) {
                    case 0: tv1.setText("What a wonderful day!"); break;
                    case 1: tv1.setText("What about listening to the music"); break;
                    case 2: tv1.setText("Today's recommended music is"); break;
                    case 3: tv1.setText("\"打ち上げ花火\""); break;
                    case 4: tv1.setText("Please enjoy it!"); break;
                }
                times++;
                Log.d(TAG, "OnClick_btn1");
            }
        });

        ImageView f1 = findViewById(R.id.figure1);
        ImageView f2 = findViewById(R.id.figure2);

        f1.setImageAlpha(0);

        f2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                f2.setImageAlpha(0);
                f1.setImageAlpha(255);
            }
        });

        Button btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(MainActivity.this, Activity_2.class);
                MainActivity.this.startActivity(it);
            }
        });

    }
}