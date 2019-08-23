package com.example.appforbotunderwater;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;



public class RevImageThread implements Runnable{ //多线程实现网络连接，创建socket，关闭socket
    final int SleepTime = 5; //线程的睡眠时间，单位ms
    private String ip; //ip地址
    private int port; //端口
    Handler mainHandler;//对应主线程中的videoHandler
    Socket s; //socket
    Bitmap bitmap;
    private static final int COMPLETED = 0X111; //数据传输成功的标志


    /*构造函数，用于初始化ip,端口,handler*/
    public RevImageThread(String ip,int port,Handler mainHandler){
        this.ip = ip;
        this.port = port;
        this.mainHandler = mainHandler;
    }

    /*打开socket*/
    void open(){
        try{
            s = new Socket(ip,port); //s为socket
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /*关闭socket*/
    void close(){
        try{
            if(s != null) s.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        //初始化
        byte [] buffer = new byte[1024];
        int len = 0;

        while(s == null){//直到连接为止
            open();
        }

        while(true){
            try{
                Thread.sleep(SleepTime); //睡眠
                close();
                open();
            }catch (InterruptedException e1){
                e1.printStackTrace();
            }
            if(!s.isClosed() && s.isConnected() && !s.isInputShutdown()){
                try{
                    Log.i("mr","视频thread连接成功");

                    InputStream ins = s.getInputStream(); //输入流,获取socket的输入
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();//输出流
                    while((len = ins.read(buffer)) != -1){ //将缓冲区读完，并写入buffer
                        outStream.write(buffer,0,len);
                    }
                    ins.close(); //关闭
                    byte data[] = outStream.toByteArray();
                    bitmap = BitmapFactory.decodeByteArray(data,0,data.length);

                    //将bitmap传给handle
                    Message msg = mainHandler.obtainMessage();
                    msg.what = COMPLETED;
                    msg.obj = bitmap;
                    mainHandler.sendMessage(msg);

                    //刷新并关闭输出流缓冲区
                    outStream.flush();
                    outStream.close();

                    if(!s.isClosed()) s.close();//关闭socket

                }catch (IOException e){
                    Log.i("mr",e.getMessage());
                    try{
                        s.shutdownInput();
                        s.shutdownOutput();
                        s.close();
                    }catch (IOException e1){
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }
    }
}
