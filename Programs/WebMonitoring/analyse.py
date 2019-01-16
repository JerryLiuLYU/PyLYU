# -*- coding: utf-8 -*-
import requests
import re
import jieba
from website import mail

def crawl(url): 
    #url = 'http://ntce.neea.edu.cn/html1/report/1803/2669-1.htm'
    headers = {"User-Agent":"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"}
    r = requests.get(url,headers)
    r.encoding = r.apparent_encoding
    with open('content.txt','w',encoding='utf-8') as f:
        f.writelines(r.text)
        print('写入成功！')
        
def Analysis(List):    
    with open('content.txt','r',encoding='utf-8') as f:
        content = str(f.readlines())    
        sub_str = re.sub(u"([^\u4e00-\u9fa5])","",content)
    words = jieba.lcut(sub_str)
    counts = {}
    for word in words:
        if len(word)==1:
            continue
        else:
            counts[word] = counts.get(word,0)+1
    items = list(counts.items())
    items.sort(key = lambda x:x[1] , reverse = True)
    l = []
    if len(items)==0:
        pass
    else:
        print('此网站高频词汇前40排名：')
        for i in range(40):
            word,counts = items[i]
            l.append(word)
            print("{0:<15}{1:>5}".format(word,counts))
    new_txt = " ".join(l)
    with open(r'content.txt','w') as f:
        f.writelines(new_txt)    
    with open(r'敏感词汇.txt','r') as f:
        mingan = f.readlines()
    mingan = (mingan[0].split(','))
    my_sender = List[1]
    pwd = List[2]
    my_user = List[3]
    for i in mingan:
        if i in words:
            print('存在敏感词汇！！')
            mail(my_sender,my_user,pwd)
            break
