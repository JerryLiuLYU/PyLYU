#随机数小游戏
import random
i=1
a=random.randint(0,100)#产生一个一定范围内的随机数
b=int(input('请输入0-100中的一个数字\n然后查看是否与电脑一样：'))
while a!=b:
    if a>b:
        print('你输入的第%d个数字小于电脑随机数字'%i)
        b=int(input('请再次输入数字：'))
    else:
        print('你输入的第%d个数字大于电脑随机数字'%i)
        b=int(input('请再次输入数字：'))
    i+=1
else:
    print("恭喜你，你第%d次输入的数字与电脑的随机数字%d一样"%(i,b))