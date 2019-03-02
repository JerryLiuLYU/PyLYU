N = int(input('输入数字：'))
print("请输入需要对比的数字：")
num = []
for i in range(1,N+1):
    temp = int(input('输入第%d个数字：'%i))
    num.append(temp)
print('您输入的数字为：'，num)
print('最大值为：',max(num))