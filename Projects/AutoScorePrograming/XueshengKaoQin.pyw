import datetime
import os
import re
import socket
import sqlite3
import struct
import sys
import threading
import time
import tkinter
import tkinter.filedialog
import tkinter.messagebox
import tkinter.scrolledtext
import tkinter.simpledialog
import tkinter.ttk
from jieba import load_userdict
import jieba
import openpyxl
import xlrd

root = tkinter.Tk()
#root.config(width=360)
#root.config(height=260)
root.geometry('360x420+400+200')
# 不允许改变窗口大小
root.resizable(False, False)
root.title('课堂教学管理系统')

# 获取本机IP
serverIP = socket.gethostbyname(socket.gethostname())
if serverIP.startswith('127.0.'):
    addrs = socket.getaddrinfo(socket.gethostname(  ),None,0,socket.SOCK_STREAM)
    addrs = [x[4][0] for x in addrs]
    serverIP = [x for x in addrs if ':' not in x][0]

# 广播，发送服务端IP地址
int_sendServerIP = tkinter.IntVar(root, 1)
def sendServerIP():
    # 创建socket对象
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    while int_sendServerIP.get()==1:        
        # 255表示广播地址
        IP = serverIP[:serverIP.rindex('.')]+'.255'
        # 3秒钟发送一次信息
        sock.sendto('ServerIP'.encode(), (IP, 5000))
        time.sleep(3)
thread_sendServerIP = threading.Thread(target=sendServerIP)
thread_sendServerIP.deamon = True
thread_sendServerIP.start()


## =====================通用功能代码开始==============================
class Common:
    # 查询数据库，获取学生专业列表
    def getZhuanye():
        with sqlite3.connect('database.db') as conn:
            cur = conn.cursor()
            cur.execute('select distinct(zhuanye) from students')
            temp = cur.fetchall()
        xueshengZhuanye = []
        for line in temp:
            xueshengZhuanye.append(line[0])
        return xueshengZhuanye

    # 获取指定专业的学生名单
    def getXuehaoXingming(zhuanye):
        with sqlite3.connect('database.db') as conn:
            cur = conn.cursor()
            cur.execute("select xuehao,xingming from students where zhuanye='"+zhuanye+"' order by xuehao")
            temp = cur.fetchall()
        xueshengXinxi = []
        for line in temp:
            xueshengXinxi.append(line[0]+','+line[1])
        return xueshengXinxi
    
    # 获取学生-成绩
    def getXuehaoChengji():
        with sqlite3.connect('database.db') as conn:
            cur = conn.cursor()
            cur.execute("select xuehao,xingming,score from students")
            temp = cur.fetchall()
        xueshengXinxi = []
        for line in temp:
            xueshengXinxi.append(line[0]+','+line[1]+','+line[2])
        return xueshengXinxi

    # 获取指定学号的出勤次数
    def getChuqinCishu(xuehao):
        with sqlite3.connect('database.db') as conn:
            cur = conn.cursor()
            cur.execute("select count(xuehao) from dianming where xuehao='"+xuehao+"'")
            temp = cur.fetchall()
        return temp[0][0]


    # 获取指定SQL语句查询结果
    def getDataBySQL(sql):
        with sqlite3.connect('database.db') as conn:
            cur = conn.cursor()
            cur.execute(sql)
            result = cur.fetchall()
        return result
    
    # 执行SQL语句
    def doSQL(sql):
        with sqlite3.connect('database.db') as conn:
            cur = conn.cursor()
            cur.execute(sql)
            conn.commit()

    # 当前日期时间，格式为“年-月-日 时:分:秒”
    def getCurrentDateTime():
        return str(datetime.datetime.now())[:19]

    # 当前日期时间之前一个半小时前的时间，主要用来避免重复点名
    def getStartDateTime():
        now = datetime.datetime.now()
        now = now + datetime.timedelta(minutes=-90)
        return str(now)[:19]
