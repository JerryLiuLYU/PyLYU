import tkinter
import tkinter.messagebox
from website import monitor
import threading
from analyse import crawl
from analyse import Analysis
from cloud import generate 

root = tkinter.Tk()
root.geometry('250x220+500+200')
root.title('监测网站')
root.resizable(0,0)

lableName = tkinter.Label(root, text='监测网址:', justify=tkinter.RIGHT, width=80)
lableName.place(x=10, y=5, width=80, height=20)
varName=tkinter.StringVar(root,value='')
entryName=tkinter.Entry(root,width=120,textvariable=varName)
entryName.place(x=100,y=5,width=120,height=20)


lablePwd=tkinter.Label(root,text='发件人邮箱:',justify=tkinter.RIGHT,width=80)
lablePwd.place(x=10,y=30,width=80,height=20)
varmailbox=tkinter.StringVar(root,value='')
entrymailbox=tkinter.Entry(root,width=120,textvariable=varmailbox)
entrymailbox.place(x=100,y=30,width=120,height=20)



lablePwd=tkinter.Label(root,text='邮箱密码:',justify=tkinter.RIGHT,width=80)
lablePwd.place(x=10,y=60,width=80,height=20)
varPwd=tkinter.StringVar(root,value='')
entryPwd=tkinter.Entry(root,show='*',width=120,textvariable=varPwd)
entryPwd.place(x=100,y=60,width=120,height=20)


lablePwd=tkinter.Label(root,text='收件人邮箱:',justify=tkinter.RIGHT,width=80)
lablePwd.place(x=10,y=90,width=80,height=20)
varmailbox1=tkinter.StringVar(root,value='')
entrymailbox1=tkinter.Entry(root,width=120,textvariable=varmailbox1)
entrymailbox1.place(x=100,y=90,width=120,height=20)


def thread_it(func):
    t = threading.Thread(target=func)
    t.setDaemon(True)
    t.start()

    
    
def login():
    url = entryName.get()
    mailbox = entrymailbox.get()
    pwd = entryPwd.get()
    mailbox1 = entrymailbox1.get()
    List = [url,mailbox,pwd,mailbox1]
    tkinter.messagebox.showinfo(title='Monitoring website',message='网站持续监测中...')
    monitor(List)

def cancel():
    varmailbox.set('')
    varmailbox1.set('')
    varName.set('')
    varPwd.set('')
    
def cloud1():
    url = entryName.get()
    mailbox = entrymailbox.get()
    pwd = entryPwd.get()
    mailbox1 = entrymailbox1.get()
    List = [url,mailbox,pwd,mailbox1]
    crawl(url)
    Analysis(List)

def cloud():
    generate()

buttonOk=tkinter.Button(root,text='确认',command=lambda:thread_it(login))
buttonOk.place(x=30,y=120,width=70,height=20)



buttonCancel = tkinter.Button(root, text='重置', command=cancel)
buttonCancel.place(x=120, y=120, width=80, height=20)

    
buttonCloud = tkinter.Button(root, text='生成数据分析', command=lambda:thread_it(cloud1))
buttonCloud.place(x=50, y=150, width=100, height=20)

buttonCloud = tkinter.Button(root, text='生成词云', command=lambda:thread_it(cloud))
buttonCloud.place(x=50, y=180, width=100, height=20)

root.mainloop()
