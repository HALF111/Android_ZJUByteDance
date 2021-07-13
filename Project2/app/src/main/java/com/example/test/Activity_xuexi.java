package com.example.test;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_xuexi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xuexi);

        Button btn3_1 = findViewById(R.id.btn3_1);
        btn3_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog textTips = new AlertDialog.Builder(Activity_xuexi.this)
                        .setTitle("Tips:")
                        .setMessage("恭喜您满绩了！")
                        .create();
                textTips.show();
            }
        });

        Button btn3_2 = findViewById(R.id.btn3_2);
        btn3_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(Activity_xuexi.this, MainActivity.class);
                Activity_xuexi.this.startActivity(it);
            }
        });

    }
}