## =====================通用功能代码结束==============================
# 关闭程序时，取消点名、收作业、接收提问以及接受客户端查询等状态，避免端口一直占用
def closeWindow():
    # 结束点名
    quit = tkinter.messagebox.askokcancel("提示","是否退出？")
    if quit==True:
        if int_canDianming.get() == 1:
            int_canDianming.set(0)       
        # 教师端关闭时，广播消息通知学生端自动关闭
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        IP = serverIP[:serverIP.rindex('.')]+'.255'
        sock.sendto(b'close', (IP, 10500))
    ##    #结束服务状态
    ##    if int_server.get() == 1:
    ##        int_server.set(0)
        int_sendServerIP.set(0)
        root.destroy()
        # sql1='delete from students'
        # sql2='delete from dianming'
        # Common.doSQL(sql1)
        # Common.doSQL(sql2)
        sys.exit()
        exit()
root.protocol('WM_DELETE_WINDOW', closeWindow)
    
# 控制和检测本软件是否已注册，本软件免费试用，不需要注册
int_zhuce = tkinter.IntVar(root, value=0)
def isZhuce():
    int_zhuce.set(1)
    
isZhuce()

## =====================导入学生信息功能代码开始==============================
def buttonImportXueshengXinxiClick():
    # 如果还没有注册，拒绝运行
    sql1='delete from students'
    sql2='delete from dianming'
    Common.doSQL(sql1)
    time.sleep(1)
    Common.doSQL(sql2)
    if int_zhuce.get() == 0:
        tkinter.messagebox.showerror('很抱歉', '请联系作者进行软件注册！')
        return
    filename = tkinter.filedialog.askopenfilename(title='请选择Excel文件',
                                                  filetypes=[('Excel Files','*.xlsx')])
    if filename:
        # 读取数据并导入数据库
        workbook = xlrd.open_workbook(filename=filename)
        sheet1 = workbook.sheet_by_index(0)
        # Excel文件必须包含4列，分别为学号、姓名、专业年级、课程名称
        if sheet1.ncols != 4:
            tkinter.messagebox.showerror(title='很抱歉', message='Excel文件格式不对')
            return

        # 遍历Excel文件每一行
        for rowIndex in range(1, sheet1.nrows):
            row = sheet1.row(rowIndex)
            sql = "insert into students(xuehao,xingming,zhuanye,kecheng,score) values('"\
                  + "','".join(map(lambda item:str(item.value).strip(), row)) + "',0)"
##            sql = "insert into students(xuehao,xingming,zhuanye,kecheng) values('"\
##                  +str(row[0].value).strip()+"','"+str(row[1].value)+"','"\
##                  +str(row[2].value)+"','"+str(row[3].value)+"')"
            Common.doSQL(sql)
        tkinter.messagebox.showinfo(title='恭喜', message='导入成功')
buttonImportXueshengXinxi = tkinter.Button(root, text='导入学生信息', command=buttonImportXueshengXinxiClick)
buttonImportXueshengXinxi.place(x=20, y=20, height=30, width=100)
## =====================导入学生信息功能代码结束==============================




def buttonIPClick():
    #获取并输出本机IP地址
    #如果还没有注册，拒绝运行
    if int_zhuce.get() == 0:
        tkinter.messagebox.showerror('很抱歉', '请联系作者进行软件注册！')
        return
    tkinter.messagebox.showinfo(title='本机IP地址', message=serverIP)
buttonIP = tkinter.Button(root, text='查看本机IP地址', command=buttonIPClick)
buttonIP.place(x=240, y=20, height=30, width=100)

