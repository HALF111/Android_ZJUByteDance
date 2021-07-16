package com.bytedance.network.socket;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.network.R;
import com.bytedance.network.socket.tcp.ClientSocketThread;
import com.bytedance.network.socket.tcp.ServerListener;
import com.bytedance.network.socket.udp.UDPSendThread;
import com.bytedance.network.socket.udp.UDPReceiveThread;

public class SocketTestActivity extends AppCompatActivity {
    private TextView text;
    public static Toast toast = null;
    private EditText edit;
    private Button btn;
    private Button udpBtn;
    private ClientSocketThread sendThread;
    private ServerListener serverListener;
    private UDPSendThread udpSendThread;
    private UDPReceiveThread udpReceiveThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        edit =  findViewById(R.id.edit);
        btn = findViewById(R.id.btn_send);
        udpBtn = findViewById(R.id.btn_send_udp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendThread==null || !sendThread.isAlive()){
                    sendThread = new ClientSocketThread(SocketTestActivity.this);
                    sendThread.start();
                }
                sendThread.sendMsg(edit.getText().toString());
            }
        });
        udpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (udpSendThread ==null || !udpSendThread.isAlive()){
                    udpSendThread = new UDPSendThread(SocketTestActivity.this);
                    udpSendThread.start();
                }
                udpSendThread.sendMsg(edit.getText().toString());
            }
        });
        //启动服务器监听线程
        udpReceiveThread = new UDPReceiveThread(this);
        serverListener = new ServerListener(this);
        serverListener.start();
        udpReceiveThread.start();
    }

    @Override
    protected void onDestroy() {
        serverListener.stopServer();
        if (sendThread!=null){
            sendThread.disconnect();
        }
        super.onDestroy();
    }

}
