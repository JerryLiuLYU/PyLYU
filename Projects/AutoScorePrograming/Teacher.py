import socket, threading, time


s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)


s.bind(('127.0.0.1', 9999))
s.listen(5)


def tcplink(sock, addr):
	fp = open('record1.txt','wb')
	filedata = sock.recv(1024)
	fp.write(filedata)
	fp.close()
	sock.close()

while True:
    sock, addr = s.accept()
    t = threading.Thread(target=tcplink, args=(sock, addr))
    t.start()