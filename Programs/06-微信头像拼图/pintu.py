from wxpy import *
import math
from PIL import Image
import os
# 创建头像存放文件夹
def creat():
    dir = os.getcwd() + "\\wechat\\"
    if not os.path.exists(dir):
        os.mkdir(dir)
    return dir

# 保存好友头像
def save(dir):
    # 初始化机器人，扫码登陆
    bot = Bot()
    friends = bot.friends(update=True)
    num = 0
    for friend in friends:
        friend.get_avatar(dir + '\\' + str(num) + ".jpg")
        print('好友昵称:%s' % friend.nick_name)
        num = num + 1
# 拼接头像
def joint(path):
    # 获取文件夹内头像个数
    length = len(os.listdir(path))
    # 设置画布大小
    image_size = 2560
    # 设置每个头像大小
    each_size = math.ceil(2560 / math.floor(math.sqrt(length)))
    # 计算所需各行列的头像数量
    x_lines = math.ceil(math.sqrt(length))
    y_lines = math.ceil(math.sqrt(length))
    image = Image.new('RGB', (each_size * x_lines, each_size * y_lines))
    x = 0
    y = 0
    for (root, dirs, files) in os.walk(path):
        for pic_name in files:
            # 增加头像读取不出来的异常处理
                try:
                    with Image.open(path + pic_name) as img:
                        img = img.resize((each_size, each_size))
                        image.paste(img, (x * each_size, y * each_size))
                        x += 1
                        if x == x_lines:
                            x = 0
                            y += 1
                except IOError:
                    print("头像读取失败")
    img = image.save(os.getcwd() + "/wechat.png")
    print('微信好友头像拼接完成!')
if __name__ == '__main__':
    dir = creat()
    save(dir)
    joint(dir)