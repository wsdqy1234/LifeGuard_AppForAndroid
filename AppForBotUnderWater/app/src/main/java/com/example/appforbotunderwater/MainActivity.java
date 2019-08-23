package com.example.appforbotunderwater;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.rockerview.JoystickView;

public class MainActivity extends AppCompatActivity {
    /**网络连接等定义常量**/
    private String mIp = "192.168.137.1";
    private int VideoPort = 9000;
    private int CmdPort = 9001;
    private final int SleepTime = 300; //摇杆发消息时间间隔

    /**视频传输常量**/
    private static final int COMPLETED = 0x111;

    /**控制常量**/
    protected static final int MANCONTROL = 0x00;
    protected static final int AUTOCONTROL = 0x01;
    protected static final int STILL = 0x02;
    protected static final int GOHOME = 0x03;

    /**行动常量**/
    protected static final int LittleAHEAD = 0x04;//ahead
    protected static final int MiddleAHEAD = 0x05;
    protected static final int RapidAHEAD = 0x06;

    protected static final int LittleBACK = 0x07;//back
    protected static final int MiddleBACK = 0x08;
    protected static final int RapidBACK = 0x09;

    protected static final int LittleLEFT = 0x10;//left
    protected static final int RapidLEFT = 0x11;

    protected static final int LittleRIGHT = 0x12;//right
    protected static final int RapidRIGHT = 0x13;
    protected static final int RISE = 0x14;
    protected static final int DIVE = 0x15;

    /**设备常量**/
    protected static final int VIDEO = 0x16;
    protected static final int AIRFLOAT = 0x17;
    protected static final int NormalLIGHT = 0x18;
    protected static final int SosLIGHT = 0x19;
    protected static final int PUMP = 0x20;
    protected static final int CLOSEDEVICE = 0x25;

    /**thread与handler常量*/
    private RevImageThread revImageThread;
    private SendCmdThread sendCmdThread;
    private VideoHandler videoHandler = new VideoHandler();
    /**视频传输常量**/
    public static ImageView image;
    private static Bitmap bitmap;