## =====================在线点名功能代码开始==============================
# 控制是否可以点名，1表示可以，0表示不可以
int_canDianming = tkinter.IntVar(root, value=0)
def thread_Dianming():
    # 开始监听
    sqll='delete from dianming'
    Common.doSQL(sqll)
    global sockDianming
    sockDianming = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sockDianming.bind(('', 30303))
    sockDianming.listen(200)
    while int_canDianming.get()==1:
        try:
            #接受一个客户端连接
            conn, addr = sockDianming.accept()
        except:
            continue
        data = conn.recv(1024).decode()
        try:
            #客户端发来的消息格式为：学号,姓名,MAC地址
            xuehao, xingming, mac = data.split(',')
        except:
            conn.sendall('notmatch'.encode())
            conn.close()
            continue
        # 防SQL注入
        xuehao = re.sub(r'[;"\'=]', '', xuehao)
        xingming = re.sub(r'[;"\'=]', '', xingming)
        # 首先检查学号与姓名是否匹配，并且与数据库中的学生信息一致
        sqlIfMatch = "select count(xuehao) from students where xuehao='" + xuehao + "' and xingming='" + xingming + "'"
        if Common.getDataBySQL(sqlIfMatch)[0][0] != 1:
            conn.sendall('notmatch'.encode())
            conn.close()
        else:
            # 记录该学生点名信息：学号，姓名，时间，并反馈给客户端点名成功，然后客户端关闭连接
            currentTime = Common.getCurrentDateTime()
            # 获取一个半小时之前的时间
            startTime = Common.getStartDateTime()
            # 查看是否已经点名过，避免一个半小时内重复点名
            sqlShifouChongfuDianming = "select count(xuehao) from dianming where xuehao='"\
                                       +xuehao+"' and shijian >='"+startTime+"'"
                        
            if Common.getDataBySQL(sqlShifouChongfuDianming)[0][0] != 0:
                conn.sendall('repeat'.encode())
                conn.close()
            else:
                #检查是否代替点名，根据学生端IP地址识别
                sqlShifouDaiDianming = "select count(ip) from dianming where ip='"\
                                       +addr[0]+"' and shijian >='"+startTime+"'"
                sqlMacShifouChongfu = "select count(mac) from dianming where mac='"\
                                      +mac+"' and shijian>='"+startTime+"'"
                
                if Common.getDataBySQL(sqlShifouDaiDianming)[0][0] != 0 \
                   or Common.getDataBySQL(sqlMacShifouChongfu)[0][0] != 0:
                    conn.sendall('daidianming'.encode())
                    conn.close()
                else:
                    #点名
                    sqlDianming = "insert into dianming(xuehao,shijian,ip,mac) values('"\
                                  +xuehao+"','"+currentTime+"','"+addr[0]+"','"+mac+"')"
                    Common.doSQL(sqlDianming)
                    conn.sendall('ok'.encode())
                    conn.close()
    sockDianming.close()
    sockDianming = None

# 开始点名
def buttonStartDianmingClick():
    # 如果还没有注册，拒绝运行
    if int_zhuce.get() == 0:
        tkinter.messagebox.showerror('很抱歉', '请联系作者进行软件注册！')
        return
    if int_canDianming.get() == 1:
        tkinter.messagebox.showerror('很抱歉', '现在正在点名')
        return
    tkinter.messagebox.showinfo('恭喜', '设置成功，现在开始点名')
    #开始点名
    int_canDianming.set(1)
    global tDianming_id
    t = threading.Thread(target=thread_Dianming)
    t.deamon = True
    t.start()
    tDianming_id = t.ident
buttonStartDianming = tkinter.Button(root, text='开始点名', command=buttonStartDianmingClick)
buttonStartDianming.place(x=20, y=60, height=30, width=100)

def buttonStopDianmingClick():
    # 如果还没有注册，拒绝运行
    if int_zhuce.get() == 0:
        tkinter.messagebox.showerror('很抱歉', '请联系作者进行软件注册！')
        return
    if int_canDianming.get() == 0:
        tkinter.messagebox.showerror('很抱歉', '还没开始点名')
        return

    #停止点名
    int_canDianming.set(0)
    sockDianming.close()
    time.sleep(0.1)
    sql = 'select zhuanye from students where xuehao=(select xuehao from dianming where shijian<="'\
          + Common.getCurrentDateTime() + '"  order by shijian desc limit 1)'
    currentZhuanye = Common.getDataBySQL(sql)[0][0]
    sql = 'select count(zhuanye) from students where zhuanye="' + currentZhuanye + '"'
    totalRenshu = Common.getDataBySQL(sql)[0][0]

    sql = 'select count(xuehao) from dianming where shijian<="'+Common.getCurrentDateTime()\
          +'" and shijian>="' + Common.getStartDateTime() + '"'
    totalShidao = Common.getDataBySQL(sql)[0][0]
    
    message = '设置成功，现在停止点名!\n当前点名专业：'+currentZhuanye\
              +'\n应到人数：'+str(totalRenshu) + '\n实到人数：' + str(totalShidao)
    tkinter.messagebox.showinfo('恭喜', message)
buttonStopDianming = tkinter.Button(root, text='结束点名', command=buttonStopDianmingClick)
buttonStopDianming.place(x=130, y=60, height=30, width=100)
## =====================在线点名功能代码结束==============================


