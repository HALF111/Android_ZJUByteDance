package com.bytedance.practice5.socket;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSocketThread extends Thread {
    public ClientSocketThread(SocketActivity.SocketCallback callback) {
        this.callback = callback;
    }

    private SocketActivity.SocketCallback callback;
    private boolean stopFlag = false;
    //head请求内容
    private static String content = "HEAD / HTTP/1.1\r\nHost:www.zju.edu.cn\r\n\r\n";

    @Override
    public void run() {
        // TODO 6 用socket实现简单的HEAD请求（发送content）
        //  将返回结果用callback.onresponse(result)进行展示
        Log.d("socket", "客户端线程start");
        try{
            Socket socket = new Socket("www.zju.edu.cn", 80);
            BufferedInputStream is = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream os = new BufferedOutputStream(socket.getOutputStream());
            // 读文件
            double n = 1;
            byte[] data = new byte[1024*5];
            int len = -1;
            while(!stopFlag && socket.isConnected()){
                if(!content.isEmpty()){
                    Log.d("socket", "客户端发送" + content);
                    os.write(content.getBytes());
                    os.flush();
                    int receiveLen = is.read(data);
                    if(receiveLen != -1){
                        String result = new String(data, 0, receiveLen);
                        Log.d("socket", "客户端收到" + result);
                        callback.onResponse(result);
                        stopFlag = true;
                    }else{
                        Log.d("socket", "客户端收到-1");
                    }
                }
                sleep(300);
            }
            Log.d("socket", "客户端断开");
            os.flush();
            os.close();
            socket.close();
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}