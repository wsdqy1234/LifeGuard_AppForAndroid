package com.example.appforbotunderwater;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    /*网络连接等定义常量*/
    private String mIp = "192.168.137.1";
    private int mPort = 9000;
    private static final int COMPLETED = 0x111;
    private RevImageThread revImageThread;
    public static ImageView image;
    private static Bitmap bitmap;
    private MyHandler handler;




    static class MyHandler extends Handler{ //定义handler类
        @Override
        public void handleMessage(Message msg){
            if(msg.what == COMPLETED){
                bitmap = (Bitmap)msg.obj;
                image.setImageBitmap(bitmap);
                super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new MyHandler();
        /**视频显示**/
        image = (ImageView)findViewById(R.id.Image);

        /**连接网络**/
        revImageThread = new RevImageThread(mIp,mPort,handler); //初始化另一个线程
        new Thread(revImageThread).start();//开启接收线程
    }




}
