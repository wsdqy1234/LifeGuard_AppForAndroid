# Introduction of ServerForApp

### 1. photo.py

```python
import cv2
cap = cv2.VideoCapture(0)
cap.set(cv2.CAP_PROP_FRAME_WIDTH,640)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT,480)

for i in range(100):
    f, frame = cap.read()
    cv2.imwrite(str(i)+'.png',frame)
cap.release
```

​	**OpenCV Lib** is needed to capture photos using camera in your Laptop.

​	This code is used to **capture 100 photos as our dataset for transportation**.



### 2. ServerForApp.py

```python
import socket
import os
import sys
import struct


def sock_client_image():
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.bind(('',9000))
        s.listen(5)

    except socket.error as msg:
        print(msg)
        print(sys.exit(1))

    for i in range(100):
        sock, addr = s.accept()
        print('Connected:')

        fp = open('TestImage/'+str(i)+'.png', 'rb')  # open the photo for transportation
        while True:
            data = fp.read(1024)  # read data from the photo
                break
            sock.send(data)  # sending data through BYTE form
        sock.close()
    s.close()

if __name__ == '__main__':
    sock_client_image()
```

​	This code is used for **the Server which can transport images to Android App**.

​	In this code, 9000 port is used. You can change it whatever you want. But pay attention to that **Matching the port to the Android App**.