package com.bytedance.network.socket.udp;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class UDPSendThread extends Thread {
    public UDPSendThread(Activity activity) {
        this.activity = activity;
    }

    private Activity activity;
    private boolean stopFlag = false;
    private volatile String message = "";

    public synchronized void sendMsg(String msg) {
        this.message = msg;
    }

    public void disconnect() {
        stopFlag = true;
    }

    private synchronized void clearMsg() {
        this.message = "";
    }

    @Override
    public void run() {
        Log.d("socket", "UDP客户端线程start ");
        try {
            DatagramSocket datagramSocket = new DatagramSocket(30002); //注意，这本地端口

            // 读文件
            double n = 1;
            byte[] data = new byte[1024 * 5];
            int len = -1;
            while (!stopFlag) {
                if (!message.isEmpty()) {
                    Log.d("socket", "UDP客户端发送 " + message);
                    byte[] sendData = message.getBytes();
                    /**注意用于发送的的Paceket需要制定地址**/
                    datagramSocket.send(new DatagramPacket(sendData,sendData.length,InetAddress.getByName("localhost"),30001));
                    clearMsg();
                    DatagramPacket receivePacket =  new DatagramPacket(data,data.length);
                    datagramSocket.receive(receivePacket);
                    String result = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                    Log.d("socket", "UDP客户端收到 " + result);
                }
                sleep(300);
            }
            Log.d("socket", "客户端断开 ");
            datagramSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