## =====================查看学生出勤情况功能代码开始==============================
int_windowChakanKaoqinXinxi = tkinter.IntVar(root, value=0)
class windowChakanKaoqinXinxi:
    def __init__(self, root, myTitle):
        self.top = tkinter.Toplevel(root, width=320, height=400)
        self.top.title(myTitle)
        self.top.attributes('-topmost', 1)
        
        # 组合框选择专业，或文本框查看特定学号学生的出勤情况
        xueshengZhuanye = Common.getZhuanye()
        comboboxZhuanye = tkinter.ttk.Combobox(self.top, values=xueshengZhuanye)
        def comboboxZhuanyeChange(event):
            zhuanye = comboboxZhuanye.get()
            if zhuanye:
                xueshengs = Common.getXuehaoXingming(zhuanye)
                comboboxXuehao['values'] = xueshengs                
        comboboxZhuanye.bind('<<ComboboxSelected>>', comboboxZhuanyeChange)
        comboboxZhuanye.place(x=20, y=20, height=20, width=130)

        # 输出该专业所有学生的出勤情况
        def chakanZhuanye():
            zhuanye = comboboxZhuanye.get()
            if not zhuanye:
                tkinter.messagebox.showerror('很抱歉', '请选择专业！')
                return
            
            sql = "select students.xuehao,students.xingming,dianming.shijian from students,dianming"\
                  +" where students.xuehao=dianming.xuehao and students.zhuanye='"+zhuanye\
                  +"' order by students.xuehao"
            temp = Common.getDataBySQL(sql)
            # 删除原有的所有行
            for row in treeXueshengMingdan.get_children():
                treeXueshengMingdan.delete(row)
            # 插入新数据
            for iii, student in enumerate(temp):
                treeXueshengMingdan.insert('', iii, values=(student[0], student[1], student[2]))
        buttonZhuanye = tkinter.Button(self.top, text='按专业查看', command=chakanZhuanye)
        buttonZhuanye.place(x=160, y=20, height=20, width=80)
        comboboxXuehao = tkinter.ttk.Combobox(self.top,)
        comboboxXuehao.place(x=20, y=60, height=20, width=130)

        # 按学号查看学生出勤情况
        def chakanXuehao():
            # 删除原有的所有行
            for row in treeXueshengMingdan.get_children():
                treeXueshengMingdan.delete(row)
            xueshengXinxi = comboboxXuehao.get()
            if not xueshengXinxi:
                tkinter.messagebox.showerror('很抱歉', '请选择学生!')
                return
            
            xuehaoKaoqin = xueshengXinxi.split(',')[0]
            sql = "select students.xuehao,students.xingming,dianming.shijian from students,dianming"\
                  +" where students.xuehao=dianming.xuehao and dianming.xuehao='"+xuehaoKaoqin+"'"
            temp = Common.getDataBySQL(sql)
            for iii, student in enumerate(temp):
                treeXueshengMingdan.insert('', iii, values=(student[0], student[1], student[2]))
        buttonXuehao = tkinter.Button(self.top, text='按学号查看', command=chakanXuehao)
        buttonXuehao.place(x=160, y=60, height=20, width=80)

        # 创建表格，设置表头,show="headings"用来隐藏树形控件的默认首列
        self.frame = tkinter.Frame(self.top)
        self.frame.place(x=20, y=90, width=290, height=280)
        # 垂直滚动条
        scrollBar = tkinter.Scrollbar(self.frame)
        scrollBar.pack(side=tkinter.RIGHT, fill=tkinter.Y)
        # 使用树形控件实现表格
        treeXueshengMingdan = tkinter.ttk.Treeview(self.frame,
                                                   columns=('col1', 'col2', 'col3'),
                                                   show="headings",
                                                   yscrollcommand = scrollBar.set)
        treeXueshengMingdan.column('col1', width=70, anchor='center')
        treeXueshengMingdan.column('col2', width=60, anchor='center')
        treeXueshengMingdan.column('col3', width=140, anchor='center')
        treeXueshengMingdan.heading('col1', text='学号')
        treeXueshengMingdan.heading('col2', text='姓名')
        treeXueshengMingdan.heading('col3', text='出勤时间')
        treeXueshengMingdan.pack(side=tkinter.LEFT, fill=tkinter.Y)
        # 树形控件与垂直滚动条结合
        scrollBar.config(command=treeXueshengMingdan.yview)

        def treeviewClick(event):
            selectedItem=treeXueshengMingdan.selection()[0]
            xuehao=treeXueshengMingdan.item(selectedItem,'values')[0]
            y = tkinter.messagebox.askokcancel("提示","是否删除此条信息？")
            if y == True:
                sqll='delete from dianming where xuehao = '+xuehao
                Common.doSQL(sqll)
                treeXueshengMingdan.delete(selectedItem)
        treeXueshengMingdan.bind('<Double-1>',treeviewClick)
