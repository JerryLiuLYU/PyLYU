num = ["harden","lampard",3,34,234,54,21,875,3,3,]
#print(num.count(3))
#print(num.index(3))
for i in range(num.count(3)):   #获取3出现的次数
    ele_index = num.index(3)    #获取首次3出现的坐标
    num[ele_index]="3a"         #修改3位3a
    print(num)