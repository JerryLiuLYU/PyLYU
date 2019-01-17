import requests
import re
from bs4 import BeautifulSoup


base_url = 'https://search.51job.com/list/000000,000000,0000,00,9,99,python,2,{}.html'


def get_pages():
    url = base_url.format('1')
    r = requests.get(url)
    r.encoding = r.apparent_encoding
    soup = BeautifulSoup(r.text, 'html.parser')
    total = soup.select_one('.td').get_text()
    total = int(re.compile('(\d+)').search(total).group(1))
    print('共'+str(total)+'页数据')
    return total


def get_content(page):
    try:
        url = base_url.format(str(page))
        r = requests.get(url)
        r.raise_for_status()
        r.encoding = r.apparent_encoding
        return r.text
    except TimeoutError:
        return ""


def get_content2(html):
    try:
        url = html
        r = requests.get(url)
        r.raise_for_status()
        r.encoding = r.apparent_encoding
        # print(len(r.text))
        return r.text
    except TimeoutError:
        return ""


def get1(html1):
    reg = re.compile(r'class="t1 ">.*? <a target="_blank" title="(.*?)" href="(.*?)"', re.S)
    items = re.findall(reg, html1)
    urls = []
    for item in items:
        if item[1].split('.')[0] == 'https://jobs':
            urls.append(item[1])
        else:
            pass
    return urls


def get3(html2):
    soup = BeautifulSoup(html2, 'lxml')
    if soup.select('.cn strong'):
        salary = soup.select('.cn strong')[0].text
        title = soup.select('h1')[0].get('title')
        company = soup.select('.catn')[0].get('title')
        infom = []
        infos = int((len(soup.select('.msg')[0].get('title').split()) + 1) / 2)
        for i in range(infos):
            infom.append(soup.select('.msg')[0].get('title').split()[i * 2])
        request = soup.select('.bmsg.job_msg')
        requests = list(request[0].stripped_strings)
        pat = '[a-zA-Z]+'
        skill = []
        for r in requests:
            result = re.findall(pat, r)
            if result:
                skill.append(result)
        data = {
            'title': title,
            'company': company,
            'salary': salary,
            'information': infom,
            'requests': skill
        }
    else:
        pass
    return data
