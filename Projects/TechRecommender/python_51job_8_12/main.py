from python_51job_8_12.crawling import *
from python_51job_8_12.data_clean import * 
from python_51job_8_12.data_save import *
import pymongo


if __name__ == '__main__':
    links = []
    for j in range(1, get_pages()+1):
        print("正在爬取第" + str(j) + "页数据...")
        html1 = get_content(j)
        links.append(get1(html1))
    print('爬取列表页完成！')
    client = pymongo.MongoClient('localhost', 27017)
    walden = client['51job']
    sheet_tab = walden['招聘数据']
    for i in links:
        for link in i:
            print(link)
            html2 = get_content2(link)
            save(sheet_tab, get3(html2))
    print('存储完成！')
    data_clearing(walden, sheet_tab)

