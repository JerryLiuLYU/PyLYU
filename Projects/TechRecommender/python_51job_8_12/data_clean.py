import re
from python_51job_8_12.data_save import *


def data_clearing(walden, sheet_tab):
    for data in sheet_tab.find():

        # 月薪
        salary = data['salary']
        if salary.find('\u5343/\u6708') != -1:  # 千/月
            x = float(salary.split('\u5343')[0].split('-')[0])
            y = float(salary.split('\u5343')[0].split('-')[-1])
            salary = '{}-{}k'.format(x, y)
        elif salary.find('\u4e07/\u6708') != -1:  # 万/月
            x = float(salary.split('\u4e07')[0].split('-')[0]) * 10
            y = float(salary.split('\u4e07')[0].split('-')[-1]) * 10
            salary = '{}-{}k'.format(x, y)
        elif salary.find('\u4e07/\u5e74') != -1:  # 万/年
            x = float(salary.split('\u4e07')[0].split('-')[0]) * 10 / 12
            y = float(salary.split('\u4e07')[0].split('-')[-1]) * 10 / 12
            salary = '{}-{}k'.format(round(x, 2), round(y, 2))

        infom = data['information']

        if re.findall('\u7ecf\u9a8c', infom[1]):  # 地点
            location = infom[0]
        else:
            location = 'None'

        for i in infom:
            if re.findall('\u7ecf\u9a8c', i):  # 工作经验
                if len(i.split('\u5e74')[0]) != 5:
                    experience = i.split('\u5e74')[0]
                else:
                    experience = 'None'
            elif re.match('\u62db', i):  # 招聘人数
                zhao = i.index('招')
                ren = i.index('人')
                number_of_recruits = i[zhao + 1:ren]
            elif re.findall('\d+-\d+', i):  # 发布时间
                time = i

        # 学历
        education = ['本科', '大专', '硕士', '中专']
        s = set(education)
        a = set(infom)
        education = a.intersection(s)
        if education:
            education = list(education)[0]
        else:
            education = 'None'
        if data['requests'] and len(data['requests'][-1]) >= 6:
            del data['requests'][-1]
        request = list(n for a in data['requests'] for n in a)
        request = set(request)
        mongodb_data = {
            '职位':data['title'],
            '公司': data['company'],
            '月薪': salary,
            '地区': location,
            '工作经验': experience,
            '学历': education,
            '招聘人数': number_of_recruits,
            '发布时间': time,
            '需求': list(request)
        }
        sheet_tab = walden['招聘数据_clean']
        save(sheet_tab, mongodb_data)
