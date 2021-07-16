package com.bytedance.network.socket.udp;

import android.app.Activity;
import android.widget.Toast;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPReceiveThread extends Thread {
    private Activity activity;
    public UDPReceiveThread(Activity activity){
        this.activity = activity;
    }
    public void stopServer(){
    }
    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(30001);
            byte data[] = new byte[1024];
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(data,data.length);
                socket.receive(receivePacket);
                String result = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(activity,result,Toast.LENGTH_SHORT).show();
                    }
                });
                byte[] sendData = ("UDP收到数据: "+result).getBytes();
                socket.send(new DatagramPacket(sendData,sendData.length,receivePacket.getAddress(),receivePacket.getPort()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}