def buttonChakanKaoqinXinxiClick():
    # 如果还没有注册，拒绝运行
    if int_zhuce.get() == 0:
        tkinter.messagebox.showerror('很抱歉', '请联系作者进行软件注册！')
        return
    if int_windowChakanKaoqinXinxi.get()==0:
        int_windowChakanKaoqinXinxi.set(1)
        w1 = windowChakanKaoqinXinxi(root, '查看出勤情况')
        buttonChakanKaoqinXinxi.wait_window(w1.top)
        int_windowChakanKaoqinXinxi.set(0)
buttonChakanKaoqinXinxi = tkinter.Button(root, text='查看出勤情况', command=buttonChakanKaoqinXinxiClick)
buttonChakanKaoqinXinxi.place(x=240, y=60, height=30, width=100)
## =====================查看学生出勤情况功能代码结束==============================


## =====================查看成绩功能代码开始==============================
int_windowChakanTongjiQingkuang = tkinter.IntVar(root, value=0)
class windowChakanTongjiQingkuang:
    def __init__(self, root, myTitle):
        self.top = tkinter.Toplevel(root, width=340, height=380)
        self.top.title(myTitle)
        self.top.attributes('-topmost', 1)
        
     
        #查看指定专业所有同学的提问情况
        def chakanZhuanye():           
            xuehaoChengjis = Common.getXuehaoChengji()
            xuehaos = [xingming.split(',')[0] for xingming in xuehaoChengjis]
            xingmings = [xingming.split(',')[1] for xingming in xuehaoChengjis]
            kaoshidefen = [xingming.split(',')[2] for xingming in xuehaoChengjis]

                #删除原有的所有行
            for row in treeXueshengMingdan.get_children():
                treeXueshengMingdan.delete(row)

            for iii, item in enumerate(zip(xuehaos,xingmings, kaoshidefen)):
                treeXueshengMingdan.insert('', iii, values=tuple(item))
        buttonXuehao = tkinter.Button(self.top, text='查看/刷新', command=chakanZhuanye)
        buttonXuehao.place(x=10, y=20, height=20, width=80)
        
        # 创建表格，设置表头,show="headings"用来隐藏树形控件的默认首列
        self.frame = tkinter.Frame(self.top)
        self.frame.place(x=20, y=50, width=560, height=320)
        # 垂直滚动条
        scrollBar = tkinter.Scrollbar(self.frame)
        scrollBar.pack(side=tkinter.RIGHT, fill=tkinter.Y)
        # 使用树形控件实现表格
        treeXueshengMingdan = tkinter.ttk.Treeview(self.frame,
                                                   columns=('col1', 'col2', 'col3'),
                                                   show="headings",
                                                   yscrollcommand = scrollBar.set)
        treeXueshengMingdan.column('col1', width=100, anchor='center')
        treeXueshengMingdan.column('col2', width=100, anchor='center')
        treeXueshengMingdan.column('col3', width=100, anchor='center')
        treeXueshengMingdan.heading('col1', text='学号')
        treeXueshengMingdan.heading('col2', text='姓名')
        treeXueshengMingdan.heading('col3', text='成绩')
        treeXueshengMingdan.pack(side=tkinter.LEFT, fill=tkinter.Y)
        # 树形控件与垂直滚动条结合
        scrollBar.config(command=treeXueshengMingdan.yview)
        
