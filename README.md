# LifeGuard App for Android

## 1. ServerForApp Folder

​	This folder contains two codes `photo.py` and `ServerForApp.py`. **These codes are using for the Server to capture photos (as our dataset) and sending data of photos to Android App**.



---



## 2. AppForBotUnderWater Folder

​	This folder contains Android project named LifeGuard. It can :

| receive files through WiFi** (using socket)                  |
| ------------------------------------------------------------ |
| **play video in real time** (sourcing from the Server)       |
| **give commands to LifeGuard in order to drive it by yourself or automatically** |

​	Till now, it can complete the first two tasks. The third task is to be finished.

​	Need to sentence that: **You can change the IP and port according to your own situation**. You just need to **find the `MainActivity.java`**, change `mIp` and `mPort` on your own:

```python
public class MainActivity extends AppCompatActivity {

    /*网络连接等定义常量*/
    private String mIp = "192.168.137.1";
    private int mPort = 9000;
    private static final int COMPLETED = 0x111;
    private RevImageThread revImageThread;
    public static ImageView image;
    private static Bitmap bitmap;
    private MyHandler handler;
    ...
}
```



---



### 3. LifeGuard.apk

​	This is the .apk installation program which can deploy our app on your Android Device.