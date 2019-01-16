import wordcloud


def generate():    
    with open('content.txt','r') as f:
        l = f.readlines()
    
    w = wordcloud.WordCloud(
            font_path = "msyh.ttc",
            width = 1000,
            height = 700,
            background_color = "white",
            max_words = 50,
            )#代表一个文本对应的词云
    w.generate(l[0]) #向WordCloud对象w中加载文本
    w.to_file("website.png")