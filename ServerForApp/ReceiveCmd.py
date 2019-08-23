import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(('',9001))
s.listen(5)
while True:
    connection,addr = s.accept()
    print('Connected')
    buf = connection.recv(1024)
    content = bytearray(buf)
    try:
        print(content[0])
    except:
        continue