package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_2 extends AppCompatActivity {
    String TAG = "Activity_2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        EditText edtxt2_1 = findViewById(R.id.edtxt2_1);
        final TextView tv2_1 = findViewById(R.id.tv2_1);

        // btn2_1 is for 回显文字
        Button btn2_1 = findViewById(R.id.btn2_1);
        btn2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = edtxt2_1.getText().toString();
                tv2_1.setText(inputText);
                Toast.makeText(Activity_2.this, "button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // btn2_2 is for returning
        Button btn2_2 = findViewById(R.id.btn2_2);
        btn2_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(Activity_2.this, MainActivity.class);
                Activity_2.this.startActivity(it);
            }
        });

        // btn2_3 is to jump to https://www.baidu.com
        Button btn2_3 = findViewById(R.id.btn2_3);
        btn2_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setData(Uri.parse("https://www.baidu.com"));
                Activity_2.this.startActivity(it);
            }
        });

        // btn2_4 is to jump to the call
        Button btn2_4 = findViewById(R.id.btn2_4);
        btn2_4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent dialIntent = new Intent(Intent.ACTION_CALL_BUTTON);
                Activity_2.this.startActivity(dialIntent);
            }
        });

    }
}
