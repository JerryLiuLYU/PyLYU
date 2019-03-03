n=int(input('输入需要比较大小的数字的个数：'))
print("请输入需要对比的数字：")
num=[2,3]
for i in range(1,n+1):
    temp=int(input('输入第 %d 个数字：'%i))            #用input输出时与C语言的取址符”&“不同为”%“
    num.append(temp)                                 #往列表中插入数字，插在末尾
print('您输入的数字为：',num)                          #可以直接输出列表
print('最大值为：',max(num))