    /**VideoHandler的定义**/
    static class VideoHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            if(msg.what == COMPLETED){
                bitmap = (Bitmap)msg.obj;
                image.setImageBitmap(bitmap);
                super.handleMessage(msg);
            }
        }
    }

    /**IpButton “提交”按钮控件的onclick**/
    public void IpButton_Click(View v){ //commit ip and port
        EditText Ip_Input = (EditText)findViewById(R.id.IpText); //ip
        EditText port_Input = (EditText)findViewById(R.id.PortText); //VideoPort
        EditText port2_Input = (EditText)findViewById(R.id.PortText2);  //CmdPort

        mIp = Ip_Input.getText().toString();
        VideoPort = Integer.parseInt(port_Input.getText().toString());
        CmdPort = Integer.parseInt(port2_Input.getText().toString());

        /**连接网络**/
        revImageThread = new RevImageThread(mIp,VideoPort,videoHandler); //Initialize Video Recv Thread
        sendCmdThread = new SendCmdThread(mIp,CmdPort);
        new Thread(revImageThread).start();
        new Thread(sendCmdThread).start();
    }
    /**StillButton“急停”按钮控件的onclick**/
    public void StillButton_Click(View v){
        Message msg = new Message();
        msg.what = STILL;
        sendCmdThread.CmdHandler.sendMessage(msg);
    }
    /**HomeButton“归位”按钮控件的onclick**/
    public void HomeButton_Click(View v){
        Message msg = new Message();
        msg.what = GOHOME;
        sendCmdThread.CmdHandler.sendMessage(msg);
    }
    /**RiseButton“设备上升”按钮控件的onclick**/
    public void RiseButton_Click(View v){
        Message msg = new Message();
        msg.what = RISE;
        sendCmdThread.CmdHandler.sendMessage(msg);
    }
    /**DiveButton“设备潜入”按钮控件的onclick**/
    public void DiveButton_Click(View v){
        Message msg = new Message();
        msg.what = DIVE;
        sendCmdThread.CmdHandler.sendMessage(msg);
    }
    /**CameraButton“打开摄像头”按钮控件的onclick**/
    public void CameraButton_Click(View v){
        Message msg = new Message();
        msg.what = VIDEO;
        sendCmdThread.CmdHandler.sendMessage(msg);
    }
    /**AirButton“打开气囊”按钮控件的onclick**/
    public void AirButton_Click(View v){
        Message msg = new Message();
        msg.what = AIRFLOAT;
        sendCmdThread.CmdHandler.sendMessage(msg);
    }
    /**CmLightButton“打开正常灯”按钮控件的onclick**/
    public void CmLightButton_Click(View v){
        Message msg = new Message();
        msg.what = NormalLIGHT;
        sendCmdThread.CmdHandler.sendMessage(msg);
    }
    /**SosLightButton“急停”按钮控件的onclick**/
    public void SosLightButton_Click(View v){
        Message msg = new Message();
        msg.what = SosLIGHT;
        sendCmdThread.CmdHandler.sendMessage(msg);
    }
    /**PumpButton“打开水泵”按钮控件的onclick**/
    public void PumpButton_Click(View v){
        Message msg = new Message();
        msg.what = PUMP;
        sendCmdThread.CmdHandler.sendMessage(msg);
    }
    /**CloseButton“急停”按钮控件的onclick**/
    public void CloseButton_Click(View v){
        Message msg = new Message();
        msg.what = CLOSEDEVICE;
        sendCmdThread.CmdHandler.sendMessage(msg);
    }

    /**创建生命周期**/
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**视频显示**/
        image = (ImageView)findViewById(R.id.Image);

        /**控制switch控件**/
        Switch mSwitch = (Switch)findViewById(R.id.AutoSwitch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){//Switch打开，自动控制
                    Message msg = new Message();
                    msg.what = AUTOCONTROL;
                    sendCmdThread.CmdHandler.sendMessage(msg);
                }else{//Switch关闭，手动控制
                    Message msg = new Message();
                    msg.what = MANCONTROL;
                    sendCmdThread.CmdHandler.sendMessage(msg);
                }
            }
        });

        /**控制摇杆**/
        final JoystickView mJoyStick = (JoystickView)findViewById(R.id.joystickView);
        mJoyStick.setAngleUpdateListener(new JoystickView.OnAngleUpdateListener() {
            @Override
            public void onAngleUpdate(double angle, int action) {
                if(action != JoystickView.ACTION_RELEASE){//按下还没松开
                    boolean inEdgeArea = mJoyStick.isPushedToEdgeArea();//判断快慢,true->快,false->慢
                    double absoluteAngle = mJoyStick.getAbsoluteAngle();//判断方向，前后左右
                    if(absoluteAngle>-45 && absoluteAngle<=45){//前
                        if(inEdgeArea == true){//RapidAHEAD
                            Message msg = new Message();
                            msg.what = RapidAHEAD;
                            sendCmdThread.CmdHandler.sendMessage(msg);
                        }else{//LittleAHEAD
                            Message msg = new Message();
                            msg.what = LittleAHEAD;
                            sendCmdThread.CmdHandler.sendMessage(msg);
                        }
                    }else if(absoluteAngle>-135 && absoluteAngle<=-45){//左
                        if(inEdgeArea == true){//RapidLEFT
                            Message msg = new Message();
                            msg.what = RapidLEFT;
                            sendCmdThread.CmdHandler.sendMessage(msg);
                        }else{//LittleLEFT
                            Message msg = new Message();
                            msg.what = LittleLEFT;
                            sendCmdThread.CmdHandler.sendMessage(msg);
                        }
                    }else if( (absoluteAngle>=-180 && absoluteAngle<=-135) || (absoluteAngle>135 && absoluteAngle<180) ){//后
                        if(inEdgeArea == true){//RapidBACK
                            Message msg = new Message();
                            msg.what = RapidBACK;
                            sendCmdThread.CmdHandler.sendMessage(msg);
                        }else{//LittleBACK
                            Message msg = new Message();
                            msg.what = LittleBACK;
                            sendCmdThread.CmdHandler.sendMessage(msg);
                        }
                    }else if(absoluteAngle>45 && absoluteAngle<=135){//右
                        if(inEdgeArea == true){//RapidRIGHT
                            Message msg = new Message();
                            msg.what = RapidRIGHT;
                            sendCmdThread.CmdHandler.sendMessage(msg);
                        }else{//LittleRIGHT
                            Message msg = new Message();
                            msg.what = LittleRIGHT;
                            sendCmdThread.CmdHandler.sendMessage(msg);
                        }
                    }
                }
            }
        });
    }





}
