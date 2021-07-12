package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
//        edtxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                Toast.makeText(Activity_2.this, String.valueOf(actionId), Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

        final TextView tv2_1 = findViewById(R.id.tv2_1);

        Button btn2_1 = findViewById(R.id.btn2_1);
        btn2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = edtxt2_1.getText().toString();
                tv2_1.setText(inputText);
            }
        });

        Button btn2_2 = findViewById(R.id.btn2_2);
        btn2_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(Activity_2.this, MainActivity.class);
                Activity_2.this.startActivity(it);
            }
        });

    }
}
