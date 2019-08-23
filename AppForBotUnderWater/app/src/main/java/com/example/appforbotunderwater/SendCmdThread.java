package com.example.appforbotunderwater;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SendCmdThread implements Runnable{
    /**常量定义**/
    final int SleepTime = 5;//ms
    private String ip;
    private int port;
    public Handler CmdHandler;
    Socket s;
    private static final int COMPLETED = 0X111; //成功标志

    /**构造函数初始化**/
    public SendCmdThread(String ip,int port){
        this.ip = ip;
        this.port = port;
    }

    /**打开socket**/
    void open(){
        try{
            s = new Socket(ip,port);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**关闭socket**/
    void close(){
        try{
            if(s != null) s.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void send(int message){ //发送message
        while(s == null) open(); //循环直到连接为止
        close();
        open();//确保连接后重开一个socket
        if(!s.isClosed() && s.isConnected() && !s.isInputShutdown()){
            byte[] buffer = new byte[1024];//初始化
            int len = 0;
            try{//创建writer
                OutputStream outputStream = s.getOutputStream();//发送数据
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                writer.write(message);
                writer.flush();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        close();
    }


    /**线程架构**/
    @Override
    public void run(){
        Looper.prepare();
        CmdHandler = new Handler(){
            public void handleMessage(Message msg){
                //接收主线程发来的消息
                switch(msg.what){
                    /**控制常量**/
                    case MainActivity.MANCONTROL://手控
                        send(0);
                        break;

                    case MainActivity.AUTOCONTROL://自动控制
                        send(1);
                        break;

                    case MainActivity.STILL://静止
                        send(2);
                        break;

                    case MainActivity.GOHOME://归位
                        send(3);
                        break;

                    /**行动常量**/
                    case MainActivity.LittleAHEAD://慢速前进
                        send(4);
                        break;

                    case MainActivity.MiddleAHEAD://中速前进
                        send(5);
                        break;

                    case MainActivity.RapidAHEAD://高速前进
                        send(6);
                        break;

                    case MainActivity.LittleBACK://慢速后退
                        send(7);
                        break;

                    case MainActivity.MiddleBACK://中速后退
                        send(8);
                        break;

                    case MainActivity.RapidBACK://快速后退
                        send(9);
                        break;

                    case MainActivity.LittleLEFT://慢速左转
                        send(10);
                        break;

                    case MainActivity.RapidLEFT://快速左转
                        send(11);
                        break;

                    case MainActivity.LittleRIGHT://慢速右转
                        send(12);
                        break;

                    case MainActivity.RapidRIGHT://快速右转
                        send(13);
                        break;

                    case MainActivity.RISE://上升
                        send(14);
                        break;

                    case MainActivity.DIVE://下潜
                        send(15);
                        break;

                    /**设备常量**/
                    case MainActivity.VIDEO://开启摄像头
                        send(16);
                        break;

                    case MainActivity.AIRFLOAT://打开气囊
                        send(17);
                        break;

                    case MainActivity.NormalLIGHT://正常灯光
                        send(18);
                        break;

                    case MainActivity.SosLIGHT://救援灯光
                        send(19);
                        break;

                    case MainActivity.PUMP://打开水泵
                        send(20);
                        break;

                    case MainActivity.CLOSEDEVICE://关闭设备
                        send(25);
                        break;
                    default://其他消息
                        break;
                }
            }
        };
        Looper.loop();
    }
}
