#计算x的n次方
x = int(input('请输入x'))
n = int(input('请输入n'))
s= 1
for i in range(n):
    s = s*x
print(s)