def buttonChakanTongjiQingkuangClick():
    # 如果还没有注册，拒绝运行
    if int_zhuce.get() == 0:
        tkinter.messagebox.showerror('很抱歉', '请联系作者进行软件注册！')
        return
    if int_windowChakanTongjiQingkuang.get()==0:
        int_windowChakanTongjiQingkuang.set(1)
        w1 = windowChakanTongjiQingkuang(root, '查看成绩')
        buttonChakanTongjiQingkuang.wait_window(w1.top)
        int_windowChakanTongjiQingkuang.set(0)
buttonChakanTongjiQingkuang = tkinter.Button(root, text='查看成绩', command=buttonChakanTongjiQingkuangClick)
buttonChakanTongjiQingkuang.place(x=20, y=100, height=30, width=100)
## =====================查看成绩功能代码结束==============================

## =====================数据导出功能代码开始==============================
# 数据导出到xlsx文件
def buttonDaochuClick():
    from openpyxl import Workbook         
    wb = Workbook()
    # 删除默认的worksheet   
    wb.remove(wb.worksheets[0])
    # 点名记录
    ws = wb.create_sheet(title='学生成绩情况')
    ws.append(['学号', '姓名', '成绩'])
    xuehaoChengjis = Common.getXuehaoChengji()
    for i in xuehaoChengjis:
        ws.append(i.split(','))
    wb.save('成绩数据.xlsx')
    tkinter.messagebox.showinfo('恭喜', '数据导出成功，请查看系统文件夹中的"数据导出.xlsx"文件')
buttonDaochu = tkinter.Button(root, text='数据导出', command=buttonDaochuClick)
buttonDaochu.place(x=130, y=100, height=30, width=100)
## =====================数据导出功能代码结束==============================




## ==========远程关闭所有学生机器代码开始=======
def onbuttonShutdownClick():
    result = tkinter.messagebox.askyesno('远程关机', '确定要关闭所有学生机器吗？')
    if result == tkinter.YES:
        # 通知客户端开始接收广播
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        IP = serverIP[:serverIP.rindex('.')]+'.255'
        sock.sendto(b'shutdown', (IP, 10500))
buttonShutdown = tkinter.Button(root, text='关闭所有学生机器', command=onbuttonShutdownClick)
buttonShutdown.place(x=240, y=100, width=100, height=30)
## ==========远程关闭所有学生机器代码结束=======




studentkeys = {}
def everyStudent(conn):
    xuehao = conn.recv(30).decode()
    xuehao = str(xuehao).replace(' ','')
           
 # 接收键盘数据    
    temp = conn.recv(40096)  
    print("-------temp-----")
    print(temp.decode().lower())
    print("-------reslult-----")
    result = jieba.lcut(temp.decode().lower())
    print(result)
    print(keyword)
    count = 0
    for i in result:
        if i in keyword:
            count = count+1  
    studentkeys[xuehao] = studentkeys.get(xuehao,0)+count
    sqlkey = "update students set score= "+str(studentkeys[xuehao])+" where xuehao = "+xuehao
    Common.doSQL(sqlkey)
    conn.close() 
    
    

def receiveScreenMain():
    '''定期接收学生端键盘记录的主线程函数'''
    global sockReceiveScreen
    sockReceiveScreen = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sockReceiveScreen.bind(('', 10008))
    sockReceiveScreen.listen(150)
    while receivingScreen:
        try:
            conn, addr = sockReceiveScreen.accept()
            t = threading.Thread(target=everyStudent, args=(conn,))
            t.deamon = True
            t.start()
        except:
            sockReceiveScreen.close()
            return        
    else:
        sockReceiveScreen.close()        
receivingScreen = True
t = threading.Thread(target=receiveScreenMain)
t.deamon = True
t.start()

def getkeywords():
    '''函数注释'''
    filename1 = tkinter.filedialog.askopenfilename(title='请选择txt文件',
                                                  filetypes=[('TXT Files','*.txt')])
    if filename1:
        jieba.set_dictionary("dict.txt")
        jieba.initialize()
        jieba.load_userdict(filename1)
        global keyword
        with open(filename1,'r') as f:
            keyword = f.readlines()
        for i in range(0,len(keyword)-1):
            keyword[i] = keyword[i].strip('\n')
        tkinter.messagebox.showinfo(title='恭喜', message='导入成功')
getkeywords = tkinter.Button(root, text='导入关键字', command=getkeywords)
getkeywords.place(x=130, y=20, height=30, width=100)
root.mainloop()
