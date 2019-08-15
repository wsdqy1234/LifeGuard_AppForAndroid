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
