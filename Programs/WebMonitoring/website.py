import requests
import time
import smtplib
from email.mime.text import MIMEText
from email.utils import formataddr


def get_status(url):
    try:
        r = requests.get(url)
        return r.status_code
    except:
        return '产生异常'


def monitor(List):
    url = List[0]
    my_sender = List[1]
    pwd = List[2]
    my_user = List[3]
    n = get_status(url)
    while(True):
        if n != 200:
            flag = 0
            for i in range(3):
                time.sleep(6)  # 睡眠60秒
                n1 = get_status(url)
                if n1 != 200:
                    flag += 1
            if flag == 3:
                mail(my_sender,my_user,pwd)
        else:
            time.sleep(1200)

def mail(my_sender,my_user,pwd):
    try: 
        msg = MIMEText('监测的网站已出现异常！', 'plain', 'utf-8')
        msg['From'] = formataddr(["Monitoring website", my_sender])  # 发件人邮箱昵称、发件人邮箱账号
        msg['To'] = formataddr(["hello", my_user])  # 收件人邮箱昵称、收件人邮箱账号
        msg['Subject'] = "监测结果"  # 邮件的主题
        server = smtplib.SMTP("smtp.126.com", 25)  # 发件人邮箱中的SMTP服务器，端口是25
        server.login(my_sender, pwd)  # 括号中对应的是发件人邮箱账号、邮箱密码
        server.sendmail(my_sender, [my_user, ], msg.as_string())
        server.quit()
        print("发送成功")
    except Exception:
        print("发送失败")
        
    





